package de.woodpecker.archery.arrowdetection.persistance;

import org.opencv.core.Scalar;

import java.io.Serializable;

public class DisplayArrowsImageProcessorSettings implements Serializable {
    private int arrowThickness = 3;
    private Scalar arrowColor = new Scalar(255, 80, 80);
    private Scalar colorRect = new Scalar(0, 0, 255);
    private Scalar colorMarker = new Scalar(0, 255, 0);

    public Scalar getArrowColor() {
        return arrowColor;
    }

    public void setArrowColor(Scalar arrowColor) {
        this.arrowColor = arrowColor;
    }

    public int getArrowThickness() {
        return arrowThickness;
    }

    public void setArrowThickness(int arrowThickness) {
        this.arrowThickness = arrowThickness;
    }

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
