package com.example.educonnect.educonnect.Controllers;


import com.example.educonnect.educonnect.Entities.User;
import com.example.educonnect.educonnect.Entities.UserSession;
import com.example.educonnect.educonnect.Repository.AuthRepository;
import com.example.educonnect.educonnect.Repository.UserRepository;

import com.example.educonnect.educonnect.Utils.Modals;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.example.educonnect.educonnect.Utils.RoleNavigation.navigateUser;


public class LoginController implements Initializable {
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



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("LoginController initialized.");

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
