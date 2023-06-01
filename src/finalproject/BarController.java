package finalproject;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class BarController implements Initializable {

    @FXML
    private MenuItem exit;
    @FXML
    private MenuItem TimesNewRomanFF;
    @FXML
    private MenuItem ArialFF;
    @FXML
    private MenuItem ForteFF;
    @FXML
    private MenuItem tenfontSize;
    @FXML
    private MenuItem sixteenfontSize;
    @FXML
    private MenuItem twentyFourfontSize;
    @FXML
    private MenuItem BlackkBGC;
    @FXML
    private MenuItem grayBGC;
    @FXML
    private MenuItem SilverBGC;
    @FXML
    private MenuItem TurquoiseBGC;
    @FXML
    private MenuItem whiteBGC;
    @FXML
    private MenuItem aboutApp;
    @FXML
    private FlowPane flowPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void exitHandle(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        Stage stage = (Stage) menuItem.getParentPopup().getOwnerWindow();
        stage.close();
    }

    @FXML
    private void ArialFFHandle(ActionEvent event) {
        flowPane.setStyle("-fx-font-family: 'Arial';");
        flowPane.getParent().setStyle(flowPane.getStyle() + flowPane.getParent().getStyle()
                + "-fx-font-family: 'Arial';");
    }

    @FXML
    private void ForteFFHandle(ActionEvent event) {
        flowPane.setStyle("-fx-font-family: Forte;");
        flowPane.getParent().setStyle(flowPane.getStyle() + flowPane.getParent().getStyle()
                + "-fx-font-family: Forte;");
    }

    @FXML
    private void tenfontSizeHandle(ActionEvent event) {
        flowPane.setStyle("-fx-font-size: 10px;");
        flowPane.getParent().setStyle(flowPane.getStyle() + flowPane.getParent().getStyle()
                + "-fx-font-size: 10px;");
    }

    @FXML
    private void sixteenfontSizeHandle(ActionEvent event) {
        flowPane.setStyle("-fx-font-size: 16px;");
        flowPane.getParent().setStyle(flowPane.getStyle() + flowPane.getParent().getStyle()
                + "-fx-font-size: 16px;");
    }

    @FXML
    private void BlackkBGCHandle(ActionEvent event) {
        flowPane.setStyle("-fx-background-color: black;");
        flowPane.getParent().setStyle(flowPane.getStyle() + flowPane.getParent().getStyle()
                + "-fx-background-color: black;");
    }

    @FXML
    private void grayBGCHandle(ActionEvent event) {
        flowPane.setStyle("-fx-background-color: gray;");
        flowPane.getParent().setStyle(flowPane.getStyle() + flowPane.getParent().getStyle()
                + "-fx-background-color: gray;");
    }

    @FXML
    private void SilverBGCHandle(ActionEvent event) {
        flowPane.setStyle("-fx-background-color: silver;");
        flowPane.getParent().setStyle(flowPane.getStyle() + flowPane.getParent().getStyle()
                + "-fx-background-color: silver;");
    }

    @FXML
    private void TurquoiseBGCHandle(ActionEvent event) {
        flowPane.setStyle("-fx-background-color: turquoise;");
        flowPane.getParent().setStyle(flowPane.getStyle() + flowPane.getParent().getStyle()
                + "-fx-background-color: turquoise;");
    }

    @FXML
    private void whiteBGCHandle(ActionEvent event) {
        flowPane.setStyle("-fx-background-color: white;");
        flowPane.getParent().setStyle(flowPane.getStyle() + flowPane.getParent().getStyle()
                + "-fx-background-color: white;");
    }

    @FXML
    private void aboutAppHandle(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Orders Desktop Application");
        alert.setHeaderText(null);
        alert.setContentText("The Orders Desktop Application is a user-friendly application "
                + "designed to manage and track orders efficiently. "
                + "It provides features such as order creation, editing, and deletion."
                + " With a simple and intuitive interface, users can easily navigate through the application"
                + " and perform various order-related tasks. For any further assistance, please contact our"
                + " support team at misk.s.ashoor@gmail.com");
        alert.showAndWait();
    }

    @FXML
    private void twentyFourfontSizeHandle(ActionEvent event) {
        flowPane.setStyle("-fx-font-size: 24px;");
        flowPane.getParent().setStyle(flowPane.getStyle() + flowPane.getParent().getStyle()
                + "-fx-font-size: 24px;");
    }

    @FXML
    private void TimesNewRomanFFHandle(ActionEvent event) {
        flowPane.setStyle("-fx-font-family: 'Times New Roman';");
        flowPane.getParent().setStyle(flowPane.getStyle() + flowPane.getParent().getStyle()
                + "-fx-font-family: 'Times New Roman';");
    }
}
