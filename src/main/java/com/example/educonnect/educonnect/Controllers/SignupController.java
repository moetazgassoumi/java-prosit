package com.example.educonnect.educonnect.Controllers;

import com.example.educonnect.educonnect.Entities.User;
import com.example.educonnect.educonnect.Entities.UserRole;
import com.example.educonnect.educonnect.Exceptions.DatabaseException;
import com.example.educonnect.educonnect.Repository.UserRepository;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Date;
import java.time.LocalDate;

import static com.example.educonnect.educonnect.Utils.Validator.isValidEmail;

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
        roleComboBox.getItems().setAll(UserRole.values());
        roleComboBox.setOnAction(event -> toggleFormateurFields());
        toggleFormateurFields(); // cacher salaire/specialité au début
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
        boolean show = showPasswordCheckBox.isSelected();
        visiblePasswordField.setVisible(show);
        visiblePasswordField.setManaged(show);

        passwordField.setVisible(!show);
        passwordField.setManaged(!show);
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

        if (nomField.getText().isEmpty()) errors.append("Nom requis\n");
        if (prenomField.getText().isEmpty()) errors.append("Prénom requis\n");
        if (cinField.getText().isEmpty() || !cinField.getText().matches("\\d+")) errors.append("CIN invalide\n");
        if (telephoneField.getText().isEmpty() || !telephoneField.getText().matches("\\d+")) errors.append("Téléphone invalide\n");
        if (emailField.getText().isEmpty() || !isValidEmail(emailField.getText())) errors.append("Email invalide\n");


        if (passwordField.getText().isEmpty()) errors.append("Mot de passe requis\n");
        if (!passwordField.getText().equals(confirmPasswordField.getText())) errors.append("Les mots de passe ne correspondent pas\n");

        if (dateNssPicker.getValue() == null || dateNssPicker.getValue().isAfter(LocalDate.now())) errors.append("Date de naissance invalide\n");
        if (lieuField.getText().isEmpty()) errors.append("Lieu requis\n");
        if (adresseField.getText().isEmpty()) errors.append("Adresse requise\n");
        if (roleComboBox.getValue() == null) errors.append("Rôle requis\n");

        if (roleComboBox.getValue() == UserRole.FORMATEUR) {
            if (specialiteField.getText().isEmpty()) errors.append("Spécialité requise\n");
            if (salaireField.getText().isEmpty() || !salaireField.getText().matches("\\d+(\\.\\d+)?")) errors.append("Salaire invalide\n");
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
