package de.woodpecker.archery.arrowdetection;

import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Zusammenfassen von erkannten Pfeilen, um eine Doppelterkennung zu vermeiden
 */
public class ArrowPositionGroup {
    static int groupingDistance = 100;
    static double slopeTolleranz = 10;
    static int groupingTimeRange = 200;//in milliseconds
    private final List<ArrowPosition> hits = new ArrayList<>();

    public boolean groupTimedOut() {
        if (hits.size() == 0)
            return false;

        return new Date().getTime() - hits.get(0).getCaptured().getTime() > groupingTimeRange;
    }

    public boolean addArrowPosition(ArrowPosition pos) {
        // Gruppe leer, dann immer einfügen
        if (hits.size() == 0) {
            hits.add(pos);
            debug(pos, "keine Gruppe vorhanden");
            return true;
        }

        // Ermitteln, ob der erste erkannte Pfeil der Gruppe zeitlich nicht zu weit entfernt liegt
        ArrowPosition firstHit = hits.get(0);
        long dif = pos.getCaptured().getTime() - firstHit.getCaptured().getTime();
        if (dif > groupingTimeRange) {
            debug(pos, "Zeit der Gruppe überschritten: " + dif);
            return false;
        }

        // ermitteln ob der neue Pfeil eine ähnliche Steigung enthält und in der nähe des ersten Pfeils liegt
        double slopeDiff = Math.abs(firstHit.getSlope() - pos.getSlope());
        if (slopeDiff > slopeTolleranz) {
            debug(pos, "Steigung weicht ab: " + slopeDiff);
            return false;
        }

        // Abstand zur ursprünglichen gerade darf nicht zu groß sein
        double distance = calcDist(firstHit, pos);
        distance = Math.abs(distance);
        if (distance > groupingDistance) {
            debug(pos, "Entfernung zum vorhergehenden Treffer zu groß: " + distance);
            return false;
        }

        hits.add(pos);
        return true;
    }

    private void debug(ArrowPosition pos, String msg) {
        //System.out.println(pos.toString() + " - " + msg);
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
