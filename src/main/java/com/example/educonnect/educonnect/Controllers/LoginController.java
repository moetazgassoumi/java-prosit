package com.example.educonnect.educonnect.Controllers;

import com.example.educonnect.educonnect.Entities.User;
import com.example.educonnect.educonnect.Entities.UserSession;
import com.example.educonnect.educonnect.Repository.AuthRepository;
import com.example.educonnect.educonnect.Repository.UserRepository;
import com.example.educonnect.educonnect.Utils.Modals;
import com.example.educonnect.educonnect.Utils.Navigate;
import com.example.educonnect.educonnect.Utils.QuoteService;
import com.example.educonnect.educonnect.Utils.WeatherService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.example.educonnect.educonnect.Utils.RoleNavigation.navigateUser;

public class LoginController implements Initializable {

    public Hyperlink forgot_password;
    @FXML private TextField ck_emailField;
    @FXML private TextField ck_passwordField;
    @FXML private Button signIn_btn;
    @FXML private Label weatherLabel; // ✅ Label météo
    @FXML private Label quoteLabel;
    @FXML private Label quoteAuthorLabel;

    AuthRepository ar = new AuthRepository();
    UserRepository ur = new UserRepository();

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("LoginController initialized.");

        // ✅ Affichage de la météo (ville fixe ou dynamique)
        Platform.runLater(() -> {
            String meteo = WeatherService.getWeather("Ariana");
            weatherLabel.setText(meteo);
            // Affichage citation inspirante
            String[] quote = QuoteService.getRandomQuote();
            quoteLabel.setText("“" + quote[0] + "”");
            quoteAuthorLabel.setText("- " + quote[1]);
        });

        // ✅ Gestionnaire du bouton login
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
