package de.woodpecker.archery.arrowdetection.detectionserver;

import de.woodpecker.archery.arrowdetection.AnalyserSettings;
import de.woodpecker.archery.arrowdetection.ArrowPosition;
import de.woodpecker.archery.arrowdetection.ArrowPositionGroup;
import de.woodpecker.archery.arrowdetection.captures.VideoInput;
import de.woodpecker.archery.arrowdetection.utils.Utils;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class ArrowPositionProducer implements Runnable {
    private final BlockingQueue<ArrowPosition> arrowPositions;
    private final ArrowPosition poisonPill;
    private final AnalyserSettings settings;
    private final Mat foregroundMask = new Mat();
    private final List<ArrowPositionGroup> arrowPositionGroups = new ArrayList<>();
    private VideoCapture capture;
    private Mat warp;
    private BackgroundSubtractorMOG2 backgroundSubtractor;

    public ArrowPositionProducer(BlockingQueue<ArrowPosition> arrowPositions, ArrowPosition poisonPill, AnalyserSettings settings) {
        this.arrowPositions = arrowPositions;
        this.poisonPill = poisonPill;
        this.settings = settings;
    }

    @Override
    public void run() {
        try {
            init();
            detect();
        } catch (InterruptedException ex) {
            stop();
            Thread.currentThread().interrupt();
        }
    }

    public void stop() {
        if (capture == null)
            return;

        capture.release();
        capture = null;
    }

    private void init() throws InterruptedException {
        // Camera Capture vorbereiten
        initCapture();

        try {
            // Perspektivische Korrektur vorbereiten
            prepareWarp(settings.getActiveVideoInput().getSettings().getPerspectiveCorrection().getPerspectiveCorrectionPoints());
            // Hintergrundentfernen
            initBackgroundSubtractor();
        } catch (IllegalArgumentException ex) {
            arrowPositions.put(poisonPill);
            stop();
        }
    }

    private void initBackgroundSubtractor() {
        backgroundSubtractor = Video.createBackgroundSubtractorMOG2(settings.getActiveVideoInput().getSettings().getBackgroundRemover().getBackgroundhistory(),
                settings.getActiveVideoInput().getSettings().getBackgroundRemover().getThreshold(),
                settings.getActiveVideoInput().getSettings().getBackgroundRemover().isDetectShadows());
    }

    private void prepareWarp(Point[] points) {
        if (points.length != 4 || points[0] == null || points[1] == null || points[2] == null || points[3] == null) {
            StringBuilder msg = new StringBuilder("Für die Berechnung werden genau 4 Punkte benötigt, es wurden aber folgede Punkte übergeben:" + System.lineSeparator());
            for (int i = 0; i < points.length; i++) {
                if (points[i] != null)
                    msg.append("Punkt ").append(i).append(": ").append(points[i].toString()).append(System.lineSeparator());
            }
            throw new IllegalArgumentException(msg.toString());
        }

        //ToDo: auslagern, da auch im PerspectiveCorrectionController benötigt
        // Source Image from points
        MatOfPoint2f src = new MatOfPoint2f(
                points[0],
                points[1],
                points[2],
                points[3]);

        // DestinationImageProperties
        // Zielbild muss die Proportionen des Bildes abbilden, daher wird z7unächst die Entfernung der Punkte an der Oberkannte
        // ermittelt und ebenfalls die Entfernung der Punkte auf der linken Seite
        double width = Utils.distance(points[2], points[3]);
        double height = Utils.distance(points[1], points[3]);

        MatOfPoint2f dst = new MatOfPoint2f(
                new Point(0, 0),
                new Point(width, 0),
                new Point(0, height),
                new Point(width, height)
        );
        warp = Imgproc.getPerspectiveTransform(src, dst);
    }

    private void detect() throws InterruptedException {
        while (capture != null) {
            // sollte abgebrochen werden

            // Von der Kamera lesen
            //ToDo: prüfen ob ThreadSleep die richtige Variante ist, ggf anderen Ansatz implementieren und parametrisieren
            //Thread.sleep(100);
            Mat frame = new Mat();
            capture.read(frame);
            if (!frame.empty()) {
                // Kamera kalibrierung anwenden
                frame = undistore(frame);
                // Perspektivenkorrektur
                frame = perspectiveCorrection(frame);
                // zuschneiden
                frame = prepareRoi(frame);
                // Hintergrund entfernen
                frame = doBackgroundSubtraction(frame);
                // Pfeilerkennen
                detectArrows(frame);
            }
        }
    }

    private void detectArrows(Mat frame) throws InterruptedException {
        sendDetectedArrows(frame);
        analyseMask(frame);
    }

    private Mat doBackgroundSubtraction(Mat frame) {
        backgroundSubtractor.apply(frame, foregroundMask);
        return foregroundMask;
    }

    private Mat prepareRoi(Mat frame) {
        Rect roi = settings.getActiveVideoInput().getSettings().getRegionOfInterest().getRoi();
        if (roi == null)
            return frame;

        return new Mat(frame, roi);
    }

    private void sendDetectedArrows(Mat frame) throws InterruptedException {
        for (Iterator<ArrowPositionGroup> iterator = arrowPositionGroups.iterator(); iterator.hasNext(); ) {
            ArrowPositionGroup group = iterator.next();
            if (group.groupTimedOut()) {
                //saveGroupToFile(group);
                ArrowPosition position = new ArrowPosition(group.getPointA(),
                        group.getPointB(),
                        settings.getActiveVideoInput().getSettings().getArrowDetection().getPointZero(),
                        settings.getActiveVideoInput().getSettings().getArrowDetection().getPointHundred());
                //Platform.runLater(() -> settings.getHits().add(position));
                //ToDo: hier müssen die Messages verschickt werden
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
            ArrowPosition position = new ArrowPosition(
                    new Point(l[0], l[1]),
                    new Point(l[2], l[3]),
                    settings.getActiveVideoInput().getSettings().getArrowDetection().getPointZero(),
                    settings.getActiveVideoInput().getSettings().getArrowDetection().getPointHundred()
            );
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

    private Mat perspectiveCorrection(Mat frame) {
        Mat transformedImage = new Mat();
        Imgproc.warpPerspective(frame, transformedImage, warp, frame.size());
        return transformedImage;
    }

    private Mat undistore(Mat frame) {
        if (!settings.getActiveVideoInput().getSettings().getCameraCalibration().isCalibrated()) {
            return frame;
        }
        Mat undistored = new Mat();
        Calib3d.undistort(frame, undistored, settings.getActiveVideoInput().getSettings().getCameraCalibration().getIntrinsic(), settings.getActiveVideoInput().getSettings().getCameraCalibration().getDistCoeffs());
        return undistored;
    }

    private void initCapture() {
        if (settings == null || settings.getActiveVideoInput() == null)
            return;

        // try to get an Video Capture device
        VideoInput activeVideoInput = settings.getActiveVideoInput();
        capture = new VideoCapture();
        activeVideoInput.openCapture(capture);

        // is the video stream available?
        if (!capture.isOpened()) {
            //ToDo: ExcaptionHandling
            System.err.println("Impossible to open the camera connection...");
            return;
        }
    }
}
