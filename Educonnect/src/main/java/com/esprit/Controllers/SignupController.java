package com.esprit.Controllers;

import com.esprit.Entities.User;
import com.esprit.Entities.UserRole;
import com.esprit.exceptions.DatabaseException;
import com.esprit.Repository.UserRepository;
import com.esprit.utils.Validator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Date;
import java.time.LocalDate;

public class SignupController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField cinField;
    @FXML private TextField telephoneField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private DatePicker dateNssPicker;
    @FXML private TextField lieuField;
    @FXML private TextField adresseField;
    @FXML private ComboBox<UserRole> roleComboBox;
    @FXML private TextField specialiteField;
    @FXML private TextField salaireField;
    @FXML private TextField photoField;
    @FXML private Label messageLabel;
    @FXML private TextField visiblePasswordField;
    @FXML private CheckBox showPasswordCheckBox;
    @FXML private ProgressBar passwordStrengthBar;
    @FXML private Label passwordStrengthLabel;

    private final UserRepository userRepository = new UserRepository();

    @FXML
    public void initialize() {
        // Appliquer le CSS après le rendu de la scène


        // Initialisation du comboBox avec les rôles
        roleComboBox.getItems().setAll(UserRole.values());
        roleComboBox.setOnAction(event -> toggleFormateurFields());

        // Initialiser l'état du formulaire (cacher salaire et spécialité au début)
        toggleFormateurFields();
    }

    private void toggleFormateurFields() {
        boolean isFormateur = roleComboBox.getValue() == UserRole.FORMATEUR;
        specialiteField.setDisable(!isFormateur);
        salaireField.setDisable(!isFormateur);
    }

    @FXML
    private void handlePhotoBrowse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            photoField.setText(selectedFile.getAbsolutePath());
        }
    }
    @FXML
    private void togglePasswordVisibility() {
        boolean show = showPasswordCheckBox.isSelected(); // Vérifie si la case est cochée
        if (show) {
            // Affiche le mot de passe en texte clair
            visiblePasswordField.setText(passwordField.getText()); // Copie le texte du mot de passe
            visiblePasswordField.setVisible(true);  // Affiche le champ visiblePasswordField
            visiblePasswordField.setManaged(true);  // Rend le champ visiblePasswordField gérable

            passwordField.setVisible(false);  // Cache le champ passwordField
            passwordField.setManaged(false);  // Rend le champ passwordField non gérable
        } else {
            // Cache le champ visiblePasswordField
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);

            passwordField.setVisible(true);  // Affiche le champ passwordField
            passwordField.setManaged(true);  // Rend le champ passwordField gérable
        }
    }

    private void updatePasswordStrength(String password) {
        double strength = calculatePasswordStrength(password);
        passwordStrengthBar.setProgress(strength);

        if (strength < 0.3) {
            passwordStrengthBar.setStyle("-fx-accent: red;");
            passwordStrengthLabel.setText("Faible");
        } else if (strength < 0.7) {
            passwordStrengthBar.setStyle("-fx-accent: orange;");
            passwordStrengthLabel.setText("Moyen");
        } else {
            passwordStrengthBar.setStyle("-fx-accent: green;");
            passwordStrengthLabel.setText("Fort");
        }
    }
    private double calculatePasswordStrength(String password) {
        int length = password.length();
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        boolean hasSymbol = password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");

        int score = 0;
        if (length >= 8) score++;
        if (hasUpper) score++;
        if (hasLower) score++;
        if (hasDigit) score++;
        if (hasSymbol) score++;

        return score / 5.0;
    }


    @FXML
    private void handleSignup() {
        if (!isInputValid()) return;

        User user = new User();
        user.setNom(nomField.getText());
        user.setPrenom(prenomField.getText());
        user.setCIN(Integer.parseInt(cinField.getText()));
        user.setTelephone(Integer.parseInt(telephoneField.getText()));
        user.setEmail(emailField.getText());
        user.setPassword(passwordField.getText());
        user.setDateNss(Date.valueOf(dateNssPicker.getValue()));
        user.setLieu(lieuField.getText());
        user.setAdresse(adresseField.getText());
        user.setRole(roleComboBox.getValue());
        user.setPhotoUrl(photoField.getText());

        if (user.getRole() == UserRole.FORMATEUR) {
            user.setSpecialite(specialiteField.getText());
            user.setSalaire(Float.parseFloat(salaireField.getText()));
        }

        try {
            userRepository.addEntity(user);
            messageLabel.setText("Inscription réussie !");
            clearForm();
        } catch (DatabaseException e) {
            messageLabel.setText("Erreur lors de l'inscription : " + e.getMessage());
        }
    }

    private boolean isInputValid() {
        StringBuilder errors = new StringBuilder();

        // Vérifier les longueurs
        if (!Validator.isValidTextLength(nomField.getText(), 4, 20)) errors.append("Nom doit contenir entre 4 et 20 caractères\n");
        if (!Validator.isValidTextLength(prenomField.getText(), 4, 20)) errors.append("Prénom doit contenir entre 4 et 20 caractères\n");
        if (!Validator.isValidTextLength(lieuField.getText(), 4, 20)) errors.append("Lieu doit contenir entre 4 et 20 caractères\n");
        if (!Validator.isValidTextLength(adresseField.getText(), 4, 20)) errors.append("Adresse doit contenir entre 4 et 20 caractères\n");

        // Vérifier CIN & Téléphone
        if (!Validator.isEightDigitNumber(cinField.getText())) errors.append("CIN doit contenir exactement 8 chiffres\n");
        if (!Validator.isEightDigitNumber(telephoneField.getText())) errors.append("Téléphone doit contenir exactement 8 chiffres\n");

        // Vérifier unicité CIN/email (suppose existence UserRepository)
        if (userRepository.cinExists(cinField.getText())) errors.append("Ce CIN est déjà utilisé\n");
        if (userRepository.emailExists(emailField.getText())) errors.append("Cet email est déjà utilisé\n");

        // Email & mot de passe
        if (!Validator.isValidEmail(emailField.getText())) errors.append("Email invalide\n");
        if (passwordField.getText().isEmpty()) errors.append("Mot de passe requis\n");
        if (!passwordField.getText().equals(confirmPasswordField.getText())) errors.append("Les mots de passe ne correspondent pas\n");

        // Date
        if (dateNssPicker.getValue() == null || dateNssPicker.getValue().isAfter(LocalDate.now()))
            errors.append("Date de naissance invalide\n");

        // Rôle
        if (roleComboBox.getValue() == null) errors.append("Rôle requis\n");

        // Si Formateur => Spécialité & salaire obligatoires
        if (roleComboBox.getValue() == UserRole.FORMATEUR) {
            if (specialiteField.getText().isEmpty()) errors.append("Spécialité requise\n");
            if (!Validator.isValidSalary(salaireField.getText())) errors.append("Salaire invalide\n");
        }

        if (errors.length() > 0) {
            messageLabel.setText(errors.toString());
            return false;
        }

        return true;
    }


    private void clearForm() {
        nomField.clear();
        prenomField.clear();
        cinField.clear();
        telephoneField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        dateNssPicker.setValue(null);
        lieuField.clear();
        adresseField.clear();
        roleComboBox.setValue(null);
        specialiteField.clear();
        salaireField.clear();
        photoField.clear();
    }
}
