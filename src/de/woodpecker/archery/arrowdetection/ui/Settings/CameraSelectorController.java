package de.woodpecker.archery.arrowdetection.ui.Settings;

import de.woodpecker.archery.arrowdetection.captures.Camera;
import de.woodpecker.archery.arrowdetection.captures.VideoFile;
import de.woodpecker.archery.arrowdetection.captures.VideoInput;
import de.woodpecker.archery.arrowdetection.imageprocessors.SimpleImageProcessor;
import de.woodpecker.archery.arrowdetection.ui.AnalyserController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.converter.NumberStringConverter;


public class CameraSelectorController extends AnalyserController {
    @FXML
    ListView<VideoInput> videoInputListView;
    @FXML
    Label headline;
    @FXML
    TextField width;
    @FXML
    TextField height;
    @FXML
    Label labelWidth;
    @FXML
    Label labelHeight;
    @FXML
    TextField dispalayName;

    public CameraSelectorController() {
        mainImageProcessor = new SimpleImageProcessor(settings.getActiveVideoInput());

    }

    @FXML
    public void initialize() {
        initCameraListItemView();
        videoInputListView.getSelectionModel().selectedItemProperty().addListener(
                (ov, old_val, new_val) -> setSelectedCamera(old_val, new_val));
        initCameraList();
    }

    private void initCameraListItemView() {
        // Setup the CellFactory
        videoInputListView.setCellFactory(listView -> new ListCell<VideoInput>() {
            {
                prefWidthProperty().bind(videoInputListView.widthProperty().subtract(20));
                setMaxWidth(Control.USE_PREF_SIZE);
            }

            @Override
            protected void updateItem(VideoInput videoInput, boolean empty) {
                super.updateItem(videoInput, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    // Beschreibung
                    VBox vBox = new VBox();

                    // Das Vorschaubild
                    ImageView previewImage = new ImageView(videoInput.getPreviewImage());
                    previewImage.setSmooth(true);
                    previewImage.setPreserveRatio(true);
                    previewImage.fitWidthProperty().bind(this.widthProperty().subtract(20));

                    vBox.getChildren().addAll(
                            previewImage,
                            new Label("ID: " + videoInput.getId()),
                            new Label(videoInput.getDisplayName())
                    );


                    // Set the item as the display
                    setGraphic(vBox);
                }
            }
        });
    }

    @FXML
    private void openFile() {
        // Dateiauswahldialog anzeigen
        String file = "D:/DevelopJ/Bildanalyse/WIN_20191003_11_19_17_Pro.mp4";
        VideoFile videoFile = new VideoFile(file);
        settings.addVideoAndActivate(videoFile);

        applicationController.startPreview();
    }

    private void initCameraList() {
        if (settings == null)
            return;
        if (videoInputListView.itemsProperty().isBound())
            videoInputListView.itemsProperty().unbind();
        videoInputListView.setItems(settings.getAvailableVideoInput());
    }

    private void setSelectedCamera(VideoInput oldVideoInput, VideoInput newVideoInput) {
        // unbind
        if (oldVideoInput != null) {
            unbindCameraProperties(oldVideoInput);
            unbindFileProperties(oldVideoInput);
            // unbind
            headline.textProperty().unbind();
            dispalayName.textProperty().unbindBidirectional(oldVideoInput.displayNameProperty());
        }

        //bind
        if (newVideoInput != null) {
            bindCameraProperies(newVideoInput);
            bindFileProperties(newVideoInput);

            // bind
            headline.textProperty().bind(newVideoInput.displayNameProperty());
            dispalayName.textProperty().bindBidirectional(newVideoInput.displayNameProperty());
        }

        settings.setActiveVideoInput(newVideoInput);
        applicationController.startPreview();
    }

    private void bindFileProperties(VideoInput newVideoInput) {
        if (!(newVideoInput instanceof VideoFile))
            return;
        height.setVisible(false);
        labelHeight.setVisible(false);
        width.setVisible(false);
        labelWidth.setVisible(false);
    }

    private void bindCameraProperies(VideoInput newVideoInput) {
        if (!(newVideoInput instanceof Camera))
            return;
        Camera camera = (Camera) newVideoInput;
        width.textProperty().bindBidirectional(camera.widthProperty(), new NumberStringConverter());
        width.setVisible(true);
        labelWidth.setVisible(true);
        height.textProperty().bindBidirectional(camera.heightProperty(), new NumberStringConverter());
        height.setVisible(true);
        labelHeight.setVisible(true);
    }

    private void unbindFileProperties(VideoInput oldVideoInput) {
        if (!(oldVideoInput instanceof VideoFile))
            return;

    }

    private void unbindCameraProperties(VideoInput oldVideoInput) {
        if (!(oldVideoInput instanceof Camera))
            return;
        Camera camera = (Camera) oldVideoInput;
        width.textProperty().unbindBidirectional(camera.widthProperty());
        height.textProperty().unbindBidirectional(camera.heightProperty());

    }


    @FXML
    private void saveAndClose() {
        if (applicationController != null) {
            applicationController.afterSafe();
            applicationController.closeCurrentWindow();
        }
    }

    @Override
    public boolean canCloseWindow() {
        return true;
    }
}
