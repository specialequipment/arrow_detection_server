package de.woodpecker.archery.arrowdetection.ui.test;

import de.woodpecker.archery.arrowdetection.ArrowPosition;
import de.woodpecker.archery.arrowdetection.detectionserver.DetectionController;
import de.woodpecker.archery.arrowdetection.ui.AnalyserController;
import de.woodpecker.archery.arrowdetection.utils.Utils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

public class HitAreaController extends AnalyserController {
    int i = 0;
    private DetectionController detectionController;
    @FXML
    private Pane imgParent;
    @FXML
    private ImageView imageView;
    @FXML
    private Label hitsLabel;
    @FXML
    private Label lastPositionLabel;
    private Mat background;
    private Timer timer;

    public HitAreaController() {
        mainImageProcessor = null;

    }


    @Override
    protected void applicationControllerChanged() {
        super.applicationControllerChanged();
        initBackground();
        run();
    }

    @FXML
    private void run() {
        applicationController.stopPreview();

        if (detectionController == null)
            detectionController = new DetectionController(settings);

        TimerTask frameGrabber = new TimerTask() {
            @Override
            public void run() {
                if (background == null)
                    return;

                Mat newMat = background.clone();
                var ref = new Object() {
                    ArrowPosition lastArrowPosition = null;
                };

                boolean arrowDetected = false;

                while (true) {
                    ArrowPosition arrowPosition;
                    if (arrowDetected)
                        arrowPosition = detectionController.getArrowPositions().poll();
                    else {
                        try {
                            arrowPosition = detectionController.getArrowPositions().take();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            return;
                        }
                    }

                    if (arrowPosition == null)
                        break;

                    ref.lastArrowPosition = arrowPosition;

                    if (arrowPosition.equals(detectionController.getPoisonPill())) {
                        timer.cancel();
                    }

                    // ToDo: Treffer Zeichnen
                    Point point = new Point(newMat.width() * arrowPosition.relativeX(), newMat.height() * arrowPosition.relativeY());
                    Imgproc.drawMarker(newMat, point, new Scalar(0, 0, 255), Imgproc.MARKER_TILTED_CROSS, 30, 3);
                    Imgproc.drawMarker(newMat, point, new Scalar(100, 100, 255), Imgproc.MARKER_TILTED_CROSS, 15, 3);
                    Imgproc.drawMarker(newMat, point, new Scalar(0, 0, 0), Imgproc.MARKER_TILTED_CROSS, 40, 1);
                    i++;
                    arrowDetected = true;
                }

                Image hitVisualizationImage = Utils.mat2Image(newMat);
                // show the original frames
                Platform.runLater(() -> {
                    imageView.setImage(hitVisualizationImage);

                    hitsLabel.setText(Integer.toString(i));
                    if (ref.lastArrowPosition != null) {
                        String x = NumberFormat.getNumberInstance().format(ref.lastArrowPosition.relativeX() * 100);
                        String y = NumberFormat.getNumberInstance().format(ref.lastArrowPosition.relativeY() * 100);
                        lastPositionLabel.setText(x + " / " + y);
                    }
                });

            }
        };
        timer = new Timer();
        timer.schedule(frameGrabber, 0, 33);
    }

    @Override
    public boolean canCloseWindow() {
        return true;
    }

    @FXML
    public void initialize() {
        imageView.fitWidthProperty().bind(imgParent.widthProperty());
        imageView.fitHeightProperty().bind(imgParent.heightProperty());
        ChangeListener<Number> imgParentSizeListener = (observable, oldValue, newValue) ->
                initBackground();

        imgParent.widthProperty().addListener(imgParentSizeListener);
        imgParent.heightProperty().addListener(imgParentSizeListener);

        initBackground();
    }

    private void initBackground() {
        int width = (int) imgParent.getWidth();
        int height = (int) imgParent.getHeight();

        if (width == 0 || height == 0)
            return;

        background = new Mat(height, width, CvType.CV_8UC3);
    }
}
