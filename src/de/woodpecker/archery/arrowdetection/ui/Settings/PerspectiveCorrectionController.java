package de.woodpecker.archery.arrowdetection.ui.Settings;

import de.woodpecker.archery.arrowdetection.imageprocessors.CameraCalibrationImageProcessor;
import de.woodpecker.archery.arrowdetection.imageprocessors.Display4EdgesImageProcessor;
import de.woodpecker.archery.arrowdetection.imageprocessors.ImageProcessor;
import de.woodpecker.archery.arrowdetection.imageprocessors.PerspectiveCorrectionImageProcessor;
import de.woodpecker.archery.arrowdetection.persistance.PerspectiveCorrectionImageProcessorSettings;
import de.woodpecker.archery.arrowdetection.ui.AnalyserController;
import de.woodpecker.archery.arrowdetection.ui.VisualizationController;
import de.woodpecker.archery.arrowdetection.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Point;

public class PerspectiveCorrectionController extends AnalyserController implements VisualizationController {
    private final PerspectiveCorrectionImageProcessor perspectiveCorrectionImageProcessor;
    private final PerspectiveCorrectionImageProcessorSettings perspectiveSettings;
    @FXML
    Slider zoomSLider;
    @FXML
    private ImageView editorView;
    private Point pointToChange;

    public PerspectiveCorrectionController() {
        perspectiveCorrectionImageProcessor = new PerspectiveCorrectionImageProcessor(settings.getActiveVideoInput());
        mainImageProcessor = perspectiveCorrectionImageProcessor;
        // Processor mit der Oberfläche verbinden
        mainImageProcessor.setVisualizationController(this);
        // init Visualizer
        ImageProcessor visualizationProcessor = new Display4EdgesImageProcessor(settings.getActiveVideoInput());
        mainImageProcessor.setVisualizationProcessor(visualizationProcessor);
        // PreProcessors
        ImageProcessor cameraCalibrationImageProcessor = new CameraCalibrationImageProcessor(settings.getActiveVideoInput());
        mainImageProcessor.setPreProcessor(cameraCalibrationImageProcessor);

        perspectiveSettings = settings.getActiveVideoInput().getSettings().getPerspectiveCorrection();
    }

    @FXML
    private void saveAndClose() {
        if (applicationController != null) {
            applicationController.afterSafe();
            applicationController.closeCurrentWindow();
        }
    }

    @FXML
    public void initialize() {
        editorView.scaleXProperty().bind(zoomSLider.valueProperty().divide(100));
        editorView.scaleYProperty().bind(zoomSLider.valueProperty().divide(100));

        editorView.setOnMouseClicked(arg0 -> {
            perspectiveCorrectionImageProcessor.prepareWarp();
            Point newPoint = new Point(arg0.getX() * editorView.getScaleX(), arg0.getY() * editorView.getScaleY());
            if (pointToChange == null) {
                editorView.setCursor(Cursor.NONE);
                // Den neuen Punkt hinzufügen
                if (perspectiveSettings.addPerspectiveCorrectionPoint(newPoint)) {
                    pointToChange = newPoint;
                } else {
                    // konnte der Punkt nicht eingefügt werden, dann einen vorhandenen verschieben
                    pointToChange = Utils.nearestPointInRange(newPoint, 100, perspectiveSettings.getPerspectiveCorrectionPoints());
                }
            } else {
                if (perspectiveSettings.allPerspectiveCorrectionPointsSet()) {
                    perspectiveSettings.changePerspectiveCorrectionPoint(pointToChange, newPoint);
                    editorView.setCursor(Cursor.DEFAULT);
                    pointToChange = null;
                } else {
                    Point nextPoint = new Point(newPoint.x + 2, newPoint.y + 2);
                    perspectiveSettings.addPerspectiveCorrectionPoint(nextPoint);
                    pointToChange = nextPoint;
                }
            }
        });
        editorView.setOnMouseMoved(arg0 -> {
            Point newPoint = new Point(arg0.getX() * editorView.getScaleX(), arg0.getY() * editorView.getScaleY());
            if (pointToChange == null) {
                if (Utils.nearestPointInRange(newPoint, 50, perspectiveSettings.getPerspectiveCorrectionPoints()) != null) {
                    editorView.setCursor(Cursor.MOVE);
                } else {
                    editorView.setCursor(Cursor.DEFAULT);
                }

                return;
            }
            perspectiveSettings.changePerspectiveCorrectionPoint(pointToChange, newPoint);
            pointToChange = newPoint;
            perspectiveCorrectionImageProcessor.prepareWarp();
        });
    }

    @Override
    public boolean canCloseWindow() {
        return true;
    }

    @Override
    public void changeImage(Image image) {
        editorView.setImage(image);
    }
}
