package de.woodpecker.archery.arrowdetection.imageprocessors;

import de.woodpecker.archery.arrowdetection.captures.VideoInput;
import de.woodpecker.archery.arrowdetection.persistance.Display4EdgesImageProcessorSettings;
import de.woodpecker.archery.arrowdetection.persistance.PerspectiveCorrectionImageProcessorSettings;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class Display4EdgesImageProcessor extends ImageProcessor {
    private Display4EdgesImageProcessorSettings settings;
    private PerspectiveCorrectionImageProcessorSettings perspectiveSettings;

    public Display4EdgesImageProcessor(VideoInput videoInput) {
        super(videoInput);
        settings = videoInput.getSettings().getDisplay4Edges();
        perspectiveSettings = videoInput.getSettings().getPerspectiveCorrection();
    }

    @Override
    public void setVideoInput(VideoInput videoInput) {
        super.setVideoInput(videoInput);
        settings = videoInput.getSettings().getDisplay4Edges();
        perspectiveSettings = videoInput.getSettings().getPerspectiveCorrection();
    }

    @Override
    protected Mat doProcessMat(Mat sourceMat) {
        // Linien Anzeigen
        if (perspectiveSettings.getPerspectiveCorrectionPoints()[0] != null) {
            if (perspectiveSettings.getPerspectiveCorrectionPoints()[1] != null)
                Imgproc.line(sourceMat, perspectiveSettings.getPerspectiveCorrectionPoints()[0], perspectiveSettings.getPerspectiveCorrectionPoints()[1], settings.getColorRect());
            if (perspectiveSettings.getPerspectiveCorrectionPoints()[2] != null)
                Imgproc.line(sourceMat, perspectiveSettings.getPerspectiveCorrectionPoints()[0], perspectiveSettings.getPerspectiveCorrectionPoints()[2], settings.getColorRect());
        }
        if (perspectiveSettings.getPerspectiveCorrectionPoints()[3] != null) {
            if (perspectiveSettings.getPerspectiveCorrectionPoints()[1] != null)
                Imgproc.line(sourceMat, perspectiveSettings.getPerspectiveCorrectionPoints()[1], perspectiveSettings.getPerspectiveCorrectionPoints()[3], settings.getColorRect());
            if (perspectiveSettings.getPerspectiveCorrectionPoints()[2] != null)
                Imgproc.line(sourceMat, perspectiveSettings.getPerspectiveCorrectionPoints()[2], perspectiveSettings.getPerspectiveCorrectionPoints()[3], settings.getColorRect());
        }

        // selektierte Punkte anzeigen
        for (int i = 0; i < perspectiveSettings.getPerspectiveCorrectionPoints().length; i++) {
            if (perspectiveSettings.getPerspectiveCorrectionPoints()[i] != null)
                Imgproc.drawMarker(sourceMat, perspectiveSettings.getPerspectiveCorrectionPoints()[i], settings.getColorMarker());
        }

        return sourceMat;
    }
}
