package de.woodpecker.archery.arrowdetection.imageprocessors;

import de.woodpecker.archery.arrowdetection.captures.VideoInput;
import de.woodpecker.archery.arrowdetection.persistance.BackgroundRemoverImageProcessorSettings;
import org.opencv.core.Mat;
import org.opencv.video.BackgroundSubtractor;
import org.opencv.video.Video;

public class BackgroundRemoverImageProcessor extends ImageProcessor {
    private final Mat foregroundMask = new Mat();
    private BackgroundSubtractor backgroundSubtractor;
    private boolean processMask = true;
    private BackgroundRemoverImageProcessorSettings settings;

    public BackgroundRemoverImageProcessor(VideoInput camera) {
        super(camera);
        settings = camera.getSettings().getBackgroundRemover();
        initBackgroundSubtractor();
    }

    public void setProcessMask(boolean processMask) {
        this.processMask = processMask;
    }

    @Override
    public void setVideoInput(VideoInput videoInput) {
        super.setVideoInput(videoInput);
        settings = videoInput.getSettings().getBackgroundRemover();
    }

    private void initBackgroundSubtractor() {
        backgroundSubtractor = Video.createBackgroundSubtractorMOG2(settings.getBackgroundhistory(), settings.getThreshold(), settings.isDetectShadows());
    }

    @Override
    protected Mat doProcessMat(Mat sourceMat) {
        backgroundSubtractor.apply(sourceMat, foregroundMask);
        if (processMask)
            return foregroundMask;
        else
            return sourceMat;
    }
}
