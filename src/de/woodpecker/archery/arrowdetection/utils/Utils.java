package de.woodpecker.archery.arrowdetection.utils;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Provide general purpose methods for handling OpenCV-JavaFX data conversion.
 * Moreover, expose some "low level" methods for matching few JavaFX behavior.
 *
 * @author <a href="mailto:luigi.derussis@polito.it">Luigi De Russis</a>
 * @author <a href="http://max-z.de">Maximilian Zuleger</a>
 * @version 1.0 (2016-09-17)
 * @since 1.0
 */
public final class Utils {
    /**
     * Convert a Mat object (OpenCV) in the corresponding Image for JavaFX
     *
     * @param frame the {@link Mat} representing the current frame
     * @return the {@link Image} to show
     */
    public static Image mat2Image(Mat frame) {
        try {
            return SwingFXUtils.toFXImage(matToBufferedImage(frame), null);
        } catch (Exception e) {
            System.err.println("Cannot convert the Mat obejct: " + e);
            return null;
        }
    }

    public static double distance(Point a, Point b) {
        if (a == null || b == null)
            return Double.MAX_VALUE;
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    public static boolean allPointsSet(Point[] points) {
        return points[0] != null && points[1] != null && points[2] != null && points[3] != null;
    }

    public static Point nearestPointInRange(Point clickedPosition, double maxDistance, Point[] points) {
        double nearestDist = Double.MAX_VALUE;
        Point nearestPoint = null;
        for (Point point : points) {
            double dist = distance(clickedPosition, point);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearestPoint = point;
            }
        }

        if (nearestDist < maxDistance) {
            return nearestPoint;
        } else {
            return null;
        }
    }

    /**
     * Generic method for putting element running on a non-JavaFX thread on the
     * JavaFX thread, to properly update the UI
     *
     * @param property a {@link ObjectProperty}
     * @param value    the value to set for the given {@link ObjectProperty}
     */
    public static <T> void onFXThread(final ObjectProperty<T> property, final T value) {
        Platform.runLater(() -> property.set(value));
    }

    /**
     * helper function to convert the Image, mostly used in mat2Image
     *
     * @param original the {@link Mat} object in BGR or grayscale
     * @return the corresponding {@link BufferedImage}
     */
    public static BufferedImage matToBufferedImage(Mat original) {
        // init
        BufferedImage image;
        int width = original.width(), height = original.height(), channels = original.channels();
        byte[] sourcePixels = new byte[width * height * channels];
        original.get(0, 0, sourcePixels);

        if (original.channels() > 1) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        } else {
            image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        }
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

        return image;
    }
}
