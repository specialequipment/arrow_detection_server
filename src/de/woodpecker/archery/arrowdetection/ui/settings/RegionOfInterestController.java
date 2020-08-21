package de.woodpecker.archery.arrowdetection.ui.settings;

import de.woodpecker.archery.arrowdetection.imageprocessors.*;
import de.woodpecker.archery.arrowdetection.persistance.RegionOfInterestImageProcessorSettings;
import de.woodpecker.archery.arrowdetection.ui.AnalyserController;
import de.woodpecker.archery.arrowdetection.ui.VisualizationController;
import de.woodpecker.archery.arrowdetection.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Point;
import org.opencv.core.Rect;

public class RegionOfInterestController extends AnalyserController implements VisualizationController {
    private final Point[] points = new Point[2];
    private final RegionOfInterestImageProcessorSettings roiSettings;
    @FXML
    private ImageView editorView;
    private Point pointToChange;

    public RegionOfInterestController() {
        mainImageProcessor = new RegionOfInterestImageProcessor(settings.getActiveVideoInput());

        // PreProcessors
        PerspectiveCorrectionImageProcessor perspectiveCorrectionImageProcessor = new PerspectiveCorrectionImageProcessor(settings.getActiveVideoInput());
        mainImageProcessor.setPreProcessor(perspectiveCorrectionImageProcessor);

        ImageProcessor cameraCalibrationImageProcessor = new CameraCalibrationImageProcessor(settings.getActiveVideoInput());
        perspectiveCorrectionImageProcessor.setPreProcessor(cameraCalibrationImageProcessor);

        //visualisierung
        DisplayRectangleImageProcessor rectangleImageProcessor = new DisplayRectangleImageProcessor(settings.getActiveVideoInput());
        mainImageProcessor.setVisualizationProcessor(rectangleImageProcessor);
        // setController
        mainImageProcessor.setVisualizationController(this);

        roiSettings = settings.getActiveVideoInput().getSettings().getRegionOfInterest();
    }

    @Override
    protected void applicationControllerChanged() {
        super.applicationControllerChanged();
        applicationController.setImageProcessor(mainImageProcessor);
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
        editorView.setOnMouseClicked(arg0 -> {
            Point newPoint = new Point(arg0.getX() * editorView.getScaleX(), arg0.getY() * editorView.getScaleY());
            if (pointToChange == null) {
                editorView.setCursor(Cursor.NONE);
                // Denn neuen Punkt hinzufügen
                if (points[0] == null) {
                    points[0] = newPoint;
                    points[1] = new Point(points[0].x + 2, points[0].y + 2);
                    pointToChange = points[1];
                } else {
                    pointToChange = Utils.nearestPointInRange(newPoint, 50, points);
                }
            } else {
                pointToChange = null;
                editorView.setCursor(Cursor.DEFAULT);
                refreshRoi();
            }
        });
        editorView.setOnMouseMoved(arg0 -> {
            Point newPoint = new Point(arg0.getX() * editorView.getScaleX(), arg0.getY() * editorView.getScaleY());
            if (pointToChange == null) {
                if (Utils.nearestPointInRange(newPoint, 50, points) != null) {
                    editorView.setCursor(Cursor.MOVE);
                } else {
                    editorView.setCursor(Cursor.DEFAULT);
                }

                return;
            }

            pointToChange.x = newPoint.x;
            pointToChange.y = newPoint.y;
            refreshRoi();
        });
    }

    private void refreshRoi() {
        Rect roi = roiSettings.getRoi();
        int x = (int) points[0].x;
        int y = (int) points[0].y;
        int width = (int) (points[1].x - points[0].x);
        int height = (int) (points[1].y - points[0].y);
        width = width > 0 ? width : 1;
        height = height > 0 ? height : 1;

        if (roi == null) {
            roiSettings.setRoi(new Rect(x, y, width, height));
        } else {
            roi.x = x;
            roi.y = y;
            roi.width = width;
            roi.height = height;
        }
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
