package de.woodpecker.archery.arrowdetection.captures;

import de.woodpecker.archery.arrowdetection.persistance.Settings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import org.opencv.videoio.VideoCapture;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;

public abstract class VideoInput {
    private static final String SETTINGS_FILE_NAME = "D:\\target\\settings_";
    private static final String SETTINGS_FILE_EXTENSION = ".xml";
    protected final StringProperty displayName = new SimpleStringProperty();
    protected Settings settings;

    public VideoInput() {
        settings = new Settings();
    }

    public abstract Image getPreviewImage();

    public abstract String getId();

    public String getDisplayName() {
        return displayName.getValue();
    }

    public StringProperty displayNameProperty() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName.getValue();
    }

    public abstract void openCapture(VideoCapture capture);

    public void loadSettings() {
        File file = new File(SETTINGS_FILE_NAME + getId() + SETTINGS_FILE_EXTENSION);
        if (!file.exists())
            return;

        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            //no Settings found, exit
            return;
        }
        XMLDecoder decoder = new XMLDecoder(fis);
        Settings decodedSettings = (Settings) decoder.readObject();
        decoder.close();
        try {
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.settings = decodedSettings;
    }


    public void save() {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(SETTINGS_FILE_NAME + getId() + SETTINGS_FILE_EXTENSION);
            XMLEncoder encoder = null;
            try {
                encoder = new XMLEncoder(fos);
                encoder.setExceptionListener(e -> System.out.println("Exception! :" + e.toString()));
                encoder.writeObject(settings);
            } finally {
                if (encoder != null)
                    encoder.close();
                fos.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public Settings getSettings() {
        return settings;
    }

}
