package de.woodpecker.archery.arrowdetection.imageprocessors;

import de.woodpecker.archery.arrowdetection.captures.VideoInput;
import de.woodpecker.archery.arrowdetection.persistance.CameraCalibrationImageProcessorSettings;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;

public class CameraCalibrationImageProcessor extends ImageProcessor {
    private CameraCalibrationImageProcessorSettings settings;

    public CameraCalibrationImageProcessor(VideoInput videoInput) {
        super(videoInput);
        settings = videoInput.getSettings().getCameraCalibration();
    }

    @Override
    public void setVideoInput(VideoInput videoInput) {
        super.setVideoInput(videoInput);
        settings = videoInput.getSettings().getCameraCalibration();
    }

    @Override
    protected Mat doProcessMat(Mat sourceMat) {
        if (!settings.isCalibrated()) {
            return sourceMat;
        }
        Mat undistored = new Mat();
        Calib3d.undistort(sourceMat, undistored, settings.getIntrinsic(), settings.getDistCoeffs());
        return undistored;
    }
}
