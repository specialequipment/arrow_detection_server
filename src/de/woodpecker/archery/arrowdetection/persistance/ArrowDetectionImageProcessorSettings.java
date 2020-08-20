package de.woodpecker.archery.arrowdetection.persistance;

import org.opencv.core.Point;

import java.io.Serializable;

public class ArrowDetectionImageProcessorSettings implements Serializable {
    private Point pointZero;
    private Point pointHundred;

    public Point getPointZero() {
        return pointZero;
    }

    public void setPointZero(Point pointZero) {
        this.pointZero = pointZero;
    }

    public Point getPointHundred() {
        return pointHundred;
    }

    public void setPointHundred(Point pointHundred) {
        this.pointHundred = pointHundred;
    }
}
