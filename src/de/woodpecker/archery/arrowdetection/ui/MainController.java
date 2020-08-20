package de.woodpecker.archery.arrowdetection.ui;

import de.woodpecker.archery.arrowdetection.cameraprocessors.CameraProcessorBase;
import de.woodpecker.archery.arrowdetection.detectionserver.Server;
import de.woodpecker.archery.arrowdetection.imageprocessors.ImageProcessor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;


public class MainController implements ApplicationController, VisualizationController {
    private final CameraProcessorBase cameraProcessor = new CameraProcessorBase();

    @FXML
    private Button showTest;
    @FXML
    private Button startServer;
    private AnalyserController currentController;
    @FXML
    private BorderPane mainArea;
    @FXML
    private MenuItem menuItemCameraCalibration;
    @FXML
    private MenuItem menuItemPerspectivCorrection;
    @FXML
    private MenuItem menuItemRegionOfInterest;
    @FXML
    private MenuItem menuItemBackgroundRemover;
    @FXML
    private MenuItem menuItemOptimisation;
    @FXML
    private ImageView cameraPreview;
    @FXML
    private Pane imageContainer;

    @FXML
    public void initialize() {
        cameraPreview.fitHeightProperty().bind(imageContainer.heightProperty());
        cameraPreview.fitWidthProperty().bind(imageContainer.widthProperty());

        cameraPreview.fitHeightProperty().addListener(observable -> centerImage());
        cameraPreview.fitWidthProperty().addListener(observable -> centerImage());

        cameraProcessor.setVisualizationController(this);

        refreshMenu();
    }

    public void centerImage() {
        Image img = cameraPreview.getImage();
        if (img != null) {
            double w = 0;
            double h = 0;

            double ratioX = cameraPreview.getFitWidth() / img.getWidth();
            double ratioY = cameraPreview.getFitHeight() / img.getHeight();

            double reducCoeff = 0;
            if (ratioX >= ratioY) {
                reducCoeff = ratioY;
            } else {
                reducCoeff = ratioX;
            }

            w = img.getWidth() * reducCoeff;
            h = img.getHeight() * reducCoeff;

            cameraPreview.setX((cameraPreview.getFitWidth() - w) / 2);
            cameraPreview.setY((cameraPreview.getFitHeight() - h) / 2);

        }
    }

    @FXML
    public void doBeforeShutDown() {
        if (AnalyserController.settings.getActiveVideoInput() != null)
            AnalyserController.settings.getActiveVideoInput().save();
        cameraProcessor.stopCamera();
    }


    @FXML
    private void loadCameraSelector() {
        changeView("Settings/CameraSelector.fxml");
    }

    @FXML
    private void loadCalibration() {
        changeView("Settings/Calibration.fxml");
    }

    @FXML
    private void loadRegionOfInterest() {
        changeView("Settings/RegionOfInterest.fxml");
    }

    @FXML
    private void loadBackgroundRemover() {
        changeView("Settings/BackgroundRemover.fxml");
    }

    @FXML
    private void loadArrowDetectionSettings() {
        changeView("Settings/ArrowDetection.fxml");
    }

    @FXML
    private void loadPerspectiveCorrection() {
        changeView("Settings/PerspectiveCorrection.fxml");
    }

    @FXML
    private void showTest() {
        showWindow("test/HitArea.fxml");
    }

    private void changeView(String view) {
        // Prüfen ob die alte Form geschlossen werdn kann
        if (currentController != null && !currentController.canCloseWindow())
            return;

        closeCurrentWindow();

        // laden der neuen Form
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(view)
        );
        try {
            // Die entsprechende Form laden und den Analyser durchreichen
            Pane newLayout = loader.load();
            currentController = loader.getController();
            //currentController.setAnalyserSettings(settings);
            currentController.setApplicationController(this);
            currentController.setResultVisualizationController(this);
            mainArea.setCenter(newLayout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showWindow(String view) {
        // laden der neuen Form
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(view)
        );
        try {
            // Die entsprechende Form laden und den Analyser durchreichen
            Pane newLayout = loader.load();
            currentController = loader.getController();
            currentController.setApplicationController(this);
            currentController.setResultVisualizationController(this);

            Stage stage = new Stage();
            stage.setTitle("Treffererkennung Test");
            stage.setScene(new Scene(newLayout, 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeCurrentWindow() {
        mainArea.setCenter(null);
        stopPreview();
    }

    @Override
    public void afterSafe() {
        refreshMenu();
    }

    private void refreshMenu() {
        boolean noCameraSelected = (AnalyserController.settings.getActiveVideoInput() == null);

        menuItemCameraCalibration.setDisable(noCameraSelected);
        menuItemPerspectivCorrection.setDisable(noCameraSelected);
        menuItemRegionOfInterest.setDisable(noCameraSelected);
        menuItemBackgroundRemover.setDisable(noCameraSelected);
        menuItemOptimisation.setDisable(noCameraSelected);
        startServer.setDisable(noCameraSelected);
        showTest.setDisable(noCameraSelected);
    }

    @Override
    public void changeImage(Image image) {
        cameraPreview.setImage(image);
    }

    public void startPreview() {
        cameraProcessor.restartCameraWithNewSettings();
    }

    public void stopPreview() {
        cameraProcessor.stopCamera();
    }

    @Override
    public void setImageProcessor(ImageProcessor imageProcessor) {
        cameraProcessor.setImageProcessor(imageProcessor);
    }

    public void startServer(ActionEvent actionEvent) {
        cameraProcessor.stopCamera();

        Server server = new Server(AnalyserController.settings);
        server.start();
    }
}
