package de.woodpecker.archery.arrowdetection.imageprocessors;

import de.woodpecker.archery.arrowdetection.captures.VideoInput;
import de.woodpecker.archery.arrowdetection.persistance.PerspectiveCorrectionImageProcessorSettings;
import de.woodpecker.archery.arrowdetection.utils.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

public class PerspectiveCorrectionImageProcessor extends ImageProcessor {
    private PerspectiveCorrectionImageProcessorSettings settings;
    private Mat warp;

    public PerspectiveCorrectionImageProcessor(VideoInput videoInput) {
        super(videoInput);
        settings = videoInput.getSettings().getPerspectiveCorrection();
    }

    @Override
    public void setVideoInput(VideoInput videoInput) {
        super.setVideoInput(videoInput);
        settings = videoInput.getSettings().getPerspectiveCorrection();
    }

    public void prepareWarp() {
        // wenn noch nicht alle Punkte angelegt oder punkte auf den selben Pixel zeigen, abbrechen
        if (!settings.allPerspectiveCorrectionPointsSet()) {
            return;
        }
        // sind alle 4 Punkte vorhanden, das Warp Material erzeugen
        createWarpMat(settings.getPerspectiveCorrectionPoints());
    }

    @Override
    protected Mat doProcessMat(Mat sourceMat) {
        return getTransformedImage(sourceMat);
    }

    private void createWarpMat(Point[] points) throws IllegalArgumentException {
        if (points.length != 4 || points[0] == null || points[1] == null || points[2] == null || points[3] == null) {
            String msg = "Für die Berechnung werden genau 4 Punkte benötigt, es wurden aber folgede Punkte übergeben:" + System.lineSeparator();
            for (int i = 0; i < points.length; i++) {
                if (points[i] != null)
                    msg += "Punkt " + i + ": " + points[i].toString() + System.lineSeparator();
            }
            throw new IllegalArgumentException(msg);
        }

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

    public Mat getTransformedImage(Mat image) {
        // sollten nicht alle Punkte zur transformation angegeben sein, das originalbild zurückliefern
        if (warp == null) {
            prepareWarp();
            return image;
        }

        Mat transformedImage = new Mat();
        Imgproc.warpPerspective(image, transformedImage, warp, image.size());
        return transformedImage;
    }
}
