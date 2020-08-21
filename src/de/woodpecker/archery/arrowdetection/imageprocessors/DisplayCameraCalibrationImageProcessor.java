package de.woodpecker.archery.arrowdetection.imageprocessors;

import de.woodpecker.archery.arrowdetection.captures.VideoInput;
import de.woodpecker.archery.arrowdetection.persistance.CameraCalibrationImageProcessorSettings;
import javafx.fxml.FXML;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DisplayCameraCalibrationImageProcessor extends ImageProcessor {
    private final List<Mat> imagePoints = new ArrayList<>();
    private final List<Mat> objectPoints = new ArrayList<>();
    private final MatOfPoint3f obj = new MatOfPoint3f();
    private final int numCornersHor = 9;
    private final int numCornersVer = 6;
    private final Mat lastDetectedChessboardImage = new Mat();
    // various variables needed for the calibration
    private CameraCalibrationImageProcessorSettings settings;
    private MatOfPoint2f imageCorners = new MatOfPoint2f();
    //private final Mat intrinsic = new Mat(3, 3, CvType.CV_32FC1);
    //private final Mat distCoeffs = new Mat();
    private Date lastCaptured = new Date();

    public DisplayCameraCalibrationImageProcessor(VideoInput videoInput) {
        super(videoInput);
        settings = videoInput.getSettings().getCameraCalibration();
        int numSquares = numCornersHor * numCornersVer;
        for (int j = 0; j < numSquares; j++)
            obj.push_back(new MatOfPoint3f(new Point3((double) j / this.numCornersHor, j % this.numCornersVer, 0.0f)));
    }

    @Override
    public void setVideoInput(VideoInput videoInput) {
        super.setVideoInput(videoInput);
        settings = videoInput.getSettings().getCameraCalibration();
    }

    @Override
    protected Mat doProcessMat(Mat sourceMat) {
        return findAndDrawPoints(sourceMat);
    }

    /**
     * Find and draws the points needed for the calibration on the chessboard
     *
     * @param frame the current frame
     * @return the current number of successfully identified chessboards as an
     * int
     */
    private Mat findAndDrawPoints(Mat frame) {
        if (settings.isCalibrated()) {
            Imgproc.putText(frame, "Kalibrierung abgeschlossen", new Point(20, 50), Imgproc.FONT_HERSHEY_COMPLEX, 1, new Scalar(255, 255, 255));
            return frame;
        }

        // init
        Mat grayImage = new Mat();

        // convert the frame in gray scale
        Imgproc.cvtColor(frame, grayImage, Imgproc.COLOR_BGR2GRAY);

        // the size of the chessboard
        Size boardSize = new Size(numCornersVer, numCornersHor);

        // look for the inner chessboard corners
        boolean found = Calib3d.findChessboardCorners(grayImage, boardSize, imageCorners,
                Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_NORMALIZE_IMAGE + Calib3d.CALIB_CB_FAST_CHECK);

        // Display Captured
        Imgproc.putText(frame, "Detected (" + imagePoints.size() + "): " + lastCaptured.getTime(), new Point(20, 50), Imgproc.FONT_HERSHEY_COMPLEX, 1, new Scalar(255, 255, 255));

        // not all the required corners have been found...
        if (!found)
            return frame;

        // optimization
        TermCriteria term = new TermCriteria(TermCriteria.EPS | TermCriteria.MAX_ITER, 30, 0.1);
        Imgproc.cornerSubPix(grayImage, imageCorners, new Size(11, 11), new Size(-1, -1), term);

        // take Snapshot
        takeSnapshot();

        // save the current frame for further elaborations
        grayImage.copyTo(lastDetectedChessboardImage);

        // show the chessboard inner corners on screen
        Calib3d.drawChessboardCorners(frame, boardSize, imageCorners, true); //ToDo: In eigenen Visualisierer auslagern
        return frame;
    }

    /**
     * Take a snapshot to be used for the calibration process
     */
    @FXML
    private void takeSnapshot() {
        Date date = new Date();

        if (date.getTime() - lastCaptured.getTime() < 5000) {
            return;
        }

        lastCaptured = date;

        int boardsNumber = 20;
        if (imagePoints.size() < boardsNumber) {
            // save all the needed values
            imagePoints.add(imageCorners);
            imageCorners = new MatOfPoint2f();
            objectPoints.add(obj);
        }

        // reach the correct number of images needed for the calibration
        if (imagePoints.size() == boardsNumber) {
            calibrateCamera();
        }
    }

    /**
     * The effective camera calibration, to be performed once in the program
     * execution
     */
    private void calibrateCamera() {
        // init needed variables according to OpenCV docs
        List<Mat> rvecs = new ArrayList<>();
        List<Mat> tvecs = new ArrayList<>();
        settings.getIntrinsic().put(0, 0, 1);
        settings.getIntrinsic().put(1, 1, 1);
        // calibrate!
        Calib3d.calibrateCamera(objectPoints, imagePoints, lastDetectedChessboardImage.size(), settings.getIntrinsic(), settings.getDistCoeffs(), rvecs, tvecs);
        settings.setCalibrated(true);
    }
}
