package de.woodpecker.archery.arrowdetection.ui;

import de.woodpecker.archery.arrowdetection.imageprocessors.ImageProcessor;

public interface ApplicationController {
    void closeCurrentWindow();

    void afterSafe();

    void startPreview();

    void stopPreview();

    void setImageProcessor(ImageProcessor imageProcessor);
}
