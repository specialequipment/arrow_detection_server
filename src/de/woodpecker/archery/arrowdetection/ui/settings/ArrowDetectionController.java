package de.woodpecker.archery.arrowdetection.ui.settings;

import de.woodpecker.archery.arrowdetection.ArrowPosition;
import de.woodpecker.archery.arrowdetection.imageprocessors.*;
import de.woodpecker.archery.arrowdetection.persistance.ArrowDetectionImageProcessorSettings;
import de.woodpecker.archery.arrowdetection.ui.AnalyserController;
import de.woodpecker.archery.arrowdetection.ui.VisualizationController;
import de.woodpecker.archery.arrowdetection.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Point;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class ArrowDetectionController extends AnalyserController implements VisualizationController {
    @FXML
    ImageView preview;
    private Point pointToChange;

    public ArrowDetectionController() {
        BlockingQueue<ArrowPosition> arrowPositions = new LinkedBlockingDeque<>();
        Point poison = new Point(-1, -1);
        ArrowPosition poisonPill = new ArrowPosition(poison, poison, new Point(0, 0), new Point(0, 0));
        mainImageProcessor = new ArrowDetectionImageProcessor(settings.getActiveVideoInput(), arrowPositions, poisonPill);

        // Vorgelagerte Aktionen
        BackgroundRemoverImageProcessor backgroundRemoverImageProcessor = new BackgroundRemoverImageProcessor(settings.getActiveVideoInput());
        mainImageProcessor.setPreProcessor(backgroundRemoverImageProcessor);

        RegionOfInterestImageProcessor regionOfInterestImageProcessor = new RegionOfInterestImageProcessor(settings.getActiveVideoInput());
        backgroundRemoverImageProcessor.setPreProcessor(regionOfInterestImageProcessor);

        PerspectiveCorrectionImageProcessor perspectiveCorrectionImageProcessor = new PerspectiveCorrectionImageProcessor(settings.getActiveVideoInput());
        regionOfInterestImageProcessor.setPreProcessor(perspectiveCorrectionImageProcessor);

        ImageProcessor cameraCalibrationImageProcessor = new CameraCalibrationImageProcessor(settings.getActiveVideoInput());
        perspectiveCorrectionImageProcessor.setPreProcessor(cameraCalibrationImageProcessor);

        // Preview
        DisplayArrowsImageProcessor previewProcessor = new DisplayArrowsImageProcessor(settings.getActiveVideoInput(), arrowPositions, poisonPill);
        backgroundRemoverImageProcessor.setVisualizationProcessor(previewProcessor);
        backgroundRemoverImageProcessor.setVisualizationController(this);
    }

    public void saveAndClose(ActionEvent actionEvent) {
        if (applicationController != null) {
            applicationController.afterSafe();
            applicationController.closeCurrentWindow();
        }
    }

    @FXML
    public void initialize() {
        // Trefferanzeige
        ArrowDetectionImageProcessorSettings arrowDetectonsettings = settings.getActiveVideoInput().getSettings().getArrowDetection();
        Point[] points = {arrowDetectonsettings.getPointZero(), arrowDetectonsettings.getPointHundred()};

        preview.setOnMouseClicked(arg0 -> {
            Point newPoint = new Point(arg0.getX() * preview.getScaleX(), arg0.getY() * preview.getScaleY());
            if (pointToChange == null) {
                preview.setCursor(Cursor.NONE);
                // Den neuen Punkt hinzufügen
                if (arrowDetectonsettings.getPointZero() == null) {
                    arrowDetectonsettings.setPointZero(newPoint);
                    pointToChange = arrowDetectonsettings.getPointZero();
                } else if (arrowDetectonsettings.getPointHundred() == null) {
                    arrowDetectonsettings.setPointHundred(newPoint);
                    pointToChange = arrowDetectonsettings.getPointHundred();
                } else {
                    pointToChange = Utils.nearestPointInRange(newPoint, 50, points);
                }
            } else {
                pointToChange = null;
                preview.setCursor(Cursor.DEFAULT);
            }
        });
        //ToDo: Funktionalität generalisieren (Wird auch im RegionOfInterestController verwendet)
        preview.setOnMouseMoved(arg0 -> {
            Point newPoint = new Point(arg0.getX() * preview.getScaleX(), arg0.getY() * preview.getScaleY());
            if (pointToChange == null) {

                if (Utils.nearestPointInRange(newPoint, 50, points) != null) {
                    preview.setCursor(Cursor.MOVE);
                } else {
                    preview.setCursor(Cursor.DEFAULT);
                }

                return;
            }

            pointToChange.x = newPoint.x;
            pointToChange.y = newPoint.y;
        });
    }

    @Override
    public boolean canCloseWindow() {
        return true;
    }

    @Override
    public void changeImage(Image image) {
        preview.setImage(image);
    }
}
