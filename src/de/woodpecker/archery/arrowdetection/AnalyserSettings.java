package de.woodpecker.archery.arrowdetection;

import de.woodpecker.archery.arrowdetection.captures.Camera;
import de.woodpecker.archery.arrowdetection.captures.VideoFile;
import de.woodpecker.archery.arrowdetection.captures.VideoInput;
import de.woodpecker.archery.arrowdetection.utils.SettingsLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class AnalyserSettings {
    private final List<VideoInput> videoInputs = new ArrayList<>();
    private final ObservableList<VideoInput> availableVideoInput = FXCollections.observableList(videoInputs);
    private VideoInput activeVideoInput;

    public AnalyserSettings() {
        SettingsLoader loader = new SettingsLoader(this);
    }

    public void addVideoAndActivate(VideoFile videoFile) {
        availableVideoInput.add(videoFile);
        setActiveVideoInput(videoFile);
    }

    public VideoInput getActiveVideoInput() {
        return activeVideoInput;
    }

    public void setActiveVideoInput(VideoInput activeVideoInput) {
        if (this.activeVideoInput != null)
            this.activeVideoInput.save();
        this.activeVideoInput = activeVideoInput;
        this.activeVideoInput.loadSettings();
    }

    public void addCameraToList(Camera cam) {
        availableVideoInput.add(cam);
    }

    public ObservableList<VideoInput> getAvailableVideoInput() {
        return availableVideoInput;
    }
}
