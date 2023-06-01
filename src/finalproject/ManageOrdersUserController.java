package finalproject;

import DB.DBConnection;
import classes.Order;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;

public class ManageOrdersUserController implements Initializable {

    @FXML
    private ComboBox<String> productIDComboBox;
    @FXML
    private TextField quantityTF;
    @FXML
    private TableView<Order> tableView;
    @FXML
    private Button viewBTN;
    @FXML
    private Button addBTN;
    @FXML
    private Button searchBTN;
    @FXML
    private Button backBTN;
    @FXML
    private Button editBTN;
    @FXML
    private Button deleteBTN;

    private Integer loginId = LoginController.getLoginId();
    private Connection connection;
    private Statement statement;
    @FXML
    private DatePicker DatePicker;
    @FXML
    private TableColumn<Order, Integer> IDTC;
    @FXML
    private TableColumn<Order, Integer> productIDTC;
    @FXML
    private TableColumn<Order, Integer> clientIDTC;
    @FXML
    private TableColumn<Order, Integer> quantityTC;
    @FXML
    private TableColumn<Order, String> dateTC;
    @FXML
    private TextField orderIDTF;
    @FXML
    private Button resetBTN;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            connection = DBConnection.getConnection();
            statement = connection.createStatement();
            IDTC.setCellValueFactory(new PropertyValueFactory<>("id"));
            quantityTC.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            productIDTC.setCellValueFactory(new PropertyValueFactory<>("product_id"));
            clientIDTC.setCellValueFactory(new PropertyValueFactory<>("user_id"));
            dateTC.setCellValueFactory(new PropertyValueFactory<>("date"));
            DatePicker.setValue(LocalDate.now());
            loadProductComboBox();
            tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    try {
                        Order selectedOrder = newSelection;
                        String productName = "Select name from products where id = " + selectedOrder.getProduct_id();
                        ResultSet executeQuery = statement.executeQuery(productName);
                        if (executeQuery.next()) {
                            String productID = String.valueOf(selectedOrder.getProduct_id() + "-" + executeQuery.getString("name"));
                            String quantityText = String.valueOf(selectedOrder.getQuantity());
                            LocalDate selectedDate = LocalDate.parse(selectedOrder.getDate());

                            productIDComboBox.setValue(productID);
                            quantityTF.setText(quantityText);
                            DatePicker.setValue(selectedDate);
                        } else {
                            clearFields();
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(ManageOrdersUserController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    clearFields();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadProductComboBox() throws SQLException {
        String sql = "SELECT * FROM products";
        statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            productIDComboBox.getItems().add(id + "-" + name);
        }
    }

    @FXML
    private void addBTNHandle(ActionEvent event) throws SQLException {
        try {
            String selectedProduct = productIDComboBox.getValue();
            String quantityText = quantityTF.getText();
            LocalDate selectedDate = DatePicker.getValue();

            if (selectedProduct == null || quantityText.isEmpty() || selectedDate == null) {
                showAlert(AlertType.ERROR, "Error", "Missing Fields", "Please fill in all fields.");
                return;
            }

            if (selectedDate.isBefore(LocalDate.now())) {
                showAlert(AlertType.ERROR, "Error", "Invalid Date", "Please select a date in the future or today.");
                return;
            }

            String[] productData = selectedProduct.split("-");
            int productID = Integer.parseInt(productData[0]);
            int quantity = Integer.parseInt(quantityText);

            String sqlCheckQuantity = "SELECT quantity FROM products WHERE id = " + productID;
            ResultSet rs = statement.executeQuery(sqlCheckQuantity);
            if (rs.next()) {
                int availableQuantity = rs.getInt("quantity");
                if (quantity > availableQuantity) {
                    Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmationAlert.setTitle("Confirmation");
                    confirmationAlert.setHeaderText("Insufficient Quantity");
                    confirmationAlert.setContentText("The Quantity of the selected product is " + availableQuantity + ".\n Do you want to proceed with the available quantity?");
                    Optional<ButtonType> result = confirmationAlert.showAndWait();

                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        quantity = availableQuantity;
                    } else {
                        return;
                    }
                }
            } else {
                showAlert(AlertType.ERROR, "Error", "Product Not Found", "The selected product could not be found.");
                return;
            }
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation");
            confirmationAlert.setHeaderText("Adding Order");
            confirmationAlert.setContentText("Do you want to Add This Order?");
            Optional<ButtonType> result = confirmationAlert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                String insertQuery = "INSERT INTO orders (product_id, user_id, quantity, date) VALUES"
                        + " (" + productID + "," + loginId + "," + quantity + ",'" + selectedDate.toString() + "')";
                int executeUpdate = statement.executeUpdate(insertQuery);
                updateProductQuantity(productID, quantity);
                showAlert(AlertType.INFORMATION, "Success", "Order Added", "The order has been successfully added.");
                clearFields();
                viewBTNhandle(event);
            } else {
                return;
            }

        } catch (SQLException ex) {
            showAlert(AlertType.ERROR, "Error", "Database Error", "An error occurred while accessing the database.");
            ex.printStackTrace();
        }
    }

    private void updateProductQuantity(int productID, int quantity) throws SQLException {
        String updateQuery = "UPDATE products SET quantity = quantity - " + quantity + " WHERE id = " + productID;
        statement.executeUpdate(updateQuery);
    }

    @FXML
    private void editBTNHandle(ActionEvent event) {
        Order selectedOrder = tableView.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showAlert(AlertType.ERROR, "Error", "No Order Selected", "Please select an order to edit.");
            return;
        }
        try {
            String selectedProduct = productIDComboBox.getValue();
            String quantityText = quantityTF.getText();
            LocalDate selectedDate = DatePicker.getValue();
            if (selectedProduct == null || quantityText.isEmpty() || selectedDate == null) {
                showAlert(AlertType.ERROR, "Error", "Missing Fields", "Please fill in all fields.");
                return;
            }

            if (selectedDate.isBefore(LocalDate.now())) {
                showAlert(AlertType.ERROR, "Error", "Invalid Date", "Please select a date in the future or today.");
                return;
            }

            String[] productData = selectedProduct.split("-");
            int productID = Integer.parseInt(productData[0]);
            int quantity = Integer.parseInt(quantityText);
            String date = selectedDate.toString();

            String sqlCheckQuantity = "SELECT quantity FROM products WHERE id = " + productID;
            ResultSet rs = statement.executeQuery(sqlCheckQuantity);
            if (rs.next()) {
                int availableQuantity = rs.getInt("quantity");
                if (quantity > availableQuantity) {
                    Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmationAlert.setTitle("Confirmation");
                    confirmationAlert.setHeaderText("Insufficient Quantity");
                    confirmationAlert.setContentText("The selected product does not have enough quantity. Do you want to proceed with the available quantity?");
                    Optional<ButtonType> result = confirmationAlert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        quantity = availableQuantity;
                    } else {
                        return; // User chose to cancel the update
                    }
                }

                if (quantity > selectedOrder.getQuantity()) {
                    Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmationAlert.setTitle("Confirmation");
                    confirmationAlert.setHeaderText("Increase Quantity");
                    confirmationAlert.setContentText("Do you want to update the product quantity?");
                    Optional<ButtonType> result = confirmationAlert.showAndWait();

                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        updateProductQuantity(productID, (quantity - selectedOrder.getQuantity()));
                    } else {
                        return; // User chose to cancel the update
                    }
                } else if (quantity < selectedOrder.getQuantity()) {
                    Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmationAlert.setTitle("Confirmation");
                    confirmationAlert.setHeaderText("Decrease Quantity");
                    confirmationAlert.setContentText("Do you want to update the product quantity?");
                    Optional<ButtonType> result = confirmationAlert.showAndWait();

                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        String updateQuery = "UPDATE products SET  quantity = quantity + " + (selectedOrder.getQuantity() - quantity) + " WHERE id = " + productID;
                        statement.executeUpdate(updateQuery);
                    } else {
                        return;
                    }
                }

                String updateQuery = "UPDATE orders SET quantity = " + quantity + ", date = '" + selectedDate.toString() + "'  WHERE id = " + selectedOrder.getId();

                int rowsAffected = statement.executeUpdate(updateQuery);
                if (rowsAffected == 1) {
                    showAlert(AlertType.INFORMATION, "Success", "Order Updated", "The order has been successfully updated.");
                    clearFields();
                    viewBTNhandle(event);
                } else {
                    showAlert(AlertType.ERROR, "Error", "Failed to Update Order", "An error occurred while updating the order.");
                }

            } else {
                showAlert(AlertType.ERROR, "Error", "Product Not Found", "The selected product could not be found.");
                return;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ManageOrdersUserController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void viewBTNhandle(ActionEvent event) {
        try {
            String viewQuery = "SELECT * FROM orders WHERE user_id = " + loginId;
            ResultSet rs = statement.executeQuery(viewQuery);

            tableView.getItems().clear();
            while (rs.next()) {
                int id = rs.getInt("id");
                int productID = rs.getInt("product_id");
                int quantity = rs.getInt("quantity");
                String date = rs.getString("date");

                Order order = new Order(id, productID, loginId, quantity, date);
                tableView.getItems().add(order);
            }
        } catch (SQLException ex) {
            showAlert(AlertType.ERROR, "Error", "Database Error", "An error occurred while accessing the database.");
            ex.printStackTrace();
        }
    }

    @FXML
    private void searchBTNHandle(ActionEvent event) {
        try {
            int order_id = Integer.parseInt(orderIDTF.getText());
            String searchQuery = "SELECT * FROM orders WHERE id = " + order_id;
            ResultSet rs = statement.executeQuery(searchQuery);

            if (rs.next()) {
                int productID = rs.getInt("product_id");
                int clientID = rs.getInt("user_id");
                int quantityText = rs.getInt("quantity");
                LocalDate selectedDate = LocalDate.parse(rs.getString("date"));

                String productNameQuery = "SELECT name FROM products WHERE id = " + productID;
                ResultSet productNameResult = statement.executeQuery(productNameQuery);

                if (productNameResult.next()) {
                    String productName = productNameResult.getString("name");

                    String productIDString = productID + "-" + productName;

                    productIDComboBox.setValue(productIDString);
                    quantityTF.setText(String.valueOf(quantityText));
                    DatePicker.setValue(selectedDate);

                    Order order = new Order(order_id, productID, clientID, quantityText, selectedDate.toString());
                    tableView.getItems().clear();
                    tableView.getItems().add(order);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Product Not Found", "The product with the specified ID was not found.");
                    clearFields();
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Order Not Found", "The order with the specified ID was not found.");
                clearFields();
            }

            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(MangeOrdersAdminController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void deleteBTNHandle(ActionEvent event) {
        try {
            Order selectedOrder = tableView.getSelectionModel().getSelectedItem();
            if (selectedOrder == null) {
                showAlert(AlertType.ERROR, "Error", "No Order Selected", "Please select an order to delete.");
                return;
            }
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation");
            confirmationAlert.setHeaderText("Delete Product");
            confirmationAlert.setContentText("Do you want to delete this product?");
            Optional<ButtonType> result = confirmationAlert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                String deleteQuery = "DELETE FROM orders WHERE id = " + selectedOrder.getId();
                int rowsAffected = statement.executeUpdate(deleteQuery);
                if (rowsAffected == 1) {
                    updateProductQuantity(selectedOrder.getProduct_id(), -selectedOrder.getQuantity());
                    showAlert(AlertType.INFORMATION, "Success", "Order Deleted", "The order has been successfully deleted.");
                    clearFields();
                    viewBTNhandle(event);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to Delete Order", "An error occurred while deleting the order.");
                }
                statement.executeUpdate(deleteQuery);
            } else {
                return;
            }
        } catch (SQLException ex) {
            showAlert(AlertType.ERROR, "Error", "Database Error", "An error occurred while accessing the database.");
            ex.printStackTrace();
        }
    }

    @FXML
    private void backBTNHandle(ActionEvent event) throws IOException {
        try {
            Parent bar = FXMLLoader.load(getClass().getResource("bar.fxml"));
            Parent parent = FXMLLoader.load(getClass().getResource("clientDashBoard.fxml"));
            VBox root = new VBox(bar, parent);
            Scene currentScene = ((Node) event.getSource()).getScene();
            currentScene.setRoot(root);
            Stage stage = (Stage) currentScene.getWindow();
            stage.setWidth(360);
            stage.setHeight(420);

        } catch (IOException ex) {
            Logger.getLogger(ClientDashBoardController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void showAlert(AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        productIDComboBox.getSelectionModel().clearSelection();
        quantityTF.clear();
        DatePicker.setValue(LocalDate.now());
        orderIDTF.clear();
        tableView.getItems().clear();
    }

    @FXML
    private void resetBTNHandle(ActionEvent event) {
        clearFields();
    }
}
