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
import java.io.IOException;
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
    @FXML private ComboBox<String> lieuComboBox;
    @FXML private TextField adresseField;
    @FXML private ComboBox<UserRole> roleComboBox;
    @FXML private TextField specialiteField;
    @FXML private TextField salaireField;
    @FXML private TextField photoField;
    @FXML private TextField visiblePasswordField;
    @FXML private CheckBox showPasswordCheckBox;
    @FXML private ProgressBar passwordStrengthBar;
    @FXML private Label passwordStrengthLabel;

    // Error Labels
    @FXML private Label nomErrorLabel;
    @FXML private Label prenomErrorLabel;
    @FXML private Label emailErrorLabel;
    @FXML private Label passwordErrorLabel;
    @FXML private Label confirmPasswordErrorLabel;

    private final UserRepository userRepository = new UserRepository();

    @FXML
    public void initialize() {
        roleComboBox.getItems().setAll(UserRole.FORMATEUR, UserRole.MEMBRE);
        lieuComboBox.getItems().addAll(
                "Tunis", "Ariana", "Ben Arous", "Manouba", "Nabeul", "Zaghouan", "Bizerte",
                "Beja", "Jendouba", "Kef", "Siliana", "Sousse", "Monastir", "Mahdia", "Kairouan",
                "Kasserine", "Sidi Bouzid", "Sfax", "Gafsa", "Tozeur", "Kebili", "Gabès",
                "Médenine", "Tataouine"
        );
        roleComboBox.setOnAction(event -> toggleFormateurFields());
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
        user.setLieu(lieuComboBox.getValue());
        user.setAdresse(adresseField.getText());
        user.setRole(roleComboBox.getValue());
        user.setPhotoUrl(photoField.getText());

        if (user.getRole() == UserRole.FORMATEUR) {
            user.setSpecialite(specialiteField.getText());
            user.setSalaire(Float.parseFloat(salaireField.getText()));
        }

        try {
            userRepository.addEntity(user);
            // ✅ Après inscription réussie ➔ rediriger vers Login.fxml
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(com.example.educonnect.educonnect.Main.class.getResource("views/sign-in.fxml"));
            nomField.getScene().setRoot(root);

        } catch (DatabaseException | IOException e) {
            nomErrorLabel.setText("Erreur lors de l'inscription : " + e.getMessage());
        }
    }


    private boolean isInputValid() {
        boolean valid = true;
        nomErrorLabel.setText("");
        prenomErrorLabel.setText("");
        emailErrorLabel.setText("");
        passwordErrorLabel.setText("");
        confirmPasswordErrorLabel.setText("");

        if (nomField.getText().isEmpty()) {
            nomErrorLabel.setText("Nom requis");
            valid = false;
        }
        if (prenomField.getText().isEmpty()) {
            prenomErrorLabel.setText("Prénom requis");
            valid = false;
        }
        if (emailField.getText().isEmpty() || !isValidEmail(emailField.getText())) {
            emailErrorLabel.setText("Email invalide");
            valid = false;
        }
        if (passwordField.getText().isEmpty()) {
            passwordErrorLabel.setText("Mot de passe requis");
            valid = false;
        }
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            confirmPasswordErrorLabel.setText("Les mots de passe ne correspondent pas");
            valid = false;
        }
        return valid;
    }

    private void clearForm() {
        nomField.clear();
        prenomField.clear();
        cinField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        dateNssPicker.setValue(null);
        lieuComboBox.setValue(null);
        adresseField.clear();
        roleComboBox.setValue(null);
        specialiteField.clear();
        salaireField.clear();
        photoField.clear();
        telephoneField.clear();
    }
    @FXML
    private void onLoginLinkClick(javafx.scene.input.MouseEvent event) {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(com.example.educonnect.educonnect.Main.class.getResource("views/sign-in.fxml"));
            nomField.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}