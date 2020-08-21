package de.woodpecker.archery.arrowdetection.ui;

import de.woodpecker.archery.arrowdetection.AnalyserSettings;
import de.woodpecker.archery.arrowdetection.imageprocessors.ImageProcessor;

public abstract class AnalyserController {
    protected static final AnalyserSettings settings = new AnalyserSettings();
    protected ApplicationController applicationController;
    protected ImageProcessor mainImageProcessor;

    public AnalyserController() {
    }

    public static AnalyserSettings getSettings() {
        return settings;
    }

    public void setResultVisualizationController(VisualizationController resultVisualizationController) {
        if (mainImageProcessor != null) {
            mainImageProcessor.setResultController(resultVisualizationController);
        }
    }

    public void setApplicationController(ApplicationController applicationController) {
        this.applicationController = applicationController;
        applicationController.setImageProcessor(mainImageProcessor);
        applicationControllerChanged();
    }

    protected void applicationControllerChanged() {
    }

    public abstract boolean canCloseWindow();

}
