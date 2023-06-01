package finalproject;

import DB.DBConnection;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChangePasswordController implements Initializable {

    @FXML
    private FlowPane flowPane;
    @FXML
    private TextField oldPasswordTF;
    @FXML
    private TextField newPasswordTF;
    @FXML
    private Button changeBTN;
    @FXML
    private HBox backHBox;
    @FXML
    private ImageView backBTN;
    @FXML
    private Label back;
    Statement stat;
    Alert alert;
    private Integer loginId = LoginController.getLoginId();
    @FXML
    private Button resetBTN;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Connection connection = DBConnection.getConnection();
            stat = connection.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(ChangePasswordController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void changeBTNHandle(ActionEvent event) throws IOException {
        try {
            String oldPassword = oldPasswordTF.getText();
            String newPassword = newPasswordTF.getText();

            ResultSet rs = stat.executeQuery("SELECT * FROM users WHERE id = '" + loginId + "'");
            if (rs.next()) {
                String currentPassword = rs.getString("password");
                if (!newPassword.trim().isEmpty() && !oldPassword.trim().isEmpty()) {
                    if (!currentPassword.equals(oldPassword)) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Incorrect Password", "The entered old password is incorrect.");
                        return;
                    }
                    if (newPassword.length() <= 8) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Invalid Password", "Password should exceed 8 characters.");
                        return;
                    }

                    String updateSql = "UPDATE users SET password = " + newPassword + " WHERE id = " + loginId;
                    stat.executeUpdate(updateSql);
                    showAlert(Alert.AlertType.INFORMATION, "Password Changed", "", "Your password has been successfully changed.");
                    oldPasswordTF.clear();
                    newPasswordTF.clear();
                    navigateToDashboard(event);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Missing Fields", "Please fill in all fields.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "User Not Found", "User with the specified ID was not found.");
            }

            rs.close();
        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Database Error", "An error occurred while changing the password.");
            ex.printStackTrace();
        }
    }

    private void navigateToDashboard(ActionEvent event) throws IOException, SQLException {
        Parent bar = FXMLLoader.load(getClass().getResource("bar.fxml"));
        Scene currentScene = ((Node) event.getSource()).getScene();
        Stage stage = (Stage) currentScene.getWindow();

        ResultSet rs = stat.executeQuery("SELECT * FROM users WHERE id = " + loginId);

        if (rs.next()) {
            int role = rs.getInt("role");

            switch (role) {
                case 0: {
                    Parent parent = FXMLLoader.load(getClass().getResource("adminDashboard.fxml"));
                    VBox root = new VBox(bar, parent);
                    currentScene.setRoot(root);
                    stage.setWidth(370);
                    stage.setHeight(460);
                    break;
                }
                case 1: {
                    Parent parent = FXMLLoader.load(getClass().getResource("clientDashBoard.fxml"));
                    VBox root = new VBox(bar, parent);
                    currentScene.setRoot(root);
                    stage.setWidth(375);
                    stage.setHeight(450);
                    break;
                }
                default:
                    break;
            }
        }

        rs.close();
    }

    @FXML
    private void backHBoxHandle(MouseEvent event) throws SQLException, IOException {
        Parent bar = FXMLLoader.load(getClass().getResource("bar.fxml"));
        Scene currentScene = ((Node) event.getSource()).getScene();
        Stage stage = (Stage) currentScene.getWindow();

        ResultSet rs = stat.executeQuery("SELECT * FROM users WHERE id = " + loginId);

        if (rs.next()) {
            int role = rs.getInt("role");

            switch (role) {
                case 0: {
                    Parent parent = FXMLLoader.load(getClass().getResource("adminDashboard.fxml"));
                    VBox root = new VBox(bar, parent);
                    currentScene.setRoot(root);
                    stage.setWidth(370);
                    stage.setHeight(460);
                    break;
                }
                case 1: {
                    Parent parent = FXMLLoader.load(getClass().getResource("clientDashBoard.fxml"));
                    VBox root = new VBox(bar, parent);
                    currentScene.setRoot(root);
                    stage.setWidth(375);
                    stage.setHeight(450);
                    break;
                }
                default:
                    break;
            }
        }

        rs.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void resetBTNHandle(ActionEvent event) {
        newPasswordTF.clear();
        oldPasswordTF.clear();
    }

}
