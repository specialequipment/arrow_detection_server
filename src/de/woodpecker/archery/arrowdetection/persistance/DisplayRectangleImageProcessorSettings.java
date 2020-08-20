package de.woodpecker.archery.arrowdetection.persistance;

import org.opencv.core.Scalar;

import java.io.Serializable;

public class DisplayRectangleImageProcessorSettings implements Serializable {
    private Scalar colorRect = new Scalar(0, 0, 255);
    private Scalar colorMarker = new Scalar(0, 255, 0);

    public Scalar getColorRect() {
        return colorRect;
    }

    public void setColorRect(Scalar colorRect) {
        this.colorRect = colorRect;
    }

    public Scalar getColorMarker() {
        return colorMarker;
    }

    public void setColorMarker(Scalar colorMarker) {
        this.colorMarker = colorMarker;
    }
}
