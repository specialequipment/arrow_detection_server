package de.woodpecker.archery.arrowdetection.imageprocessors;

import de.woodpecker.archery.arrowdetection.ArrowPosition;
import de.woodpecker.archery.arrowdetection.ArrowPositionGroup;
import de.woodpecker.archery.arrowdetection.captures.VideoInput;
import de.woodpecker.archery.arrowdetection.persistance.ArrowDetectionImageProcessorSettings;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class ArrowDetectionImageProcessor extends ImageProcessor {
    private final BlockingQueue<ArrowPosition> arrowPositions;
    private final List<ArrowPositionGroup> arrowPositionGroups = new ArrayList<>();
    private ArrowDetectionImageProcessorSettings settings;

    public ArrowDetectionImageProcessor(VideoInput videoInput, BlockingQueue<ArrowPosition> arrowPositions, ArrowPosition poisonPill) {
        super(videoInput);
        settings = videoInput.getSettings().getArrowDetection();
        this.arrowPositions = arrowPositions;

        arrowPositionGroups.add(new ArrowPositionGroup());
    }

    @Override
    public void setVideoInput(VideoInput videoInput) {
        super.setVideoInput(videoInput);
        settings = videoInput.getSettings().getArrowDetection();
    }

    @Override
    protected Mat doProcessMat(Mat sourceMat) throws InterruptedException {
        prepareDetectionList(sourceMat);
        analyseMask(sourceMat);
        return sourceMat;
    }

    private void prepareDetectionList(Mat sourceMat) throws InterruptedException {

        for (Iterator<ArrowPositionGroup> iterator = arrowPositionGroups.iterator(); iterator.hasNext(); ) {
            ArrowPositionGroup group = iterator.next();
            if (group.groupTimedOut()) {
                ArrowPosition position = new ArrowPosition(group.getPointA(), group.getPointB(), settings.getPointZero(), settings.getPointHundred());
                arrowPositions.put(position);
                // Remove the current element from the iterator and the list.
                iterator.remove();
            }
        }
    }

    private void analyseMask(Mat fgMask) {
        Mat linesP = new Mat(); // will hold the results of the detection
        Imgproc.HoughLinesP(fgMask, linesP, 1, Math.PI / 180, 50, 50, 10); // runs the actual detection
        //Imgproc.HoughLinesP(dst, linesP, 1, Math.PI / 180, 50, 50, 10); // runs the actual detection
        for (int x = 0; x < linesP.rows(); x++) {
            double[] l = linesP.get(x, 0);
            ArrowPosition position = new ArrowPosition(new Point(l[0], l[1]), new Point(l[2], l[3]), settings.getPointZero(), settings.getPointHundred());
            addPositionToList(position);
        }
    }

    private void addPositionToList(ArrowPosition position) {
        boolean inserted = false;

        for (ArrowPositionGroup arrowPositionGroup : arrowPositionGroups) {
            inserted = arrowPositionGroup.addArrowPosition(position);
        }
        if (!inserted) {
            ArrowPositionGroup arrowPositionGroup = new ArrowPositionGroup();
            arrowPositionGroup.addArrowPosition(position);
            arrowPositionGroups.add(arrowPositionGroup);
        }
    }
}
