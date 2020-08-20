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
    final static String settingsFileName = "D:\\target\\settings_";
    final static String settingsFileExtension = ".xml";
    protected final StringProperty displayName = new SimpleStringProperty();
    protected Settings settings;

    public VideoInput() {
        if (settings == null) {
            settings = new Settings();
        }
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
        File file = new File(settingsFileName + getId() + settingsFileExtension);
        if (!file.exists())
            return;

        FileInputStream fis = null;
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
            fos = new FileOutputStream(settingsFileName + getId() + settingsFileExtension);
            XMLEncoder encoder = new XMLEncoder(fos);
            encoder.setExceptionListener(e -> System.out.println("Exception! :" + e.toString()));
            encoder.writeObject(settings);
            encoder.close();
            fos.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public Settings getSettings() {
        return settings;
    }

}
