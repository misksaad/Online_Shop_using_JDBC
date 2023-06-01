package finalproject;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FinalProject extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent bar = FXMLLoader.load(getClass().getResource("bar.fxml"));
        Parent parent = FXMLLoader.load(getClass().getResource("login.fxml"));
        VBox root = new VBox(bar, parent);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
        primaryStage.setTitle("Orders Desktop Application");
        primaryStage.setWidth(365);
        primaryStage.setHeight(440);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
