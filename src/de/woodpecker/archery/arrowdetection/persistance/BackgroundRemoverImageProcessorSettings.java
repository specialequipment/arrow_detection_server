package de.woodpecker.archery.arrowdetection.persistance;

import java.io.Serializable;

public class BackgroundRemoverImageProcessorSettings implements Serializable {
    private int backgroundhistory = 2;
    private int Threshold = 1000;
    private boolean detectShadows = false;

    public int getBackgroundhistory() {
        return backgroundhistory;
    }

    public void setBackgroundhistory(int backgroundhistory) {
        this.backgroundhistory = backgroundhistory;
    }

    public int getThreshold() {
        return Threshold;
    }

    public void setThreshold(int threshold) {
        Threshold = threshold;
    }

    public boolean isDetectShadows() {
        return detectShadows;
    }

    public void setDetectShadows(boolean detectShadows) {
        this.detectShadows = detectShadows;
    }
}
