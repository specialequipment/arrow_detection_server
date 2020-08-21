package de.woodpecker.archery.arrowdetection.imageprocessors;

import de.woodpecker.archery.arrowdetection.ArrowPosition;
import de.woodpecker.archery.arrowdetection.captures.VideoInput;
import de.woodpecker.archery.arrowdetection.persistance.ArrowDetectionImageProcessorSettings;
import de.woodpecker.archery.arrowdetection.persistance.DisplayArrowsImageProcessorSettings;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.concurrent.BlockingQueue;

public class DisplayArrowsImageProcessor extends ImageProcessor {

    private final BlockingQueue<ArrowPosition> arrowPositionsQueue;
    private final Mat lastDetectedArrowImage = new Mat();
    private DisplayArrowsImageProcessorSettings settings;
    private ArrowDetectionImageProcessorSettings arrowDetectionSettings;

    public DisplayArrowsImageProcessor(VideoInput videoInput, BlockingQueue<ArrowPosition> arrowPositionsQueue, ArrowPosition poisonPill) {
        super(videoInput);
        settings = videoInput.getSettings().getDisplayArrows();
        arrowDetectionSettings = videoInput.getSettings().getArrowDetection();
        this.arrowPositionsQueue = arrowPositionsQueue;
    }

    @Override
    public void setVideoInput(VideoInput videoInput) {
        super.setVideoInput(videoInput);
        settings = videoInput.getSettings().getDisplayArrows();
        arrowDetectionSettings = videoInput.getSettings().getArrowDetection();
    }

    @Override
    protected Mat doProcessMat(Mat sourceMat) {
        // neue Punkte anzeigen
        ArrowPosition arrowPosition;
        boolean newArrowDetected = false;
        while (true) {
            arrowPosition = arrowPositionsQueue.poll();
            if (arrowPosition == null)
                break;
            Imgproc.arrowedLine(sourceMat, arrowPosition.getPointB(), arrowPosition.getPointA(), settings.getArrowColor(), settings.getArrowThickness());
            newArrowDetected = true;
        }
        if (newArrowDetected || lastDetectedArrowImage.empty())
            sourceMat.copyTo(lastDetectedArrowImage);

        Mat newMat = lastDetectedArrowImage.clone();

        // Null Refferenz zeichnen
        // rechteck zeichnen
        if (arrowDetectionSettings.getPointZero() != null && arrowDetectionSettings.getPointHundred() != null)
            Imgproc.rectangle(newMat, arrowDetectionSettings.getPointZero(), arrowDetectionSettings.getPointHundred(), settings.getColorRect());

        // Marker linksOben und rechtsunten zeichnen
        if (arrowDetectionSettings.getPointZero() != null)
            Imgproc.drawMarker(newMat, arrowDetectionSettings.getPointZero(), settings.getColorMarker());
        if (arrowDetectionSettings.getPointHundred() != null)
            Imgproc.drawMarker(newMat, arrowDetectionSettings.getPointHundred(), settings.getColorMarker());

        return newMat;
    }
}
