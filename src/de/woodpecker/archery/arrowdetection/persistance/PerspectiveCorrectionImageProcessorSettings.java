package de.woodpecker.archery.arrowdetection.persistance;

import org.opencv.core.Point;

import java.io.Serializable;

public class PerspectiveCorrectionImageProcessorSettings implements Serializable {
    private Point[] perspectiveCorrectionPoints = new Point[4];

    public Point[] getPerspectiveCorrectionPoints() {
        return perspectiveCorrectionPoints;
    }

    public void setPerspectiveCorrectionPoints(Point[] perspectiveCorrectionPoints) {
        if (perspectiveCorrectionPoints.length != 4)
            throw new IllegalArgumentException("Es müssen genau 4 Punkte übergeben werden");
        this.perspectiveCorrectionPoints = perspectiveCorrectionPoints;
    }

    private Point[] sortPerspectiveCorrectionPoints(Point[] points) {
        // wenn noch nicht alle Punkte angelegt, abbrechen
        if (!allPerspectiveCorrectionPointsSet())
            return points;

        // Mittelpunkt bestimmen
        int xMiddle = (int) (perspectiveCorrectionPoints[0].x + perspectiveCorrectionPoints[1].x + perspectiveCorrectionPoints[2].x + perspectiveCorrectionPoints[3].x) / 4;

        Point point1 = null;
        Point point2 = null;
        Point point3 = null;
        Point point4 = null;

        // sortieren
        for (Point value : points) {
            if (value.x < xMiddle) {
                if (point1 == null)
                    point1 = value;
                else if (point1.y > value.y) {
                    point2 = point1;
                    point1 = value;
                } else {
                    point2 = value;
                }
            } else {
                if (point3 == null)
                    point3 = value;
                else if (point3.y > value.y) {
                    point4 = point3;
                    point3 = value;
                } else {
                    point4 = value;
                }
            }
        }

        Point[] result = {point1, point3, point2, point4};
        return result;
    }

    public boolean allPerspectiveCorrectionPointsSet() {
        if (perspectiveCorrectionPoints == null)
            return false;

        return perspectiveCorrectionPoints[0] != null && perspectiveCorrectionPoints[1] != null && perspectiveCorrectionPoints[2] != null && perspectiveCorrectionPoints[3] != null;
    }

    public boolean addPerspectiveCorrectionPoint(Point point) {
        boolean inserted = false;
        // in die Liste aufnehmen
        for (int i = 0; i < perspectiveCorrectionPoints.length; i++) {
            if (perspectiveCorrectionPoints[i] == null) {
                perspectiveCorrectionPoints[i] = point;
                inserted = true;
                break;
            }
        }
        return inserted;
    }

    public void addPerspectiveCorrectionPoint(Point point, int pos) {
        if (pos > 3 || pos < 0) {
            throw new IllegalArgumentException();
        }
        perspectiveCorrectionPoints[pos] = point;
    }

    public void changePerspectiveCorrectionPoint(Point originPoint, Point newPoint) {
        for (int i = 0; i < perspectiveCorrectionPoints.length; i++) {
            if (perspectiveCorrectionPoints[i].equals(originPoint)) {
                perspectiveCorrectionPoints[i] = newPoint;
                return;
            }
        }
    }

}
