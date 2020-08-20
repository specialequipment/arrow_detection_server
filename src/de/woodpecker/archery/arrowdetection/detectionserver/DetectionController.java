package de.woodpecker.archery.arrowdetection.detectionserver;

import de.woodpecker.archery.arrowdetection.AnalyserSettings;
import de.woodpecker.archery.arrowdetection.ArrowPosition;
import org.opencv.core.Point;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class DetectionController {
    private final BlockingQueue<ArrowPosition> arrowPositions;
    private final ArrowPosition poisonPill;
    private final ArrowPositionProducer arrowPositionProducer;

    public DetectionController(AnalyserSettings settings) {
        // Queue für die Pfeilerkennung
        arrowPositions = new LinkedBlockingDeque<>();
        // PoisonPill, um die Erkennung abzubrechen
        Point poison = new Point(-1, -1);
        poisonPill = new ArrowPosition(poison, poison, new Point(0, 0), new Point(0, 0));
        // Object für die in einen eigenen Thread ausgelagerte Pfeilerkennung
        arrowPositionProducer = new ArrowPositionProducer(arrowPositions, poisonPill, settings);

        start();
    }

    public BlockingQueue<ArrowPosition> getArrowPositions() {
        return arrowPositions;
    }

    public ArrowPosition getPoisonPill() {
        return poisonPill;
    }

    public void start() {
        new Thread(arrowPositionProducer).start();
    }

    public void stop() {
        arrowPositionProducer.stop();
    }
}
