package finalproject;

import DB.DBConnection;
import classes.Order;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MangeOrdersAdminController implements Initializable {

    @FXML
    private ComboBox<String> productIDComboBox;
    @FXML
    private ComboBox<String> clientIDComboBox;
    @FXML
    private TextField quantityTF;
    @FXML
    private DatePicker DatePicker;
    @FXML
    private TableView<Order> tableView;
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
    private Button addBTN;
    @FXML
    private Button viewBTN;
    @FXML
    private Button searchBTN;
    @FXML
    private Button backBTN;

    private Integer loginId = LoginController.getLoginId();
    private Connection connection;
    private Statement statement;
    @FXML
    private TextField clientIDTF;
    @FXML
    private Button resetBTN;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Connection connection = DBConnection.getConnection();
            statement = connection.createStatement();
            IDTC.setCellValueFactory(new PropertyValueFactory<>("id"));
            quantityTC.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            productIDTC.setCellValueFactory(new PropertyValueFactory<>("product_id"));
            clientIDTC.setCellValueFactory(new PropertyValueFactory<>("user_id"));
            dateTC.setCellValueFactory(new PropertyValueFactory<>("date"));
            DatePicker.setValue(LocalDate.now());
            loadClientsComboBox();
            loadProductComboBox();
            tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    try {
                        Order selectedOrder = newSelection;
                        String productName = "Select id,name from products";
                        ResultSet executeQuery = statement.executeQuery(productName);
                        if (executeQuery.next()) {
                            String productID = String.valueOf(executeQuery.getInt("id") + "-" + executeQuery.getString("name"));
                            String clientID = String.valueOf(selectedOrder.getUser_id());
                            String quantityText = String.valueOf(selectedOrder.getQuantity());
                            LocalDate selectedDate = LocalDate.parse(selectedOrder.getDate());

                            productIDComboBox.setValue(productID);
                            quantityTF.setText(quantityText);
                            DatePicker.setValue(selectedDate);
                            clientIDComboBox.setValue(clientID);
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
        connection = DBConnection.getConnection();
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            productIDComboBox.getItems().add(id + "-" + name);
        }
    }

    private void loadClientsComboBox() throws SQLException {
        String sql = "SELECT * FROM users WHERE role = 1";
        connection = DBConnection.getConnection();
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            clientIDComboBox.getItems().add(id + "-" + name);
        }
    }

    @FXML
    private void addBTNHandle(ActionEvent event) {
        try {
            String productIDString = productIDComboBox.getValue();
            String[] productIDParts = productIDString.split("-");
            int productID = Integer.parseInt(productIDParts[0]);

            String clientIDString = clientIDComboBox.getValue();
            String[] clientIDParts = clientIDString.split("-");
            int clientID = Integer.parseInt(clientIDParts[0]);

            int quantity = 0;
            if (!quantityTF.getText().isEmpty()) {
                quantity = Integer.parseInt(quantityTF.getText());
                if (quantity <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid Quantity", "Quantity must be a positive number.");
                    return;
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid Quantity", "Please enter a valid quantity.");
                return;
            }

            LocalDate date = DatePicker.getValue();

            String checkQuantityQuery = "SELECT quantity FROM products WHERE id = " + productID;
            ResultSet resultSet = statement.executeQuery(checkQuantityQuery);
            if (resultSet.next()) {
                int availableQuantity = resultSet.getInt("quantity");
                if (quantity > availableQuantity) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Insufficient Quantity", "The ordered quantity exceeds the available quantity.");
                    return;
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Product Not Found", "The selected product could not be found.");
                return;
            }
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation");
            confirmationAlert.setHeaderText("Adding Order");
            confirmationAlert.setContentText("Do you want to Add This Order?");
            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                String insertQuery = "INSERT INTO orders (product_id, user_id, quantity, date) VALUES ("
                        + productID + ", " + clientID + ", " + quantity + ", '" + date.toString() + "')";
                statement.executeUpdate(insertQuery);

                String updateQuery = "UPDATE products SET quantity = quantity - " + quantity + " WHERE id = " + productID;
                statement.executeUpdate(updateQuery);

                showAlert(Alert.AlertType.INFORMATION, "Success", "Order Added", "The order has been added successfully.");

                clearFields();
            } else {
                return;
            }
        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Database Error", "An error occurred while accessing the database.");
            ex.printStackTrace();
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input", "Please enter a valid quantity.");
        }
    }

    @FXML
    private void viewBTNhandle(ActionEvent event) {
        try {
            String viewQuery = "SELECT * FROM orders ";
            ResultSet rs = statement.executeQuery(viewQuery);

            tableView.getItems().clear();
            while (rs.next()) {
                int id = rs.getInt("id");
                int productID = rs.getInt("product_id");
                int user_id = rs.getInt("user_id");
                int quantity = rs.getInt("quantity");
                String date = rs.getString("date");

                Order order = new Order(id, productID, user_id, quantity, date);
                tableView.getItems().add(order);
            }
        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Database Error", "An error occurred while accessing the database.");
            ex.printStackTrace();
        }
    }

    @FXML
    private void searchBTNHandle(ActionEvent event) {
        try {
            int client_id = Integer.parseInt(clientIDTF.getText());
            String searchQuery = "SELECT * FROM orders WHERE user_id = " + client_id;
            ResultSet rs = statement.executeQuery(searchQuery);

            List<Order> orderList = new ArrayList<>();

            while (rs.next()) {
                int orderId = rs.getInt("id");
                int productID = rs.getInt("product_id");
                int clientID = rs.getInt("user_id");
                int quantityText = rs.getInt("quantity");
                LocalDate selectedDate = LocalDate.parse(rs.getString("date"));

                Order order = new Order(orderId, productID, clientID, quantityText, selectedDate.toString());
                orderList.add(order);
            }

            if (!orderList.isEmpty()) {
                tableView.getItems().clear();
                tableView.getItems().addAll(orderList);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "No Orders Found", "No orders found for the specified client ID.");
                clearFields();
            }

            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(MangeOrdersAdminController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void backBTNHandle(ActionEvent event) {
        try {
            Parent bar = FXMLLoader.load(getClass().getResource("bar.fxml"));
            Parent parent = FXMLLoader.load(getClass().getResource("adminDashboard.fxml"));
            VBox root = new VBox(bar, parent);
            Scene currentScene = ((Node) event.getSource()).getScene();
            currentScene.setRoot(root);
            Stage stage = (Stage) currentScene.getWindow();
            stage.setWidth(365);
            stage.setHeight(450);

        } catch (IOException ex) {
            Logger.getLogger(ClientDashBoardController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        productIDComboBox.getSelectionModel().clearSelection();
        quantityTF.clear();
        DatePicker.setValue(null);
        tableView.getItems().clear();
    }

    @FXML
    private void resetBTNHandle(ActionEvent event) {
        clearFields();
    }
}
