package de.woodpecker.archery.arrowdetection.cameraprocessors;

import de.woodpecker.archery.arrowdetection.imageprocessors.ImageProcessor;
import de.woodpecker.archery.arrowdetection.ui.AnalyserController;
import de.woodpecker.archery.arrowdetection.ui.VisualizationController;
import de.woodpecker.archery.arrowdetection.utils.Utils;
import javafx.application.Platform;
import javafx.scene.image.Image;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.util.Timer;
import java.util.TimerTask;

public class CameraProcessorBase {
    private boolean cameraActive = false;
    private Timer timer;
    private VisualizationController visualizationController;
    private VideoCapture capture;
    private ImageProcessor imageProcessor;

    public void setImageProcessor(ImageProcessor imageProcessor) {
        this.imageProcessor = imageProcessor;
        if (imageProcessor == null) {
            stopCamera();
        } else {
            startCamera();
        }
    }

    public void setVisualizationController(VisualizationController visualizationController) {
        this.visualizationController = visualizationController;
    }

    public void restartCameraWithNewSettings() {
        stopCamera();
        startCamera();
    }

    private void stopTimer() {
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
    }

    /**
     * start processing
     */
    public void startCamera() {
        if (cameraActive)
            return;

        stopTimer();

        if (AnalyserController.getSettings().getActiveVideoInput() == null)
            return;

        // grab a frame every 33 ms (30 frames/sec)
        TimerTask frameGrabber = new TimerTask() {
            @Override
            public void run() {
                if ((capture == null) && !initCapture()) {
                    return;
                }
                Mat camStream = grabFrame(capture);

                if (camStream.empty()) {
                    return;
                }

                Mat processedMat = null;
                try {
                    processedMat = processMat(camStream);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    stopTimer();
                }
                Image result = Utils.mat2Image(processedMat);

                // show the original frames
                Platform.runLater(() -> {
                    if (visualizationController != null)
                        visualizationController.changeImage(result);
                });

            }
        };
        cameraActive = true;
        timer = new Timer("CameraImageGrabber");
        timer.schedule(frameGrabber, 0, 30);
    }

    private boolean initCapture() {
        if (AnalyserController.getSettings().getActiveVideoInput() == null)
            return false;

        // try to get an Video Capture device
        VideoCapture newCapture = new VideoCapture();
        AnalyserController.getSettings().getActiveVideoInput().openCapture(newCapture);

        // is the video stream available?
        if (!newCapture.isOpened()) {
            //ToDo: ExcaptionHandling
            System.err.println("Impossible to open the camera connection...");
            return false;
        }


        this.capture = newCapture;
        return true;
    }

    /**
     * stop processing
     */
    public void stopCamera() {
        // the camera is not active at this point
        cameraActive = false;

        // stop the timer
        stopTimer();

        // release the camera
        if (capture != null) {
            capture.release();
            capture = null;
        }
    }

    /**
     * Method to process the image in child classes
     * Default is no image manipulation
     *
     * @param camStream
     * @return
     */
    protected Mat processMat(Mat camStream) throws InterruptedException {
        if (imageProcessor == null)
            return camStream;

        return imageProcessor.processMat(camStream);
    }

    /**
     * Get a frame from the opened video stream (if any)
     *
     * @param capture
     * @return the {@link Image} to show
     */
    private Mat grabFrame(VideoCapture capture) {
        Mat frame = new Mat();
        capture.read(frame);
        return frame;
    }
}
