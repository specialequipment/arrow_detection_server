package de.woodpecker.archery.arrowdetection.ui.settings;

import de.woodpecker.archery.arrowdetection.imageprocessors.CameraCalibrationImageProcessor;
import de.woodpecker.archery.arrowdetection.imageprocessors.DisplayCameraCalibrationImageProcessor;
import de.woodpecker.archery.arrowdetection.imageprocessors.ImageProcessor;
import de.woodpecker.archery.arrowdetection.ui.AnalyserController;
import de.woodpecker.archery.arrowdetection.ui.VisualizationController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * The controller associated to the only view of our application. The
 * application logic is implemented here. It handles the button for
 * starting/stopping the camera, the acquired video stream, the relative
 * controls and the overall calibration process.
 *
 * @author <a href="mailto:luigi.derussis@polito.it">Luigi De Russis</a>
 * @since 2013-11-20
 */

public class CalibrationController extends AnalyserController implements VisualizationController {
    // FXML buttons
    @FXML
    private Button cameraButton;
    @FXML
    private Button applyButton;
    @FXML
    private Button snapshotButton;
    // the FXML area for showing the current frame (before calibration)
    @FXML
    private ImageView originalFrame;
    // the FXML area for showing the current frame (after calibration)
    @FXML
    private ImageView calibratedFrame;
    // info related to the calibration process
    @FXML
    private TextField numBoards;
    @FXML
    private TextField numHorCorners;
    @FXML
    private TextField numVertCorners;

    public CalibrationController() {
        mainImageProcessor = new CameraCalibrationImageProcessor(settings.getActiveVideoInput());

        ImageProcessor editorVisualization = new DisplayCameraCalibrationImageProcessor(settings.getActiveVideoInput());
        // Processor mit der Oberfläche verbinden
        mainImageProcessor.setVisualizationProcessor(editorVisualization);
        mainImageProcessor.setVisualizationController(this);
    }

    @FXML
    private void saveAndClose() {
        if (applicationController != null) {
            applicationController.afterSafe();
            applicationController.closeCurrentWindow();
        }
    }

    @Override
    public boolean canCloseWindow() {
        return true;
    }

    @Override
    public void changeImage(Image image) {
        originalFrame.setImage(image);
    }
}
