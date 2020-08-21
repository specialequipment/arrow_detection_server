package de.woodpecker.archery.arrowdetection.imageprocessors;

import de.woodpecker.archery.arrowdetection.captures.VideoInput;
import de.woodpecker.archery.arrowdetection.persistance.RegionOfInterestImageProcessorSettings;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

public class RegionOfInterestImageProcessor extends ImageProcessor {
    private RegionOfInterestImageProcessorSettings settings;

    public RegionOfInterestImageProcessor(VideoInput videoInput) {
        super(videoInput);
        settings = videoInput.getSettings().getRegionOfInterest();
    }

    @Override
    public void setVideoInput(VideoInput videoInput) {
        super.setVideoInput(videoInput);
        settings = videoInput.getSettings().getRegionOfInterest();
    }

    @Override
    protected Mat doProcessMat(Mat sourceMat) {
        // nur wenn alle vier Seiten eingegrenzt wurden weitermachen, ggf. in Zukunft erweitern, so dass nur eine Seite eingeschränkt werden kann.
        Rect roi = settings.getRoi();
        if (roi == null)
            return sourceMat;

        return new Mat(sourceMat, roi);
    }


}
