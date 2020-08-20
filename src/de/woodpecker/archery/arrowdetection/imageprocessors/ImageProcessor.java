package de.woodpecker.archery.arrowdetection.imageprocessors;

import de.woodpecker.archery.arrowdetection.captures.VideoInput;
import de.woodpecker.archery.arrowdetection.ui.VisualizationController;
import de.woodpecker.archery.arrowdetection.utils.Utils;
import javafx.scene.image.Image;
import org.opencv.core.Mat;

public abstract class ImageProcessor {
    //protected AnalyserSettings settings;
    protected VideoInput videoInput;
    private ImageProcessor preProcessor;
    private ImageProcessor visualizationProcessor;
    private VisualizationController resultController;
    private VisualizationController visualizationController;

    public ImageProcessor(VideoInput videoInput) {
        this.videoInput = videoInput;
    }

    public VideoInput getVideoInput() {
        return videoInput;
    }

    public void setVideoInput(VideoInput videoInput) {
        this.videoInput = videoInput;
        if (preProcessor != null)
            preProcessor.setVideoInput(videoInput);
        if (visualizationProcessor != null)
            visualizationProcessor.setVideoInput(videoInput);
    }

    /*public void setSettings(AnalyserSettings settings) {
        this.settings = settings;
        if (preProcessor != null)
            preProcessor.setSettings(settings);
        if (visualizationProcessor != null)
            visualizationProcessor.setSettings(settings);
    }*/

    public void setPreProcessor(ImageProcessor preProcessor) {
        this.preProcessor = preProcessor;
    }

    public void setVisualizationProcessor(ImageProcessor visualizationProcessor) {
        this.visualizationProcessor = visualizationProcessor;
    }

    public void setResultController(VisualizationController resultController) {
        this.resultController = resultController;
    }

    public void setVisualizationController(VisualizationController visualizationController) {
        this.visualizationController = visualizationController;
    }

    public Mat processMat(Mat sourceMat) throws InterruptedException {
        // ggf. vorgelagerte Bildverarbeitung durchführen
        if (preProcessor != null)
            sourceMat = preProcessor.processMat(sourceMat);
        // ggf. zusätzliche Visualisierung / alternative Visualisierung
        if (visualizationProcessor != null && visualizationController != null) {
            Mat visualizationMat = sourceMat.clone();
            visualizationMat = visualizationProcessor.processMat(visualizationMat);
            Image image = Utils.mat2Image(visualizationMat);
            visualizationController.changeImage(image);
        }
        // Die Bildverarbeitung durchführen und ggf. Visualisieren
        sourceMat = doProcessMat(sourceMat);
        if (resultController != null) {
            Image image = Utils.mat2Image(sourceMat);
            resultController.changeImage(image);
        }

        return sourceMat;
    }

    /**
     * Implementierung der konkreten Bildverarbeitung
     *
     * @param sourceMat
     * @return
     */
    protected abstract Mat doProcessMat(Mat sourceMat) throws InterruptedException;

    /*public AnalyserSettings getSettings() {
        return settings;
    }*/
}
