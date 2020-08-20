package de.woodpecker.archery.arrowdetection;

import org.opencv.core.Point;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ArrowPosition {
    protected final static Random random = new Random();
    private final Date captured;
    private final double slope;
    private final Point pointA;// Top
    private final Point pointB;//Bottom
    private final Point pointZero;// 0/0 Prozent Referenz
    private final Point pointHundred;// 100/100 Prozent Referenz
    private boolean shown;


    public ArrowPosition(Point pointA, Point pointB, Point pointZero, Point pointHundred) {
        this.pointZero = pointZero;
        this.pointHundred = pointHundred;
        this.captured = new Date();

        // sort Point A is top left
        if ((pointA.y < pointB.y) || (pointA.y == pointB.y && pointA.x < pointB.y)) {
            this.pointA = pointA;
            this.pointB = pointB;
        } else {
            this.pointA = pointB;
            this.pointB = pointA;
        }

        // Steigung berechnen
        slope = calcSlope(this.pointA, this.pointB);
        shown = false;
    }

    public Point getPointB() {
        return pointB;
    }

    public double relativeX() {
        if (pointA == null || pointZero == null || pointHundred == null)
            return 0;
        return (pointA.x - pointZero.x) / pointHundred.x;
    }

    public double relativeY() {
        if (pointA == null || pointZero == null || pointHundred == null)
            return 0;
        return (pointA.y - pointZero.y) / pointHundred.y;
    }

    public double getSlope() {
        return slope;
    }

    private double calcSlope(Point pointA, Point pointB) {
        //Steigung = y2-y1 / x2-x1
        return (pointA.y - pointB.y) / (pointA.x - pointB.x);
    }

    public Date getCaptured() {
        return captured;
    }

    public boolean isShown() {
        return shown;
    }

    public void setShown(boolean shown) {
        this.shown = shown;
    }

    public Point getPointA() {
        return pointA;
    }

    @Override
    public String toString() {
        String pattern = " HH:mm:ss.SSSZ";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String time = simpleDateFormat.format(captured);
        return "Zeit: " + time + " Position: " + pointA;
    }
}
