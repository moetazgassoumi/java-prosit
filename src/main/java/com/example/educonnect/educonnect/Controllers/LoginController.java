package com.example.educonnect.educonnect.Controllers;


import com.example.educonnect.educonnect.Entities.User;
import com.example.educonnect.educonnect.Entities.UserSession;
import com.example.educonnect.educonnect.Repository.AuthRepository;
import com.example.educonnect.educonnect.Repository.UserRepository;

import com.example.educonnect.educonnect.Utils.Modals;
import com.example.educonnect.educonnect.Utils.Navigate;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.example.educonnect.educonnect.Utils.RoleNavigation.navigateUser;


public class LoginController implements Initializable {
    public Hyperlink forgot_password;
    @FXML
    private TextField ck_emailField;

    @FXML
    private TextField ck_passwordField;
    @FXML
    private Button signIn_btn;
    AuthRepository ar = new AuthRepository();
    UserRepository ur = new UserRepository();
    private void handleLogin() throws IOException {
        System.out.println("Login button clicked");

        if (ar.authenticateUser(ck_emailField.getText(), ck_passwordField.getText())) {
            User u1 = ur.getUserByEmail(ck_emailField.getText());
            UserSession us = UserSession.initializeUserSession(u1);
            System.out.println("The session user role is " + us.getRole());

            // ✅ Show success and redirect only after closing the alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Connexion réussie");
            alert.setHeaderText(null);
            alert.setContentText("Vous êtes connecté avec succès !");
            alert.showAndWait(); // ⛔️ Bloque jusqu’à ce que l’utilisateur ferme

            // ✅ Ensuite on redirige
            Stage window = (Stage) signIn_btn.getScene().getWindow();
            navigateUser(window, us.getRole());
        } else {
            Modals.displayError("Login Failed", "Invalid email or password");
        }
    }
    public void onForgotPasswordClick(MouseEvent mouseEvent) {
        System.out.println("Forgot password clicked");
        Stage window = (Stage) forgot_password.getScene().getWindow(); // ✅ changement ici
        Navigate.navigate(forgot_password, "views/ForgotPassword.fxml", window);
    }




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("LoginController initialized.");

        // Charger le fichier CSS après le rendu de la scène
        Platform.runLater(() -> {
            // Récupérer la scène à partir de la fenêtre
            Stage stage = (Stage) signIn_btn.getScene().getWindow();
            Scene scene = stage.getScene();
            // Ajouter le fichier CSS à la scène
            getClass().getResource("/com/example/educonnect/educonnect/Style/login.css");
        });

        // Ajouter le gestionnaire d'action pour le bouton de connexion
        signIn_btn.setOnAction(event -> {
            try {
                handleLogin();
            } catch (IOException e) {
                e.printStackTrace();
                Modals.displayError("Erreur", "Une erreur est survenue lors de la connexion.");
            }
        });
    }

}