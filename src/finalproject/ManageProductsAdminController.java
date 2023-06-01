package finalproject;

import DB.DBConnection;
import classes.Product;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ManageProductsAdminController implements Initializable {

    @FXML
    private TextField quantityTF;
    @FXML
    private TableColumn<Product, String> nameTC;
    @FXML
    private TableColumn<Product, String> categoryTC;
    @FXML
    private TableColumn<Product, Double> priceTC;
    @FXML
    private TableColumn<Product, Integer> quantityTC;
    @FXML
    private TableColumn<Product, String> descriptionTC;
    @FXML
    private Button addBTN;
    @FXML
    private Button editBTN;
    @FXML
    private Button searchBTN;
    @FXML
    private Button deleteBTN;
    @FXML
    private Button viewBTN;
    @FXML
    private Button backBTN;
    @FXML
    private TableView<Product> tableView;
    @FXML
    private TableColumn<Product, Integer> IDTC;
    Statement statement;
    @FXML
    private TextArea descriptionTF;
    @FXML
    private ComboBox<String> productIDComboBox;
    @FXML
    private TextField nameTF;
    @FXML
    private TextField priceTF;
    @FXML
    private Button resetBTN;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Connection connection = DBConnection.getConnection();
            statement = connection.createStatement();
            IDTC.setCellValueFactory(new PropertyValueFactory<>("id"));
            quantityTC.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            nameTC.setCellValueFactory(new PropertyValueFactory<>("name"));
            categoryTC.setCellValueFactory(new PropertyValueFactory<>("category"));
            priceTC.setCellValueFactory(new PropertyValueFactory<>("price"));
            descriptionTC.setCellValueFactory(new PropertyValueFactory<>("description"));
            loadProductComboBox();
            tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    try {
                        Product selectedProduct = newSelection;
                        String productName = "Select * from products where id = " + selectedProduct.getId();
                        ResultSet executeQuery = statement.executeQuery(productName);
                        if (executeQuery.next()) {
                            String name = selectedProduct.getName();
                            double price = selectedProduct.getPrice();
                            int quantity = selectedProduct.getQuantity();
                            String description = selectedProduct.getDescription();

                            productIDComboBox.setValue(selectedProduct.getCategory());
                            nameTF.setText(name);
                            priceTF.setText(String.valueOf(price));
                            quantityTF.setText(String.valueOf(quantity));
                            descriptionTF.setText(description);
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

    private void loadProductComboBox() {
        List<String> categories = Arrays.asList(
                "Electronics",
                "Clothing",
                "Home Appliances",
                "Personal Care",
                "Sports and Fitness"
        );

        productIDComboBox.getItems().addAll(categories);
    }

    @FXML
    private void addBTNHandle(ActionEvent event) {
        try {
            String name = nameTF.getText();
            double price = Double.parseDouble(priceTF.getText());
            int quantity = Integer.parseInt(quantityTF.getText());
            String description = descriptionTF.getText();
            String selectedProduct = productIDComboBox.getValue();

            if (price <= 0 || name.isEmpty() || quantity <= 0 || description.isEmpty() || selectedProduct == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Missing Fields", "Please fill in all fields.");
                return;
            }

            String checkQuery = "SELECT * FROM products WHERE name = '" + name + "' AND category = '" + selectedProduct + "'";
            ResultSet executeQuery = statement.executeQuery(checkQuery);
            if (executeQuery.next()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Product Already Exists", "A product with the same name and category already exists.");
                return;
            }

            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation");
            confirmationAlert.setHeaderText("Adding Product");
            confirmationAlert.setContentText("Do you want to Add This Product?");
            Optional<ButtonType> result = confirmationAlert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                String insertQuery = "INSERT INTO products (name, category,price, quantity, description) VALUES ('" + name + "', '" + selectedProduct + "', " + price + "," + quantity + ",'" + description + "')";
                int rowsAffected = statement.executeUpdate(insertQuery);

                if (rowsAffected == 1) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Product Added", "The Product has been successfully added.");
                    clearFields();
                    viewBTNhandle(event);
                } else {
                    return;
                }

            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to Add Product", "An error occurred while adding the Product.");
            }

        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Database Error", "An error occurred while accessing the database.");
            ex.printStackTrace();
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input", "Please enter a valid quantity.");
        }
    }

    @FXML
    private void editBTNHandle(ActionEvent event) {
        Product selectedProduct = tableView.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No Product Selected", "Please select an Product to edit.");
            return;
        }

        try {
            String name = nameTF.getText();
            double price = Double.parseDouble(priceTF.getText());
            int quantity = Integer.parseInt(quantityTF.getText());
            String description = descriptionTF.getText();
            String productname = productIDComboBox.getValue();
            if (price <= 0 || name.isEmpty() || quantity <= 0 || description.isEmpty() || productname == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Missing Fields", "Please fill in all fields.");
                return;
            }

            String checkQuery = "SELECT * FROM products WHERE name = '" + name + "' AND category = '" + selectedProduct + "'";
            ResultSet executeQuery = statement.executeQuery(checkQuery);
            if (executeQuery.next()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Product Already Exists", "A product with the same name and category already exists.");
                return;
            }

            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation");
            confirmationAlert.setHeaderText("Update Product");
            confirmationAlert.setContentText("Do you want to update This Product?");
            Optional<ButtonType> result = confirmationAlert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                String updateQuery = "UPDATE products SET name = '" + name + "', category = '" + selectedProduct.getCategory() + "', price = " + price + ", quantity = " + quantity + ", description = '" + description + "' WHERE id = " + selectedProduct.getId();
                int rowsAffected = statement.executeUpdate(updateQuery);
                if (rowsAffected == 1) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Order Updated", "The order has been successfully updated.");
                    clearFields();
                    viewBTNhandle(event);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to Update Order", "An error occurred while updating the order.");

                }
            } else {
                return;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ManageOrdersUserController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void searchBTNHandle(ActionEvent event) {
        try {
            String category = productIDComboBox.getValue();
            String searchQuery = "SELECT * FROM products WHERE category = '" + category + "'";
            ResultSet rs = statement.executeQuery(searchQuery);

            if (rs.next()) {
                List<Product> productList = new ArrayList<>();
                do {
                    int id = rs.getInt("id");
                    int quantity = rs.getInt("quantity");
                    double price = rs.getDouble("price");
                    String name = rs.getString("name");
                    String description = rs.getString("description");

                    // Create a Product object with retrieved data
                    Product product = new Product(id, quantity, price, name, category, description);
                    productList.add(product);
                } while (rs.next());

                // Clear the table view and populate it with the search results
                tableView.getItems().clear();
                tableView.getItems().addAll(productList);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Product Not Found", "No products found for the specified category.");
                clearFields();
            }

            rs.close();
        } catch (SQLException ex) {
            ex.getMessage();
        }
    }

    @FXML
    private void viewBTNhandle(ActionEvent event) {
        //int id, int quantity, double price, String name, String category, String description
        try {
            String viewQuery = "SELECT * FROM products";
            ResultSet rs = statement.executeQuery(viewQuery);

            tableView.getItems().clear();
            while (rs.next()) {
                int id = rs.getInt("id");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                String name = rs.getString("name");
                String category = rs.getString("category");
                String description = rs.getString("description");

                Product product = new Product(id, quantity, price, name, category, description);
                tableView.getItems().add(product);
            }
        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Database Error", "An error occurred while accessing the database.");
            ex.printStackTrace();
        }
    }

    @FXML
    private void deleteBTNHandle(ActionEvent event) {
        try {
            Product selectedProduct = tableView.getSelectionModel().getSelectedItem();
            if (selectedProduct == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "No Product Selected", "Please select a product to delete.");
                return;
            }
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation");
            confirmationAlert.setHeaderText("Delete Product");
            confirmationAlert.setContentText("Do you want to delete this product?");
            Optional<ButtonType> result = confirmationAlert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                // First, delete the associated orders
                String deleteOrdersQuery = "DELETE FROM orders WHERE product_id = " + selectedProduct.getId();
                int rowsAffected = statement.executeUpdate(deleteOrdersQuery);

                if (rowsAffected >= 0) {
                    // Orders deleted successfully or no associated orders found
                    String deleteProductQuery = "DELETE FROM products WHERE id = " + selectedProduct.getId();
                    rowsAffected = statement.executeUpdate(deleteProductQuery);
                    if (rowsAffected == 1) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Product Deleted", "The product has been successfully deleted.");
                        clearFields();
                        viewBTNhandle(event);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to Delete Product", "An error occurred while deleting the product.");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to Delete Orders", "An error occurred while deleting the associated orders.");
                }
            } else {
                return;
            }

        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Database Error", "An error occurred while accessing the database.");
            ex.printStackTrace();
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
        nameTF.clear();
        priceTF.clear();
        descriptionTF.clear();
        tableView.getItems().clear();
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

    @FXML
    private void resetBTNHandle(ActionEvent event) {
        clearFields();
    }

}
