package de.woodpecker.archery.arrowdetection.ui.settings;

import de.woodpecker.archery.arrowdetection.imageprocessors.*;
import de.woodpecker.archery.arrowdetection.ui.AnalyserController;
import de.woodpecker.archery.arrowdetection.ui.VisualizationController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BackgroundRemoverController extends AnalyserController implements VisualizationController {
    @FXML
    ImageView preview;

    public BackgroundRemoverController() {
        // Hauptaktion
        mainImageProcessor = new BackgroundRemoverImageProcessor(settings.getActiveVideoInput());
        mainImageProcessor.setVisualizationController(this);

        // Preview
        BackgroundRemoverImageProcessor previewProcessor = new BackgroundRemoverImageProcessor(settings.getActiveVideoInput());
        previewProcessor.setProcessMask(false);
        mainImageProcessor.setVisualizationProcessor(previewProcessor);

        // Vorgelagerte Aktionen
        RegionOfInterestImageProcessor regionOfInterestImageProcessor = new RegionOfInterestImageProcessor(settings.getActiveVideoInput());
        mainImageProcessor.setPreProcessor(regionOfInterestImageProcessor);

        PerspectiveCorrectionImageProcessor perspectiveCorrectionImageProcessor = new PerspectiveCorrectionImageProcessor(settings.getActiveVideoInput());
        regionOfInterestImageProcessor.setPreProcessor(perspectiveCorrectionImageProcessor);

        ImageProcessor cameraCalibrationImageProcessor = new CameraCalibrationImageProcessor(settings.getActiveVideoInput());
        perspectiveCorrectionImageProcessor.setPreProcessor(cameraCalibrationImageProcessor);
    }


    @Override
    public boolean canCloseWindow() {
        return true;
    }

    @Override
    public void changeImage(Image image) {
        preview.setImage(image);
    }

    public void saveAndClose(ActionEvent actionEvent) {
        if (applicationController != null) {
            applicationController.afterSafe();
            applicationController.closeCurrentWindow();
        }
    }
}
