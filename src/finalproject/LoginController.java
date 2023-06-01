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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginController extends Stage implements Initializable {

    @FXML
    private TextField emailTF;
    @FXML
    private PasswordField passwordTF;
    @FXML
    private Button signInBTN;
    @FXML
    private Button signUpBTN;
    @FXML
    private FlowPane flowPane;
    Statement stat;
    Alert alert;
    private static Integer loginId;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Connection connection = DBConnection.getConnection();
            stat = connection.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void signInBTNHandle(ActionEvent event) throws IOException, SQLException {
        Parent bar = FXMLLoader.load(getClass().getResource("bar.fxml"));
        Scene currentScene = ((Node) event.getSource()).getScene();
        Stage stage = (Stage) currentScene.getWindow();

        String email = emailTF.getText();
        String password = passwordTF.getText();
        if (!email.isEmpty() && !password.isEmpty()) {
            ResultSet rs = stat.executeQuery("SELECT * FROM users where email = '" + email + "' and"
                    + " password = '" + password + "'");
            if (rs.next()) {
                loginId = rs.getInt("id");
                switch (rs.getInt("role")) {
                    case 0: {
                        Parent parent = FXMLLoader.load(getClass().getResource("adminDashboard.fxml"));
                        VBox root = new VBox(bar, parent);
                        currentScene.setRoot(root);
                        stage.setWidth(365);
                        stage.setHeight(460);
                        break;
                    }
                    case 1: {
                        Parent parent = FXMLLoader.load(getClass().getResource("clientDashBoard.fxml"));
                        VBox root = new VBox(bar, parent);
                        currentScene.setRoot(root);
                        stage.setWidth(365);
                        stage.setHeight(450);
                        break;
                    }
                }
            } else {
                emailTF.clear();
                passwordTF.clear();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Sign In Failed");
                alert.setHeaderText(null);
                alert.setContentText("Invalid email or password. Please try again.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Missing Fields");
            alert.setContentText("Please fill in all fields.");
            alert.showAndWait();
        }
    }

    @FXML
    private void signUpBTNHandle(ActionEvent event) throws IOException {
        Parent bar = FXMLLoader.load(getClass().getResource("bar.fxml"));
        Parent parent = FXMLLoader.load(getClass().getResource("signUp.fxml"));
        VBox root = new VBox(bar, parent);
        Scene currentScene = ((Node) event.getSource()).getScene();
        currentScene.setRoot(root);
        Stage stage = (Stage) currentScene.getWindow();
        stage.setWidth(365);
        stage.setHeight(550);
    }

    public static Integer getLoginId() {
        return loginId;
    }
}
