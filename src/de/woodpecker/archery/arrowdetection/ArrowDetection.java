package de.woodpecker.archery.arrowdetection;

import de.woodpecker.archery.arrowdetection.ui.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.opencv.core.Core;


public class ArrowDetection extends Application {
    public static void Main(String[] args) {
        // load the native OpenCV library
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        startMain(primaryStage);
    }

    private void startMain(Stage primaryStage) {
        try {
            // load the FXML resource
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ui/Main.fxml"));
            BorderPane root = loader.load();
            MainController mainController = loader.getController();

            // create and style a scene
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            // create the stage with the given title and the previously created
            // scene
            primaryStage.setTitle("Calibration");
            primaryStage.setScene(scene);

            primaryStage.setOnCloseRequest((WindowEvent event1) -> mainController.doBeforeShutDown());

            // show the GUI
            primaryStage.show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
