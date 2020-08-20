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

    /**
     * The main class for a JavaFX application. It creates and handle the main
     * window with its resources (style, graphics, etc.).
     * <p>
     * This application apply the Canny filter to the camera video stream or try
     * to remove a uniform background with the erosion and dilation operators.
     *
     * @author <a href="mailto:luigi.derussis@polito.it">Luigi De Russis</a>
     * @author <a href="mailto:alberto.cannavo@polito.it">Alberto Cannavò</a>
     * @version 2.0 (2017-03-10)
     * @since 1.0 (2013-12-20)
     */
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

            // set a whitesmoke background
            //root.setStyle("-fx-background-color: whitesmoke;");
            // create and style a scene
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            // create the stage with the given title and the previously created
            // scene
            primaryStage.setTitle("Calibration");
            primaryStage.setScene(scene);

            primaryStage.setOnCloseRequest((WindowEvent event1) -> {
                mainController.doBeforeShutDown();
            });

            // show the GUI
            primaryStage.show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
