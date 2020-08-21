package de.woodpecker.archery.arrowdetection;

import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Zusammenfassen von erkannten Pfeilen, um eine Doppelterkennung zu vermeiden
 */
public class ArrowPositionGroup {
    static final int GROUPING_DISTANCE = 100;
    static final double SLOPE_TOLLERANZ = 10;
    static final int GROUPING_TIME_RANGE = 200;//in milliseconds
    private final List<ArrowPosition> hits = new ArrayList<>();

    public boolean groupTimedOut() {
        if (hits.isEmpty())
            return false;

        return new Date().getTime() - hits.get(0).getCaptured().getTime() > GROUPING_TIME_RANGE;
    }

    public boolean addArrowPosition(ArrowPosition pos) {
        // Gruppe leer, dann immer einfügen
        if (hits.isEmpty()) {
            hits.add(pos);
            return true;
        }

        // Ermitteln, ob der erste erkannte Pfeil der Gruppe zeitlich nicht zu weit entfernt liegt
        ArrowPosition firstHit = hits.get(0);
        long dif = pos.getCaptured().getTime() - firstHit.getCaptured().getTime();
        if (dif > GROUPING_TIME_RANGE) {
            return false;
        }

        // ermitteln ob der neue Pfeil eine ähnliche Steigung enthält und in der nähe des ersten Pfeils liegt
        double slopeDiff = Math.abs(firstHit.getSlope() - pos.getSlope());
        if (slopeDiff > SLOPE_TOLLERANZ) {
            return false;
        }

        // Abstand zur ursprünglichen gerade darf nicht zu groß sein
        double distance = calcDist(firstHit, pos);
        distance = Math.abs(distance);
        if (distance > GROUPING_DISTANCE) {
            return false;
        }

        hits.add(pos);
        return true;
    }

    private double calcDist(ArrowPosition firstHit, ArrowPosition pos) {
        double b = firstHit.getPointA().y - firstHit.getSlope() * firstHit.getPointA().x;
        return firstHit.getSlope() * pos.getPointA().x - pos.getPointA().y + b;
    }

    public Point getPointA() {
        double x = Double.MAX_VALUE;
        double y = Double.MAX_VALUE;
        for (ArrowPosition hit : hits) {
            if (hit.getPointA().y < y) {
                x = hit.getPointA().x;
                y = hit.getPointA().y;
            }
        }
        return new Point(x, y);
    }

    public Point getPointB() {
        double x = 0;
        double y = 0;
        for (ArrowPosition hit : hits) {
            if (hit.getPointB().y > y) {
                x = hit.getPointB().x;
                y = hit.getPointB().y;
            }
        }
        return new Point(x, y);
    }


}
