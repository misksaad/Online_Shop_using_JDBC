package finalproject;

import DB.DBConnection;
import classes.User;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MangeClientsAdminController implements Initializable {

    @FXML
    private TableView<User> tableView;
    @FXML
    private TableColumn<User, Integer> IDTC;
    @FXML
    private Button viewBTN;
    @FXML
    private Button searchBTN;
    @FXML
    private Button backBTN;
    private Connection connection;
    private Statement statement;
    @FXML
    private TextField nameTF;
    @FXML
    private TableColumn<User, String> nameTC;
    @FXML
    private TableColumn<User, String> emailTC;
    @FXML
    private TableColumn<User, String> mobileTC;
    @FXML
    private TableColumn<User, String> passwordTC;
    @FXML
    private Button deleteBTN;
    @FXML
    private Button resetBTN;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Connection connection = DBConnection.getConnection();
            statement = connection.createStatement();
            IDTC.setCellValueFactory(new PropertyValueFactory<>("id"));
            nameTC.setCellValueFactory(new PropertyValueFactory<>("name"));
            emailTC.setCellValueFactory(new PropertyValueFactory<>("email"));
            mobileTC.setCellValueFactory(new PropertyValueFactory<>("mobile"));
            passwordTC.setCellValueFactory(new PropertyValueFactory<>("password"));
            tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    try {
                        User selectedUser = newSelection;
                        String productName = "Select id,name from users";
                        ResultSet executeQuery = statement.executeQuery(productName);
                        if (executeQuery.next()) {
                            String clientID = String.valueOf(executeQuery.getInt("id"));
                            String name = selectedUser.getName();
                            String email = selectedUser.getEmail();
                            String password = selectedUser.getPassword();
                            String mobile = selectedUser.getMobile();
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

    @FXML
    private void viewBTNhandle(ActionEvent event) {
        try {
            String viewQuery = "SELECT * FROM users WHERE role = 1";
            ResultSet rs = statement.executeQuery(viewQuery);

            tableView.getItems().clear();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String password = rs.getString("password");
                String mobile = rs.getString("mobile");
                String email = rs.getString("email");

                User user = new User(id, 1, name, email, mobile, password);
                tableView.getItems().add(user);
            }
        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Database Error", "An error occurred while accessing the database.");
            ex.printStackTrace();
        }
    }

    @FXML
    private void searchBTNHandle(ActionEvent event) {
        try {
            String searchName = nameTF.getText();
            String viewQuery = "SELECT * FROM users WHERE name = '" + searchName + "' and role = 1";
            ResultSet rs = statement.executeQuery(viewQuery);

            tableView.getItems().clear();
            List<User> userList = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String password = rs.getString("password");
                String mobile = rs.getString("mobile");
                String email = rs.getString("email");

                User user = new User(id, 1, name, email, mobile, password);
                tableView.getItems().add(user);

                userList.add(user);
            }

            if (!userList.isEmpty()) {
                tableView.getItems().clear();
                tableView.getItems().addAll(userList);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "No User Found", "No User found for the specified name.");
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
        tableView.getItems().clear();
        nameTF.clear();
    }

    @FXML
    private void deleteHandle(ActionEvent event) throws SQLException {
        try {
            User selectedProduct = tableView.getSelectionModel().getSelectedItem();
            if (selectedProduct == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "No user Selected", "Please select a user to delete.");
                return;
            }
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation");
            confirmationAlert.setHeaderText("Delete User");
            confirmationAlert.setContentText("Do you want to delete this user?");
            Optional<ButtonType> result = confirmationAlert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                String deleteProductQuery = "DELETE FROM users WHERE id = " + selectedProduct.getId();
                int executeUpdate = statement.executeUpdate(deleteProductQuery);
                if (executeUpdate == 1) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "User Deleted", "The User has been successfully deleted.");
                    clearFields();
                    viewBTNhandle(event);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to Delete User", "An error occurred while deleting the product.");
                }
            } else {
                return;
            }

        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Database Error", "An error occurred while accessing the database.");
            ex.printStackTrace();
        }
    }

    @FXML
    private void resetBTNHandle(ActionEvent event) {
        clearFields();
    }

}
