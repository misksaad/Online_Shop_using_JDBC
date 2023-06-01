package finalproject;

import DB.DBConnection;
import classes.Invoice;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

public class ManageInvoicesAdminController implements Initializable {

    @FXML
    private Button generateBTN;
    @FXML
    private Button viewBTN;
    @FXML
    private Button searchBTN;
    @FXML
    private Button deleteBTN;
    @FXML
    private Button backBTN;
    @FXML
    private TableView<Invoice> tableView;
    @FXML
    private TableColumn<Invoice, Integer> idTC;
    @FXML
    private TableColumn<Invoice, Integer> order_idTC;
    @FXML
    private TableColumn<Invoice, Double> priceTC;
    @FXML
    private TableColumn<Invoice, String> dateTC;
    @FXML
    private TextField orderIDTF;

    private Connection connection;
    private Statement statement;
    @FXML
    private Button resetBTN;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Connection connection = DBConnection.getConnection();
            statement = connection.createStatement();
            idTC.setCellValueFactory(new PropertyValueFactory<>("id"));
            order_idTC.setCellValueFactory(new PropertyValueFactory<>("order_id"));
            priceTC.setCellValueFactory(new PropertyValueFactory<>("total_price"));
            dateTC.setCellValueFactory(new PropertyValueFactory<>("date"));

        } catch (SQLException ex) {
            Logger.getLogger(ManageInvoicesAdminController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void generateBTNHandle(ActionEvent event) {
        try {
            String generateQuery = "INSERT INTO invoices (order_id, total_price, date) "
                    + "SELECT o.id, p.price * o.quantity, o.date "
                    + "FROM orders o "
                    + "JOIN products p ON o.product_id = p.id";
            int rowsAffected = statement.executeUpdate(generateQuery);
            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Invoices Generated",
                        "The invoices have been successfully generated.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to Generate Invoices",
                        "An error occurred while generating the invoices.");
            }
            viewBTNhandle(event);
        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Database Error",
                    "An error occurred while accessing the database.");
            ex.printStackTrace();
        }
    }

    @FXML
    private void viewBTNhandle(ActionEvent event) {
        try {
            String viewQuery = "SELECT * FROM invoices ";
            ResultSet rs = statement.executeQuery(viewQuery);

            tableView.getItems().clear();
            while (rs.next()) {
                int id = rs.getInt("id");
                int orderID = rs.getInt("order_id");
                double totalPrice = rs.getDouble("total_price");
                String date = rs.getString("date");

                Invoice invoice = new Invoice(id, orderID, totalPrice, date);
                tableView.getItems().add(invoice);
            }
        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Database Error",
                    "An error occurred while accessing the database.");
            ex.printStackTrace();
        }
    }

    @FXML
    private void searchBTNHandle(ActionEvent event) {
        try {
            String searchID = orderIDTF.getText();
            String searchQuery = "SELECT * FROM invoices WHERE order_id = " + searchID;
            ResultSet rs = statement.executeQuery(searchQuery);

            tableView.getItems().clear();
            while (rs.next()) {
                int id = rs.getInt("id");
                int orderID = rs.getInt("order_id");
                double totalPrice = rs.getDouble("total_price");
                String date = rs.getString("date");

                Invoice invoice = new Invoice(id, orderID, totalPrice, date);
                tableView.getItems().add(invoice);
            }
            rs.close();
        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Database Error",
                    "An error occurred while accessing the database.");
            ex.printStackTrace();
        }
    }

    @FXML
    private void deleteBTNHandle(ActionEvent event) {
        try {
            Invoice selectedInvoice = tableView.getSelectionModel().getSelectedItem();
            if (selectedInvoice == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "No Invoice Selected",
                        "Please select an invoice to delete.");
                return;
            }
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation");
            confirmationAlert.setHeaderText("Delete Invoice");
            confirmationAlert.setContentText("Do you want to delete this invoice?");
            Optional<ButtonType> result = confirmationAlert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                String deleteQuery = "DELETE FROM invoices WHERE id = " + selectedInvoice.getId();
                int rowsAffected = statement.executeUpdate(deleteQuery);
                if (rowsAffected == 1) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Invoice Deleted",
                            "The invoice has been successfully deleted.");
                    clearFields();
                    viewBTNhandle(event);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to Delete Invoice",
                            "An error occurred while deleting the invoice.");
                }
            }
        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Database Error",
                    "An error occurred while accessing the database.");
            ex.printStackTrace();
        }
    }

    @FXML
    private void backBTNHandle(ActionEvent event) throws IOException {
        try {
            Parent bar = FXMLLoader.load(getClass().getResource("bar.fxml"));
            Parent parent = FXMLLoader.load(getClass().getResource("adminDashboard.fxml"));
            VBox root = new VBox(bar, parent);
            Scene currentScene = ((Node) event.getSource()).getScene();
            currentScene.setRoot(root);
            Stage stage = (Stage) currentScene.getWindow();
            stage.setWidth(370);
            stage.setHeight(460);

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
        orderIDTF.clear();
    }

    @FXML
    private void resetBTNHandles(ActionEvent event) {
        clearFields();
    }
}
