package com.example.educonnect.educonnect.Controllers;

import com.example.educonnect.educonnect.Repository.AuthRepository;
import com.example.educonnect.educonnect.Repository.UserRepository;
import com.example.educonnect.educonnect.Utils.Navigate;
import com.example.educonnect.educonnect.Utils.SendMail;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class ForgotPasswordController implements Initializable {

    public MFXButton SendCodeBtn;
    public TextField tf_Email;
    public Pane general_pane;
    public MFXGenericDialog Dialog_Password;
    public TextField tf_passwordUpdate;
    public MFXGenericDialog Verif_Dialog;
    public TextField tf_codee;
    public MFXButton ValidateCodeBtn;
    public MFXButton savePassword;
    public MFXButton backButton;

    AuthRepository au = new AuthRepository();
    UserRepository u = new UserRepository();

    int x;
    int userId;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Dialog_Password.setVisible(false);
        Verif_Dialog.setVisible(false);
    }

    public void sendcode(javafx.event.ActionEvent event) {
        general_pane.setVisible(false);
        userId = u.getUserIdByEmail(tf_Email.getText());

        if (userId != -1) {
            Random random = new Random();
            int randomNumber = random.nextInt(999999);
            x = randomNumber;

            String message = "Your verification code is: " + randomNumber;
            String recipientEmail = tf_Email.getText();
            boolean emailSent = SendMail.send(recipientEmail, "Verification Code", message);

            if (emailSent) {
                Verif_Dialog.setVisible(true);
            } else {
                showError("Email Sending Failed", "Failed to send verification code. Please check your internet connection or email settings and try again.");
                Navigate.navigate(SendCodeBtn, "views/mainLoginSignUp.fxml", (Stage) SendCodeBtn.getScene().getWindow());
            }
        } else {
            showError("Email Not Found", "L'email n'existe pas !");
            Navigate.navigate(SendCodeBtn, "views/mainLoginSignUp.fxml", (Stage) SendCodeBtn.getScene().getWindow());
        }
    }

    public void validateCode() {
        if (Integer.parseInt(tf_codee.getText()) == x) {
            Verif_Dialog.setVisible(false);
            Dialog_Password.setOpacity(0);
            Dialog_Password.setVisible(true);

            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), Dialog_Password);
            fadeIn.setToValue(1.0);
            fadeIn.play();

            BoxBlur boxBlur = new BoxBlur(5, 5, 3);
            general_pane.setEffect(boxBlur);
        }
    }

    public void modifierpass() throws IOException {
        au.modifyPassword(userId, tf_passwordUpdate.getText());
        Stage window = (Stage) savePassword.getScene().getWindow();
        Navigate.navigate(savePassword, "views/mainLoginSignUp.fxml", window);
        general_pane.setEffect(null);
    }

    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/educonnect/educonnect/Views/mainLoginSignUp.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Navigation Error", "Impossible de retourner à la page de connexion.");
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
