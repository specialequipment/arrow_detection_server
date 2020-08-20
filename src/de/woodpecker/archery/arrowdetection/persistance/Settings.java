package de.woodpecker.archery.arrowdetection.persistance;

import java.io.Serializable;

public class Settings implements Serializable {
    private int cameraId;
    private ArrowDetectionImageProcessorSettings arrowDetection = new ArrowDetectionImageProcessorSettings();
    private BackgroundRemoverImageProcessorSettings backgroundRemover = new BackgroundRemoverImageProcessorSettings();
    private CameraCalibrationImageProcessorSettings cameraCalibration = new CameraCalibrationImageProcessorSettings();
    private Display4EdgesImageProcessorSettings display4Edges = new Display4EdgesImageProcessorSettings();

    private DisplayArrowsImageProcessorSettings displayArrows = new DisplayArrowsImageProcessorSettings();
    private DisplayRectangleImageProcessorSettings displayRectangle = new DisplayRectangleImageProcessorSettings();
    private PerspectiveCorrectionImageProcessorSettings perspectiveCorrection = new PerspectiveCorrectionImageProcessorSettings();
    private RegionOfInterestImageProcessorSettings regionOfInterest = new RegionOfInterestImageProcessorSettings();

    public Settings() {
    }

    public int getCameraId() {
        return cameraId;
    }

    public void setCameraId(int cameraId) {
        this.cameraId = cameraId;
    }

    public ArrowDetectionImageProcessorSettings getArrowDetection() {
        return arrowDetection;
    }

    public void setArrowDetection(ArrowDetectionImageProcessorSettings arrowDetection) {
        this.arrowDetection = arrowDetection;
    }

    public BackgroundRemoverImageProcessorSettings getBackgroundRemover() {
        return backgroundRemover;
    }

    public void setBackgroundRemover(BackgroundRemoverImageProcessorSettings backgroundRemover) {
        this.backgroundRemover = backgroundRemover;
    }

    public CameraCalibrationImageProcessorSettings getCameraCalibration() {
        return cameraCalibration;
    }

    public void setCameraCalibration(CameraCalibrationImageProcessorSettings cameraCalibration) {
        this.cameraCalibration = cameraCalibration;
    }

    public Display4EdgesImageProcessorSettings getDisplay4Edges() {
        return display4Edges;
    }

    public void setDisplay4Edges(Display4EdgesImageProcessorSettings display4Edges) {
        this.display4Edges = display4Edges;
    }

    public DisplayArrowsImageProcessorSettings getDisplayArrows() {
        return displayArrows;
    }

    public void setDisplayArrows(DisplayArrowsImageProcessorSettings displayArrows) {
        this.displayArrows = displayArrows;
    }

    public DisplayRectangleImageProcessorSettings getDisplayRectangle() {
        return displayRectangle;
    }

    public void setDisplayRectangle(DisplayRectangleImageProcessorSettings displayRectangle) {
        this.displayRectangle = displayRectangle;
    }

    public PerspectiveCorrectionImageProcessorSettings getPerspectiveCorrection() {
        return perspectiveCorrection;
    }

    public void setPerspectiveCorrection(PerspectiveCorrectionImageProcessorSettings perspectiveCorrection) {
        this.perspectiveCorrection = perspectiveCorrection;
    }

    public RegionOfInterestImageProcessorSettings getRegionOfInterest() {
        return regionOfInterest;
    }

    public void setRegionOfInterest(RegionOfInterestImageProcessorSettings regionOfInterest) {
        this.regionOfInterest = regionOfInterest;
    }
}
