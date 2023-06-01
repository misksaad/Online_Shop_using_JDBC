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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SignUpController implements Initializable {

    @FXML
    private TextField nameTF;
    @FXML
    private TextField emailTF;
    @FXML
    private TextField mobileTF;
    @FXML
    private PasswordField passwordTF;
    @FXML
    private Button RegisterBTN;
    Statement stat;
    @FXML
    private Button resetBTN;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Connection connection = DBConnection.getConnection();
            stat = connection.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(SignUpController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void RegisterBTNHandle(ActionEvent event) throws SQLException, IOException {
        String name = nameTF.getText();
        String email = emailTF.getText();
        String mobile = mobileTF.getText();
        String password = passwordTF.getText();

        if (name.isEmpty() || email.isEmpty() || mobile.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Error", "Missing Fields", "Please fill in all fields.");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert(AlertType.ERROR, "Error", "Invalid Email", "Please enter a valid email address.");
            return;
        }

        if (!isValidMobileNumber(mobile)) {
            showAlert(AlertType.ERROR, "Error", "Invalid Mobile Number", "Please enter a valid mobile number.");
            return;
        }

        if (isEmailExists(email)) {
            showAlert(AlertType.ERROR, "Error", "Email Already Exists", "The email address is already registered.");
            return;
        }

        if (password.length() <= 8) {
            showAlert(AlertType.ERROR, "Error", "Invalid Password", "Password should exceed 8 characters.");
            return;
        }

        String sql = "INSERT INTO users(name,email,mobile,password,role) VALUES ('" + name + "','" + email + "','"
                + mobile + "','" + password + "',1)";
        this.stat.executeUpdate(sql);
        showAlert(AlertType.INFORMATION, "Add Successful", "", "Record added successfully.");
        Parent bar = FXMLLoader.load(getClass().getResource("bar.fxml"));
        Parent parent = FXMLLoader.load(getClass().getResource("clientDashBoard.fxml"));
        VBox root = new VBox(bar, parent);
        Scene currentScene = ((Node) event.getSource()).getScene();
        currentScene.setRoot(root);
        Stage stage = (Stage) currentScene.getWindow();
        stage.setWidth(365);
        stage.setHeight(440);
    }

    private boolean isEmailExists(String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE email = '" + email + "'";
        ResultSet resultSet = this.stat.executeQuery(query);
        resultSet.next();
        int count = resultSet.getInt(1);
        return count > 0;
    }

    private boolean isValidMobileNumber(String mobile) {
        return mobile.matches("^(056|059)\\d{7}$") && mobile.length() == 10 && mobile.matches("\\d+");
    }

    private boolean isValidEmail(String email) {
        return email.endsWith(".com");
    }

    private void showAlert(AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void resetBTNHandle(ActionEvent event) {
        nameTF.clear();
        emailTF.clear();
        mobileTF.clear();
        passwordTF.clear();
    }
}
