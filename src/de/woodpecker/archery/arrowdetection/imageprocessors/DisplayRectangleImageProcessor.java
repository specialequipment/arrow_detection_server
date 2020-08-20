package de.woodpecker.archery.arrowdetection.imageprocessors;

import de.woodpecker.archery.arrowdetection.captures.VideoInput;
import de.woodpecker.archery.arrowdetection.persistance.DisplayRectangleImageProcessorSettings;
import de.woodpecker.archery.arrowdetection.persistance.RegionOfInterestImageProcessorSettings;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

public class DisplayRectangleImageProcessor extends ImageProcessor {
    private DisplayRectangleImageProcessorSettings settings;
    private RegionOfInterestImageProcessorSettings roiSettings;

    public DisplayRectangleImageProcessor(VideoInput videoInput) {
        super(videoInput);
        settings = videoInput.getSettings().getDisplayRectangle();
        roiSettings = videoInput.getSettings().getRegionOfInterest();
    }

    @Override
    public void setVideoInput(VideoInput videoInput) {
        super.setVideoInput(videoInput);
        settings = videoInput.getSettings().getDisplayRectangle();
        roiSettings = videoInput.getSettings().getRegionOfInterest();
    }

    @Override
    protected Mat doProcessMat(Mat sourceMat) {
        if (roiSettings.getRoi() == null)
            return sourceMat;

        Rect roi = roiSettings.getRoi();

        // rechteck zeichnen
        Imgproc.rectangle(sourceMat, roi, settings.getColorRect());

        // Marker linksOben und rechtsunten zeichnen
        Imgproc.drawMarker(sourceMat, new Point(roi.x, roi.y), settings.getColorMarker());
        Imgproc.drawMarker(sourceMat, new Point(roi.x + roi.width, roi.y + roi.height), settings.getColorMarker());

        return sourceMat;
    }

}
