package finalproject;

import DB.DBConnection;
import classes.Invoice;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ViewInvoicesController implements Initializable {

    @FXML
    private HBox backHBox;
    @FXML
    private ImageView backBTN;
    @FXML
    private Label back;
    @FXML
    private TableColumn<Invoice, Integer> idTC;
    @FXML
    private TableColumn<Invoice, Integer> order_idTC;
    @FXML
    private TableColumn<Invoice, Double> priceTC;
    @FXML
    private TableColumn<Invoice, String> dateTC;
    @FXML
    private TableView<Invoice> tableView;

    private Integer loginId = LoginController.getLoginId();
    private Connection connection;
    private Statement statement;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Connection connection = DBConnection.getConnection();
            statement = connection.createStatement();
            String selectFromOrders = "SELECT * FROM orders WHERE user_id = " + loginId;
            ResultSet ordersResult = statement.executeQuery(selectFromOrders);

            List<Integer> orderIds = new ArrayList<>();

            tableView.getItems().clear();
            while (ordersResult.next()) {
                int orderId = ordersResult.getInt("id");
                orderIds.add(orderId);
                System.out.println(orderId);
            }

            for (int orderId : orderIds) {
                String selectFromInvoices = "SELECT * FROM invoices WHERE order_id = " + orderId;
                ResultSet invoicesResult = statement.executeQuery(selectFromInvoices);

                while (invoicesResult.next()) {
                    int id = invoicesResult.getInt("id");
                    double totalPrice = invoicesResult.getDouble("total_price");
                    String date = invoicesResult.getString("date");

                    Invoice invoice = new Invoice(id, orderId, totalPrice, date);
                    tableView.getItems().add(invoice);
                }
            }

            idTC.setCellValueFactory(new PropertyValueFactory<>("id"));
            order_idTC.setCellValueFactory(new PropertyValueFactory<>("order_id"));
            priceTC.setCellValueFactory(new PropertyValueFactory<>("total_price"));
            dateTC.setCellValueFactory(new PropertyValueFactory<>("date"));
        } catch (SQLException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void backHBoxHandle(MouseEvent event) {
        try {
            Parent bar = FXMLLoader.load(getClass().getResource("bar.fxml"));
            Parent parent = FXMLLoader.load(getClass().getResource("clientDashBoard.fxml"));
            VBox root = new VBox(bar, parent);
            Scene currentScene = ((Node) event.getSource()).getScene();
            currentScene.setRoot(root);
            Stage stage = (Stage) currentScene.getWindow();
            stage.setWidth(390);
            stage.setHeight(420);
        } catch (IOException ex) {
            Logger.getLogger(ClientDashBoardController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
