package de.woodpecker.archery.arrowdetection.cameraprocessors;

import de.woodpecker.archery.arrowdetection.captures.Camera;
import javafx.scene.image.Image;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.util.concurrent.BlockingQueue;

import static de.woodpecker.archery.arrowdetection.utils.Utils.mat2Image;
import static org.opencv.videoio.Videoio.*;

public class CamerasProducer implements Runnable {
    private final static int[] types = {CAP_DSHOW, CAP_VFW, CAP_FIREWIRE, CAP_QT, CAP_UNICAP, CAP_MSMF, CAP_PVAPI, CAP_OPENNI, CAP_OPENNI2, CAP_OPENNI_ASUS, CAP_OPENNI2_ASUS, CAP_XIAPI, CAP_XINE, CAP_AVFOUNDATION, CAP_GIGANETIX, CAP_INTELPERC};
    private final Camera poisonPill;
    private final BlockingQueue<Camera> cameraQueue;
    VideoCapture camera = new VideoCapture();


    public CamerasProducer(BlockingQueue<Camera> cameraQueue, Camera poisonPill) {
        this.cameraQueue = cameraQueue;
        this.poisonPill = poisonPill;
    }

    private void searchForAvailableCameras() throws InterruptedException {
        for (int i = 0; i < types.length; i++) {
            for (int j = 0; j < 100; j++) {
                reolveCameraForIndex(j + types[i], types[i]);
            }
        }
        cameraQueue.put(poisonPill);
    }

    private void reolveCameraForIndex(int cameraIndex, int type) throws InterruptedException {
        try {
            camera.open(cameraIndex);
            if (camera.isOpened()) {
                String name = camera.getBackendName();
                Camera foundCamera = new Camera(type, cameraIndex, name);
                Mat frame = new Mat();
                int k = 10; //ggf. TimeOut verwenden falls die Preview Bilder nicht gut sind (automatische Belichtung)
                while (k > 0) {
                    if (camera.read(frame)) {
                        k--;
                    }
                }
                Image cameraPreview = mat2Image(frame);
                foundCamera.setPreviewImage(cameraPreview);
                cameraQueue.put(foundCamera);
            }
        } finally {
            camera.release();
        }
    }

    @Override
    public void run() {
        try {
            searchForAvailableCameras();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
