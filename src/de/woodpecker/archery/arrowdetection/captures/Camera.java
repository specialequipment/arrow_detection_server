package de.woodpecker.archery.arrowdetection.captures;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;
import org.opencv.videoio.VideoCapture;

import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_HEIGHT;
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_WIDTH;

public class Camera extends VideoInput {

    private final IntegerProperty driver = new SimpleIntegerProperty();
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final DoubleProperty width = new SimpleDoubleProperty();
    private final DoubleProperty height = new SimpleDoubleProperty();
    private final String backendName;
    private Image previewImage;

    public Camera(int driver, int id, String backendName) {
        this.driver.set(driver);
        this.id.set(id);
        this.backendName = backendName;
        this.displayName.set("Kamera " + id + " " + backendName);
        getSettings().setCameraId(id);
    }

    public String getBackendName() {
        return backendName;
    }

    public double getWidth() {
        return width.get();
    }

    public void setWidth(double width) {
        this.width.set(width);
    }

    public double getHeight() {
        return height.get();
    }

    public void setHeight(double height) {
        this.height.set(height);
    }

    public Image getPreviewImage() {
        return previewImage;
    }

    public void setPreviewImage(Image previewImage) {
        this.previewImage = previewImage;
        setWidth(previewImage.getWidth());
        setHeight(previewImage.getHeight());
    }

    @Override
    public void openCapture(VideoCapture capture) {
        if (getWidth() > 0 && getHeight() > 0) {
            capture.set(CAP_PROP_FRAME_WIDTH, getWidth());
            capture.set(CAP_PROP_FRAME_HEIGHT, getHeight());
        } else {
            capture.set(CAP_PROP_FRAME_WIDTH, 1920);
            capture.set(CAP_PROP_FRAME_HEIGHT, 1080);
        }

        // start the video capture
        capture.open(id.intValue());
    }

    public int getDriver() {
        return driver.get();
    }

    public IntegerProperty driverProperty() {
        return driver;
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public DoubleProperty widthProperty() {
        return width;
    }

    public DoubleProperty heightProperty() {
        return height;
    }

    @Override
    public String getId() {
        return id.getValue().toString();
    }
}
