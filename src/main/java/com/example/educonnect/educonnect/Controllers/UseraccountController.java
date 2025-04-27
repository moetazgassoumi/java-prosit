package com.example.educonnect.educonnect.Controllers;

import com.example.educonnect.educonnect.Entities.*;
import com.example.educonnect.educonnect.Main;
import com.example.educonnect.educonnect.Repository.*;
import com.example.educonnect.educonnect.Utils.FaceDetector;
import com.example.educonnect.educonnect.Utils.Modals;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.*;
import javafx.util.Duration;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import org.bytedeco.opencv.global.opencv_imgcodecs;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static com.example.educonnect.educonnect.Utils.Validator.isValidEmail;

public class UseraccountController implements Initializable {

    public Button Home_Btn, Users_Btn, Reclamations_Btn, Events_Btn, Logout_Btn;
    public Pane general_pane;
    public Button Update_AccountUser, ConfirmerD0_AccountUser1, ChangeImageBtn;
    public TextField tf_UserAccountNumero, tf_UserAccountEmail;
    public ImageView ImageviewUser;
    public Label user_name;
    public MFXGenericDialog DialogConfirm_Delete, faceSetupDialog;
    public Label statusLabel;
    public ImageView webcamPreview;
    public Button captureButton, saveButton, closeFaceDialogButton;
    @FXML private Button takePhotoButton;

    private File selectedImageFile;
    private final AuthRepository au = new AuthRepository();
    private final UserRepository ur = new UserRepository();
    private double xOffset = 0;
    private double yOffset = 0;
    private Integer userId;
    private String userName;
    private static final int REQUIRED_PHOTOS = 20;
    private static final String FACES_DIR = "C:/Users/moeta/IdeaProjects/educonnect/faces/";

    private final FaceDetector faceDetector;
    private final FaceRecognitionRepository faceRecognitionRepository;
    private VideoCapture camera;
    private List<Mat> capturedFaces = new ArrayList<>();
    private boolean isCapturing = false;

    public UseraccountController() {
        URL cascadeUrl = getClass().getResource("/com/example/educonnect/educonnect/haarcascade_frontalface_default.xml");
        if (cascadeUrl == null) throw new RuntimeException("Haar cascade file not found");
        String cascadePath = cascadeUrl.getPath().replaceFirst("^/", "");
        this.faceDetector = new FaceDetector(cascadePath);
        this.faceRecognitionRepository = new FaceRecognitionRepository();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        getUserSession();
        if (userId == null) {
            showAlert("Erreur : Session non trouvée. Veuillez vous connecter.");
            return;
        }
        initializeCamera();
        setupFaceRecognitionStatus();
        initializeBindings();
        afficherdetails();
    }

    private void getUserSession() {
        UserSession session = ApplicationContext.getInstance().getUserSession();
        if (session != null) {
            userId = session.getUserId();
            userName = session.getUserName();
        }
    }

    private void afficherdetails() {
        User a = ur.getUserById(userId);
        if (a == null) {
            System.out.println("Utilisateur non trouvé !");
            return;
        }
        user_name.setText("Bienvenue, " + a.getNom());
        if (a.getPhotoUrl() != null) {
            try {
                Image image = new Image(a.getPhotoUrl());
                ImageviewUser.setImage(image);
            } catch (Exception ex) {
                System.out.println("Erreur image : " + ex.getMessage());
            }
        }
        tf_UserAccountEmail.setText(a.getEmail());
        tf_UserAccountNumero.setText(String.valueOf(a.getTelephone()));
    }

    public void updateUser(ActionEvent e) {
        User u = ur.getUserById(userId);
        if (!isValidEmail(tf_UserAccountEmail.getText())) {
            Modals.displayError("Email invalide", "Veuillez entrer un email valide.");
            return;
        }
        u.setEmail(tf_UserAccountEmail.getText());
        u.setTelephone(Integer.parseInt(tf_UserAccountNumero.getText()));
        if (selectedImageFile != null) u.setPhotoUrl(selectedImageFile.getAbsolutePath());
        ur.updateEmailPhoneAndImage(u);
        Modals.displaySuccess("Succès", "Profil mis à jour.");
        afficherdetails();
    }

    @FXML
    private void changeImage(ActionEvent e) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choisir une image");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File f = fc.showOpenDialog(ChangeImageBtn.getScene().getWindow());
        if (f != null) {
            selectedImageFile = f;
            ImageviewUser.setImage(new Image(f.toURI().toString()));
        }
    }

    public void deleteUser(ActionEvent e) throws IOException {
        ur.deleteEntity(userId);
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(Main.class.getResource("views/sign-in.fxml"))));
    }

    public void afficherDeleteDialog(ActionEvent e) {
        DialogConfirm_Delete.setOpacity(0);
        DialogConfirm_Delete.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), DialogConfirm_Delete);
        fadeIn.setToValue(1.0);
        fadeIn.play();
        general_pane.setEffect(new BoxBlur(5, 5, 3));
    }

    public void closeDeleteD(ActionEvent e) {
        DialogConfirm_Delete.setVisible(false);
        general_pane.setEffect(null);
    }

    public void onLogoutButtonClick(ActionEvent e) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Message");
            alert.setHeaderText(null);
            alert.setContentText("Êtes-vous sûr de vouloir vous déconnecter ?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get().equals(ButtonType.OK)) {
                Logout_Btn.getScene().getWindow().hide();
                Parent root = FXMLLoader.load(Main.class.getResource("views/sign-in.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.initStyle(StageStyle.TRANSPARENT);
                stage.show();
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    // --- FACE RECOGNITION ---
    private void initializeBindings() {
        DialogConfirm_Delete.setVisible(false);
        faceSetupDialog.setVisible(false);
        captureButton.setOnAction(event -> captureFace());
        saveButton.setOnAction(event -> saveFace());
        closeFaceDialogButton.setOnAction(event -> closeFaceDialog());
    }

    private void setupFaceRecognitionStatus() {
        File userDir = new File(FACES_DIR + userId.toString());
        if (userDir.exists() && userDir.listFiles() != null && userDir.listFiles().length > 0) {
            takePhotoButton.setDisable(true);
            takePhotoButton.setText("Activated");
        } else {
            takePhotoButton.setOnAction(event -> showFaceSetupDialog());
        }
    }

    private void initializeCamera() {
        camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            showAlert("Cannot access webcam.");
        }
    }

    private void releaseCamera() {
        if (camera != null && camera.isOpened()) {
            camera.release();
        }
    }

    @FXML
    private void showFaceSetupDialog() {
        capturedFaces.clear();
        faceSetupDialog.setVisible(true);
        startWebcamPreview();
    }

    @FXML
    private void captureFace() {
        if (!isCapturing) {
            isCapturing = true;
            statusLabel.setText("Capturing... (" + (capturedFaces.size() + 1) + "/" + REQUIRED_PHOTOS + ")");
            Mat frame = new Mat();
            if (camera.read(frame)) {
                Mat face = faceDetector.detectAndExtractFace(frame);
                if (face != null && !face.empty()) {
                    capturedFaces.add(face);
                    statusLabel.setText("Captured " + capturedFaces.size() + "/" + REQUIRED_PHOTOS);
                    if (capturedFaces.size() >= REQUIRED_PHOTOS) {
                        captureButton.setDisable(true);
                        saveButton.setDisable(false);
                        statusLabel.setText("Capture complete. Click Save to finish.");
                    }
                } else {
                    statusLabel.setText("No face detected. Try again.");
                }
            }
            isCapturing = false;
        }
    }

    @FXML
    private void saveFace() {
        String userDirPath = FACES_DIR + userId.toString();
        File userDir = new File(userDirPath);
        if (!userDir.exists()) userDir.mkdirs();
        try {
            for (int i = 0; i < capturedFaces.size(); i++) {
                String photoPath = userDirPath + "/photo_" + System.currentTimeMillis() + "_" + i + ".jpg";
                opencv_imgcodecs.imwrite(photoPath, capturedFaces.get(i));
            }
            new Thread(() -> {
                faceRecognitionRepository.trainModel();
                Platform.runLater(() -> {
                    showAlert("Face recognition setup completed successfully!");
                    takePhotoButton.setDisable(true);
                    takePhotoButton.setText("Face Recognition Set Up");
                    closeFaceDialog();
                });
            }).start();
        } catch (Exception ex) {
            showAlert("Error saving face data: " + ex.getMessage());
        }
    }

    private void stopWebcamPreview() {
        if (camera != null && camera.isOpened()) {
            camera.release();
        }
    }

    @FXML
    private void closeFaceDialog() {
        stopWebcamPreview();
        faceSetupDialog.setVisible(false);
        capturedFaces.clear();
        captureButton.setDisable(false);
        saveButton.setDisable(true);
        statusLabel.setText("Position your face in front of the camera");
    }

    private void startWebcamPreview() {
        if (!camera.isOpened()) initializeCamera();
        new Thread(() -> {
            while (faceSetupDialog.isVisible()) {
                Mat frame = new Mat();
                if (camera.read(frame)) {
                    Image fxImage = matToJavaFXImage(frame);
                    Platform.runLater(() -> webcamPreview.setImage(fxImage));
                }
                try { Thread.sleep(33); } catch (InterruptedException ignored) {}
            }
        }).start();
    }

    private Image matToJavaFXImage(Mat mat) {
        try {
            Mat convertedMat = new Mat();
            if (mat.channels() == 1) {
                opencv_imgproc.cvtColor(mat, convertedMat, opencv_imgproc.COLOR_GRAY2BGR);
            } else if (mat.channels() == 3) {
                opencv_imgproc.cvtColor(mat, convertedMat, opencv_imgproc.COLOR_BGR2RGB);
            } else {
                convertedMat = mat;
            }
            int width = convertedMat.cols();
            int height = convertedMat.rows();
            byte[] data = new byte[width * height * 3];
            convertedMat.data().get(data);

            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            byte[] targetPixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
            System.arraycopy(data, 0, targetPixels, 0, data.length);

            return SwingFXUtils.toFXImage(bufferedImage, null);
        } catch (Exception e) {
            System.err.println("Erreur conversion Mat -> Image : " + e.getMessage());
            return null;
        } finally {
            if (mat != null && !mat.isNull()) mat.release();
        }
    }

    private void showAlert(String msg) {
        Platform.runLater(() -> new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait());
    }
}
