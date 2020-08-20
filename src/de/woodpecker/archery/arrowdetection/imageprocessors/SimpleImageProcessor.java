package de.woodpecker.archery.arrowdetection.imageprocessors;

import de.woodpecker.archery.arrowdetection.captures.VideoInput;
import org.opencv.core.Mat;

public class SimpleImageProcessor extends ImageProcessor {

    public SimpleImageProcessor(VideoInput videoInput) {
        super(videoInput);
    }

    @Override
    protected Mat doProcessMat(Mat sourceMat) {
        return sourceMat;
    }
}
