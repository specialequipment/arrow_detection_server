package de.woodpecker.archery.arrowdetection.utils;

import de.woodpecker.archery.arrowdetection.AnalyserSettings;
import de.woodpecker.archery.arrowdetection.cameraprocessors.CamerasProducer;
import de.woodpecker.archery.arrowdetection.captures.Camera;
import javafx.application.Platform;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class SettingsLoader {
    private final Camera poisonPill;
    private final BlockingQueue<Camera> cameraBlockingQueue;
    private final AnalyserSettings settings;

    public SettingsLoader(AnalyserSettings settings) {
        this.settings = settings;
        this.cameraBlockingQueue = new LinkedBlockingDeque<>();
        this.poisonPill = new Camera(0, 0, "Poison Pill");
        new Thread(new CamerasProducer(cameraBlockingQueue, poisonPill)).start();
        loadAvailableCameras();
    }

    public void loadAvailableCameras() {
        Timer timer = new Timer();
        TimerTask frameGrabber = new TimerTask() {
            @Override
            public void run() {
                try {
                    final Camera cam = cameraBlockingQueue.take();
                    if (cam.equals(poisonPill)) {
                        timer.cancel();
                        timer.purge();
                    } else {
                        Platform.runLater(() -> settings.addCameraToList(cam));
                    }
                } catch (InterruptedException e) {
                    timer.cancel();
                    timer.purge();
                }
            }
        };

        timer.schedule(frameGrabber, 0, 100);
    }
}
