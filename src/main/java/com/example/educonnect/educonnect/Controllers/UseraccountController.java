
package com.example.educonnect.educonnect.Controllers;

import com.example.educonnect.educonnect.Entities.*;
import com.example.educonnect.educonnect.Main;
import com.example.educonnect.educonnect.Repository.*;
import com.example.educonnect.educonnect.Utils.FaceDetector;
import com.example.educonnect.educonnect.Repository.FaceRecognitionRepository;
import com.example.educonnect.educonnect.Utils.Modals;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import javafx.animation.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;


import static com.example.educonnect.educonnect.Utils.Validator.isValidEmail;

public class UseraccountController implements Initializable {
    public Button Home_Btn, Users_Btn, Reclamations_Btn, Events_Btn, Logout_Btn;
    public Pane general_pane;
    public MFXButton Confirmer_AccountUser, Update_AccountUser, ConfirmerD0_AccountUser1;
    public MFXTextField tf_UserAccountNumero, tf_UserAccountEmail;
    public ImageView ImageviewUser;
    public MFXGenericDialog DialogConfirm_Delete, Dialog_UpdatePassword_User;
    public MFXButton AnnulerDelete1_User, ConfirmDelete1_User;
    public Label user_name;
    public MFXButton UpdatePassword_User, AnnulerUpdateP_User, ChangeImageBtn;
    private File selectedImageFile;
    public MFXTextField tf_UserOldPassword, tf_UserNewPassword;
    public MFXGenericDialog faceSetupDialog;
    public ImageView webcamPreview;
    public Label statusLabel;
    public MFXButton captureButton;
    public MFXButton saveButton;
    public MFXButton closeFaceDialogButton;
    @FXML
    private Button takePhotoButton;

    private final AuthRepository au = new AuthRepository();
    private final UserRepository ur = new UserRepository();
    private double xOffset = 0;
    private double yOffset = 0;
    final int x = 1315;
    final int y = 890;
    private Integer userId;
    String userName, userPrenom;
    UserRole userRole;

    // face detector
    private final FaceDetector faceDetector;
    private final FaceRecognitionRepository faceRecognitionRepository;
    private static final int REQUIRED_PHOTOS = 20;
    private static final String FACES_DIR = "C:/Users/moeta/IdeaProjects/educonnect/faces/";
    private VideoCapture camera;
    private List<Mat> capturedFaces = new ArrayList<>();
    private boolean isCapturing = false;

    public UseraccountController() {
        URL cascadeUrl = getClass().getResource("/com/example/educonnect/educonnect/haarcascade_frontalface_default.xml");
        if (cascadeUrl == null) {
            throw new RuntimeException("Haar cascade file not found at /com/example/educonnect/educonnect/haarcascade_frontalface_default.xml");
        }
        String cascadePath = cascadeUrl.getPath().replaceFirst("^/", "");
        System.out.println("Loading cascade from: " + cascadePath);
        this.faceDetector = new FaceDetector(cascadePath);
        this.faceRecognitionRepository = new FaceRecognitionRepository();
    }



    private void setupButtonAnimation(MFXButton button) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
        st.setFromX(1.0); st.setFromY(1.0);
        st.setToX(1.05); st.setToY(1.05);
        button.setUserData(st);
    }

    @FXML private void onButtonHoverEnter(MouseEvent e) {
        MFXButton b = (MFXButton) e.getSource();
        ((ScaleTransition) b.getUserData()).setRate(1.0); ((ScaleTransition) b.getUserData()).playFromStart();
    }

    @FXML private void onButtonHoverExit(MouseEvent e) {
        MFXButton b = (MFXButton) e.getSource();
        ((ScaleTransition) b.getUserData()).setRate(-1.0); ((ScaleTransition) b.getUserData()).play();
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

    @FXML private void changeImage(ActionEvent e) {
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
        stage.setScene(new Scene(FXMLLoader.load(Main.class.getResource("views/mainLoginSignUp.fxml"))));
    }

    void afficherdetails() {
        User a = ur.getUserById(userId);  // 🔁 Meilleur choix que getUserByName()
        if (a == null) {
            System.out.println("Utilisateur non trouvé !");
            return;
        }

        // ✅ Mise à jour du nom dans l'interface
        user_name.setText("Bienvenue, " + a.getNom());

        // ✅ Chargement de l'image
        if (a.getPhotoUrl() != null) {
            try {
                Image image = new Image(a.getPhotoUrl());
                ImageviewUser.setImage(image);
            } catch (Exception ex) {
                System.out.println("Erreur de chargement d'image: " + ex.getMessage());
            }
        }

        tf_UserAccountEmail.setText(String.valueOf(a.getEmail()));
        tf_UserAccountNumero.setText(String.valueOf(a.getTelephone()));
    }


    private void getUserSession() {
        UserSession session = ApplicationContext.getInstance().getUserSession();
        if (session != null) {
            userId = session.getUserId();
            userName = session.getUserName();
            userPrenom = session.getPrenom();
            userRole = session.getRole();
        }
    }

    private void showAlert(String msg) {
        Platform.runLater(() -> new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait());
    }
    public void onLogoutButtonClick(ActionEvent actionEvent) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Message");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to logout?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get().equals(ButtonType.OK)) {
                Logout_Btn.getScene().getWindow().hide();
                Parent root = FXMLLoader.load(com.example.educonnect.educonnect.Main.class.getResource("views/mainLoginSignUp.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                root.setOnMousePressed((MouseEvent event) -> {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                });
                root.setOnMouseDragged((MouseEvent event) -> {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                    stage.setOpacity(.6);
                });

                root.setOnMouseReleased((MouseEvent event) -> {
                    stage.setOpacity(1);
                });
                stage.initStyle(StageStyle.TRANSPARENT);
                stage.setScene(scene);
                stage.show();
            }else return;

        }catch (Exception e) {e.printStackTrace();}
    }
    public void ondialogueupdatebtnClick(ActionEvent actionEvent) {
        Dialog_UpdatePassword_User.setVisible(false);
        general_pane.setEffect(null);
    }

    public void UpdatePassword(ActionEvent actionEvent) {
        if(actionEvent.getSource()==UpdatePassword_User)
        {
            if(au.validerPassword(userId,tf_UserOldPassword.getText()))
            {
                au.modifyPassword(userId,tf_UserNewPassword.getText());
            }
        }
    }
    public void afficherUpdate(ActionEvent actionEvent) {
        Platform.runLater(() -> {

            BoxBlur boxBlur = new BoxBlur(5, 5, 3);
            general_pane.setEffect(boxBlur);


            Dialog_UpdatePassword_User.setOpacity(0);
            Dialog_UpdatePassword_User.setVisible(true);

            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), Dialog_UpdatePassword_User);
            fadeIn.setToValue(1.0);
            fadeIn.setOnFinished(event -> general_pane.setEffect(boxBlur)); // Apply blur after fade-in
            fadeIn.play();
        });
    }
    public void afficherDeleteDialog(ActionEvent actionEvent) {
        DialogConfirm_Delete.setOpacity(0);
        DialogConfirm_Delete.setVisible(true);
        // Créer une transition de fondu pour simuler l'effet de coup d'éponge
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), DialogConfirm_Delete);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        BoxBlur boxBlur = new BoxBlur();
        boxBlur.setWidth(5);
        boxBlur.setHeight(5);
        boxBlur.setIterations(3);
        general_pane.setEffect(boxBlur);
    }
    public void closeDeleteD(ActionEvent actionEvent) {
        DialogConfirm_Delete.setVisible(false);
        general_pane.setEffect(null);
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
        if (!userDir.exists()) {
            userDir.mkdirs();
        }

        try {
            for (int i = 0; i < capturedFaces.size(); i++) {
                String photoPath = userDirPath + "/photo_" + System.currentTimeMillis() + "_" + i + ".jpg";
                opencv_imgcodecs.imwrite(photoPath, capturedFaces.get(i));
            }

            Thread trainThread = new Thread(() -> {
                faceRecognitionRepository.trainModel();
                Platform.runLater(() -> {
                    showAlert("Face recognition setup completed successfully!");
                    takePhotoButton.setDisable(true);
                    takePhotoButton.setText("Face Recognition Set Up");
                    closeFaceDialog();
                });
            });
            trainThread.start();
        } catch (Exception e) {
            showAlert("Error saving face data: " + e.getMessage());
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
        if (!camera.isOpened()) {
            initializeCamera();
        }

        Thread previewThread = new Thread(() -> {
            while (faceSetupDialog.isVisible()) {
                Mat frame = new Mat();
                if (camera.read(frame)) {
                    // Convert Mat to JavaFX Image (you'll need a utility method for this)
                    Image fxImage = matToJavaFXImage(frame); // Implement this conversion
                    Platform.runLater(() -> webcamPreview.setImage(fxImage));
                }
                try {
                    Thread.sleep(33); // ~30 FPS
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        previewThread.setDaemon(true);
        previewThread.start();
    }


    private Image matToJavaFXImage(Mat mat) {
        try {
            // Convert Mat to BufferedImage
            Mat convertedMat = new Mat();

            // If the Mat is not in BGR format, convert it
            if (mat.channels() == 1) {
                opencv_imgproc.cvtColor(mat, convertedMat, opencv_imgproc.COLOR_GRAY2BGR);
            } else if (mat.channels() == 3) {
                opencv_imgproc.cvtColor(mat, convertedMat, opencv_imgproc.COLOR_BGR2RGB);
            } else {
                convertedMat = mat; // Assume it's already in correct format
            }

            // Get the image data
            int width = convertedMat.cols();
            int height = convertedMat.rows();
            byte[] data = new byte[width * height * 3]; // 3 bytes per pixel (RGB)
            convertedMat.data().get(data);

            // Create BufferedImage
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            byte[] targetPixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
            System.arraycopy(data, 0, targetPixels, 0, data.length);

            // Convert BufferedImage to JavaFX Image
            return SwingFXUtils.toFXImage(bufferedImage, null);

        } catch (Exception e) {
            System.err.println("Error converting Mat to JavaFX Image: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            // Clean up
            if (mat != null && !mat.isNull()) {
                mat.release();
            }
        }
    }

    private void stopWebcamPreview() {
        releaseCamera();
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
    private void initializeBindings() {
        Dialog_UpdatePassword_User.setVisible(false);
        DialogConfirm_Delete.setVisible(false);
        faceSetupDialog.setVisible(false);

        captureButton.setOnAction(event -> captureFace());
        saveButton.setOnAction(event -> saveFace());
        closeFaceDialogButton.setOnAction(event -> closeFaceDialog());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        getUserSession();
        if (userId == null) {
            showAlert("Erreur : Session non trouvée. Veuillez vous connecter.");
            return;
        }

        setupButtonAnimation(Update_AccountUser);
        setupButtonAnimation(ConfirmerD0_AccountUser1);

        initializeCamera();
        setupFaceRecognitionStatus();
        initializeBindings();
        afficherdetails();

        // 🔗 Actions
        Update_AccountUser.setOnAction(this::updateUser);
        ConfirmerD0_AccountUser1.setOnAction(e -> {
            try { deleteUser(e); } catch (IOException ex) { ex.printStackTrace(); }
        });
    }

}