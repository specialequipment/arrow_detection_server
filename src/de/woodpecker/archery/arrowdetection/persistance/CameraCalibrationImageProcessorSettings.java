package de.woodpecker.archery.arrowdetection.persistance;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.Serializable;

public class CameraCalibrationImageProcessorSettings implements Serializable {
    //ToDo: klären, wie das korrekt gespeichert wird
    private final Mat intrinsic = new Mat(3, 3, CvType.CV_32FC1);
    private final Mat distCoeffs = new Mat();
    private boolean calibrated = false;

    public Mat getIntrinsic() {
        return intrinsic;
    }

    public Mat getDistCoeffs() {
        return distCoeffs;
    }

    public boolean isCalibrated() {
        return calibrated;
    }

    public void setCalibrated(boolean calibrated) {
        this.calibrated = calibrated;
    }
}
