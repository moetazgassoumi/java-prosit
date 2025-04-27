package com.example.educonnect.educonnect.Controllers;

import com.example.educonnect.educonnect.Entities.User;
import com.example.educonnect.educonnect.Entities.UserSession;
import com.example.educonnect.educonnect.Repository.AuthRepository;
import com.example.educonnect.educonnect.Repository.UserRepository;
import com.example.educonnect.educonnect.Repository.FaceRecognitionRepository;
import com.example.educonnect.educonnect.Utils.FaceDetector;
import com.example.educonnect.educonnect.Utils.Modals;
import com.example.educonnect.educonnect.Utils.Navigate;
import com.example.educonnect.educonnect.Utils.QuoteService;
import com.example.educonnect.educonnect.Utils.WeatherService;
import com.example.educonnect.educonnect.Entities.ApplicationContext;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

import static com.example.educonnect.educonnect.Utils.RoleNavigation.navigateUser;

public class LoginController implements Initializable {

    @FXML private Hyperlink forgot_password;
    @FXML private TextField ck_emailField;
    @FXML private PasswordField ck_passwordField;
    @FXML private Button signIn_btn;
    @FXML private Label weatherLabel;
    @FXML private Label quoteLabel;
    @FXML private Label quoteAuthorLabel;
    @FXML private Button recognizeButton;
    @FXML private Pane overlay;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Label statusLabel;

    private final FaceDetector faceDetector;
    private final FaceRecognitionRepository faceRecognitionRepository;
    private VideoCapture camera;

    private final AuthRepository ar = new AuthRepository();
    private final UserRepository ur = new UserRepository();

    // ✅ Constructeur pour initialiser les objets
    public LoginController() {
        try {
            // Charge le fichier Haarcascade depuis resources
            URL cascadeUrl = getClass().getResource("/com/example/educonnect/educonnect/haarcascade_frontalface_default.xml");
            if (cascadeUrl == null) {
                throw new RuntimeException("Erreur : Le fichier Haar Cascade n'a pas été trouvé !");
            }

            // Copie le fichier Haarcascade dans un fichier temporaire
            File tempCascadeFile = File.createTempFile("haarcascade_frontalface_default", ".xml");
            tempCascadeFile.deleteOnExit();

            try (InputStream in = cascadeUrl.openStream();
                 OutputStream out = new FileOutputStream(tempCascadeFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            // Passe le chemin réel du fichier à OpenCV
            this.faceDetector = new FaceDetector(tempCascadeFile.getAbsolutePath());
            this.faceRecognitionRepository = new FaceRecognitionRepository();

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du chargement du Haar Cascade", e);
        }
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("LoginController initialized.");

        // Affichage météo + citation
        Platform.runLater(() -> {
            String meteo = WeatherService.getWeather("Ariana");
            weatherLabel.setText(meteo);

            String[] quote = QuoteService.getRandomQuote();
            quoteLabel.setText("“" + quote[0] + "”");
            quoteAuthorLabel.setText("- " + quote[1]);
        });

        // Gestion du bouton de connexion normale
        signIn_btn.setOnAction(event -> {
            try {
                handleLogin();
            } catch (IOException e) {
                e.printStackTrace();
                Modals.displayError("Erreur", "Une erreur est survenue lors de la connexion.");
            }
        });

        // Gestion du bouton reconnaissance faciale
        recognizeButton.setOnAction(event -> loginWithFace());

        // Masquer l'overlay au départ
        overlay.setVisible(false);
        overlay.setManaged(false);
    }

    private void handleLogin() throws IOException {
        System.out.println("Login button clicked");

        if (ar.authenticateUser(ck_emailField.getText(), ck_passwordField.getText())) {
            User u1 = ur.getUserByEmail(ck_emailField.getText());
            UserSession us = UserSession.initializeUserSession(u1);
            System.out.println("The session user role is " + us.getRole());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Connexion réussie");
            alert.setHeaderText(null);
            alert.setContentText("Vous êtes connecté avec succès !");
            alert.showAndWait();

            Stage window = (Stage) signIn_btn.getScene().getWindow();
            navigateUser(window, us.getRole());
        } else {
            Modals.displayError("Échec de la connexion", "Email ou mot de passe invalide.");
        }
    }

    public void onForgotPasswordClick(MouseEvent mouseEvent) {
        System.out.println("Forgot password clicked");
        Stage window = (Stage) forgot_password.getScene().getWindow();
        Navigate.navigate(forgot_password, "views/ForgotPassword.fxml", window);
    }

    private void loginWithFace() {
        Platform.runLater(() -> {
            overlay.setVisible(true);
            overlay.setManaged(true);
            statusLabel.setText("Détection du visage en cours...");
        });

        Task<Void> recognitionTask = new Task<>() {
            @Override
            protected Void call() {
                try {
                    System.out.println("Starting face login...");
                    if (camera == null || !camera.isOpened()) {
                        initializeCamera();
                    }

                    Mat frame = new Mat();
                    boolean frameCaptured = camera.read(frame);
                    System.out.println("Frame captured: " + frameCaptured);

                    if (!frameCaptured || frame.empty()) {
                        showAlert("Impossible de capturer une image depuis la caméra.");
                        return null;
                    }

                    Platform.runLater(() -> statusLabel.setText("Analyse du visage..."));
                    Mat face = faceDetector.detectAndExtractFace(frame);

                    if (face == null || face.empty()) {
                        showAlert("Aucun visage détecté. Veuillez réessayer.");
                        return null;
                    }

                    Platform.runLater(() -> statusLabel.setText("Reconnaissance en cours..."));
                    Integer recognizedUserId = faceRecognitionRepository.recognizeFace(face);

                    if (recognizedUserId != null) {
                        User user = ur.getUserById(recognizedUserId);
                        if (user != null) {
                            UserSession session = UserSession.initializeUserSession(user);
                            ApplicationContext.getInstance().setUserSession(session);

                            Platform.runLater(() -> {
                                statusLabel.setText("Connexion réussie !");
                                Stage window = (Stage) recognizeButton.getScene().getWindow();
                                try {
                                    navigateUser(window, session.getRole());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        } else {
                            showAlert("Utilisateur reconnu introuvable dans la base de données.");
                        }
                    } else {
                        showAlert("Échec de la reconnaissance faciale.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Erreur : " + e.getMessage());
                } finally {
                    releaseCamera();
                }
                return null;
            }
        };

        recognitionTask.setOnSucceeded(event -> hideOverlayAfterDelay());
        recognitionTask.setOnFailed(event -> hideOverlayAfterDelay());

        new Thread(recognitionTask).start();
    }

    private void initializeCamera() {
        camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            System.err.println("Erreur : Impossible d'ouvrir la caméra !");
            showAlert("Impossible d'accéder à la caméra.");
        } else {
            System.out.println("Caméra initialisée.");
        }
    }

    private void releaseCamera() {
        if (camera != null && camera.isOpened()) {
            camera.release();
            System.out.println("Caméra libérée.");
        }
    }

    private void hideOverlayAfterDelay() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                Platform.runLater(() -> {
                    overlay.setVisible(false);
                    overlay.setManaged(false);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
            alert.showAndWait();
        });
    }
}
