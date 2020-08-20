package de.woodpecker.archery.arrowdetection.persistance;

import org.opencv.core.Rect;

import java.io.Serializable;

public class RegionOfInterestImageProcessorSettings implements Serializable {
    private Rect roi = null;

    public Rect getRoi() {
        return roi;
    }

    public void setRoi(Rect roi) {
        this.roi = roi;
    }
}
