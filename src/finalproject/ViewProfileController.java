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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ViewProfileController implements Initializable {

    @FXML
    private HBox backHBox;
    @FXML
    private ImageView backBTN;
    @FXML
    private Label back;
    @FXML
    private TextField nameTF;
    @FXML
    private TextField emailTF;
    @FXML
    private TextField mobileTF;
    private String email;
    private Statement stat;
    private Integer loginId = LoginController.getLoginId();
    @FXML
    private FlowPane flowPane;

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
}
