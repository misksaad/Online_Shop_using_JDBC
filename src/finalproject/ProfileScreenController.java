package finalproject;

import java.io.IOException;
import java.net.URL;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class ProfileScreenController implements Initializable {

    @FXML
    private ImageView viewProfileBTN;
    @FXML
    private Label viewProfile;
    @FXML
    private ImageView editProfileBTN;
    @FXML
    private Label editProfile;
    @FXML
    private ImageView backBTN;
    @FXML
    private Label back;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void viewProfileHBox(MouseEvent event) {
        try {
            Parent bar = FXMLLoader.load(getClass().getResource("bar.fxml"));
            Parent parent = FXMLLoader.load(getClass().getResource("viewProfile.fxml"));
            VBox root = new VBox(bar, parent);
            Scene currentScene = ((Node) event.getSource()).getScene();
            currentScene.setRoot(root);
            Stage stage = (Stage) currentScene.getWindow();
            stage.setWidth(365);
            stage.setHeight(430);
        } catch (IOException ex) {
            Logger.getLogger(ClientDashBoardController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void editProfileHBox(MouseEvent event) {
        try {
            Parent bar = FXMLLoader.load(getClass().getResource("bar.fxml"));
            Parent parent = FXMLLoader.load(getClass().getResource("editProfile.fxml"));
            VBox root = new VBox(bar, parent);
            Scene currentScene = ((Node) event.getSource()).getScene();
            currentScene.setRoot(root);
            Stage stage = (Stage) currentScene.getWindow();
            stage.setWidth(365);
            stage.setHeight(430);
        } catch (IOException ex) {
            Logger.getLogger(ClientDashBoardController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void backHBox(MouseEvent event) {
        try {
            Parent bar = FXMLLoader.load(getClass().getResource("bar.fxml"));
            Parent parent = FXMLLoader.load(getClass().getResource("clientDashBoard.fxml"));
            VBox root = new VBox(bar, parent);
            Scene currentScene = ((Node) event.getSource()).getScene();
            currentScene.setRoot(root);
            Stage stage = (Stage) currentScene.getWindow();
            stage.setWidth(360);
            stage.setHeight(420);
        } catch (IOException ex) {
            Logger.getLogger(ClientDashBoardController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
}
