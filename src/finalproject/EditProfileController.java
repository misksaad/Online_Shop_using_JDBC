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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EditProfileController implements Initializable {

    @FXML
    private HBox backHBox;
    @FXML
    private ImageView backBTN;
    @FXML
    private Label back;
    @FXML
    private Button editBTN;
    @FXML
    private TextField nameTF;
    @FXML
    private TextField emailTF;
    @FXML
    private TextField mobileTF;
    private Statement stat;
    private Integer loginId = LoginController.getLoginId();
    private Alert alert;
    @FXML
    private Button resetBTN;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Connection connection = DBConnection.getConnection();
            stat = connection.createStatement();
            ResultSet rs = stat.executeQuery("SELECT * FROM users where id = '" + loginId + "'");
            while (rs.next()) {
                nameTF.setText(rs.getString("name"));
                emailTF.setText(rs.getString("email"));
                mobileTF.setText(rs.getString("mobile"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ViewProfileController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void backHBoxHandle(MouseEvent event) {
        try {
            Parent bar = FXMLLoader.load(getClass().getResource("bar.fxml"));
            Parent parent = FXMLLoader.load(getClass().getResource("profileScreen.fxml"));
            VBox root = new VBox(bar, parent);
            Scene currentScene = ((Node) event.getSource()).getScene();
            currentScene.setRoot(root);
            Stage stage = (Stage) currentScene.getWindow();
            stage.setWidth(360);
            stage.setHeight(370);

        } catch (IOException ex) {
            Logger.getLogger(ClientDashBoardController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void editBTNHandle(ActionEvent event) {
        try {
            String name = nameTF.getText().trim();
            String email = emailTF.getText().trim();
            String mobile = mobileTF.getText().trim();

            if (name.isEmpty() || email.isEmpty() || mobile.isEmpty()) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Validation Error");
                alert.setHeaderText(null);
                alert.setContentText("Please fill in all the fields");
                alert.showAndWait();
                return;
            }

            if (!isValidMobileNumber(mobile)) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Validation Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a valid mobile number");
                alert.showAndWait();
                return;
            }

            if (!isValidEmail(email)) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Validation Error");
                alert.setHeaderText(null);
                alert.setContentText("Invalid email format... \n"
                        + "Please enter a valid email address ending with '.com'");
                alert.showAndWait();
                return;
            }

            String updateQuery = "UPDATE users SET name = '" + name + "', email = '" + email
                    + "', mobile = '" + mobile + "' WHERE id = " + loginId;
            int rowsAffected = stat.executeUpdate(updateQuery);

            if (rowsAffected > 0) {
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Profile Update");
                alert.setHeaderText(null);
                alert.setContentText("Your profile has been updated successfully");
                alert.showAndWait();

                Parent bar = FXMLLoader.load(getClass().getResource("bar.fxml"));
                Parent parent = FXMLLoader.load(getClass().getResource("viewProfile.fxml"));
                VBox root = new VBox(bar, parent);
                Scene currentScene = ((Node) event.getSource()).getScene();
                currentScene.setRoot(root);
                Stage stage = (Stage) currentScene.getWindow();
                stage.setWidth(365);
                stage.setHeight(430);
            } else {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Profile Update Failed");
                alert.setHeaderText(null);
                alert.setContentText("Failed to update your profile. Please try again");
                alert.showAndWait();
            }
        } catch (SQLException ex) {
            Logger.getLogger(EditProfileController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EditProfileController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean isValidMobileNumber(String mobile) {
        return mobile.matches("^(056|059)\\d{7}$") && mobile.length() == 10 && mobile.matches("\\d+");
    }

    private boolean isValidEmail(String email) {
        return email.endsWith(".com");
    }

    @FXML
    private void resetBTNHandle(ActionEvent event) {
        nameTF.clear();
        emailTF.clear();
        mobileTF.clear();
    }
}
