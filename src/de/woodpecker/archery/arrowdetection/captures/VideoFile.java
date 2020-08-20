package de.woodpecker.archery.arrowdetection.captures;

import javafx.scene.image.Image;
import org.opencv.videoio.VideoCapture;

import java.io.File;

public class VideoFile extends VideoInput {
    private final String fileName;
    private final File file;

    public VideoFile(String fileName) {
        this.file = new File(fileName);
        if (!this.file.exists())
            throw new IllegalArgumentException("Datei '" + fileName + "' existiert nicht");

        this.fileName = file.getName();
        displayNameProperty().setValue(this.fileName);
    }

    @Override
    public Image getPreviewImage() {
        return null;
    }

    @Override
    public void openCapture(VideoCapture capture) {
        capture.open(file.getPath());
    }

    @Override
    public String getId() {
        return fileName.replaceAll("\\W", "_");
    }
}
