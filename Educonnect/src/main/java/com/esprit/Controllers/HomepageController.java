package com.esprit.Controllers;

import com.esprit.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class HomepageController {

    @FXML
    private Button eventsButton;

    @FXML
    private Button coursesButton;

    @FXML
    private Button reclamationsButton;

    @FXML
    private Button profileButton;  // Nouveau bouton pour afficher le profil

    @FXML
    public void initialize() {
        // Set button actions
        eventsButton.setOnAction(event -> handleEventsButton());
        coursesButton.setOnAction(event -> handleCoursesButton());
        reclamationsButton.setOnAction(event -> handleReclamationsButton());
        profileButton.setOnAction(event -> handleProfileButton()); // Action du bouton Profile
    }

    private void handleEventsButton() {
        try {
            // Load the Event FXML file
            Parent eventRoot = FXMLLoader.load(getClass().getResource("/Views/Events/front_view.fxml"));
            Stage stage = (Stage) eventsButton.getScene().getWindow();
            stage.setScene(new Scene(eventRoot));
            stage.setTitle("Events - EduConnect");
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading Event page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleCoursesButton() {
        try {
            // Load the Course FXML file
            Parent courseRoot = FXMLLoader.load(getClass().getResource("/Views/Cours/front_view.fxml"));
            Stage stage = (Stage) coursesButton.getScene().getWindow();
            stage.setScene(new Scene(courseRoot));
            stage.setTitle("Courses - EduConnect");
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading Course page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleReclamationsButton() {
        try {
            // Load the Reclamation FXML file
            Parent reclamationRoot = FXMLLoader.load(getClass().getResource("/Views/Reclamations/front_view.fxml"));
            Stage stage = (Stage) reclamationsButton.getScene().getWindow();
            stage.setScene(new Scene(reclamationRoot));
            stage.setTitle("Reclamations - EduConnect");
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading Reclamation page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Nouvelle méthode pour gérer l'ouverture de la page de profil
    @FXML
    private void handleProfileButton() {
        try {
            // Charger la vue du profil de l'utilisateur
            Parent profileRoot = FXMLLoader.load(getClass().getResource("/Views/useraccount.fxml"));
            Stage stage = (Stage) profileButton.getScene().getWindow();
            stage.setScene(new Scene(profileRoot));
            stage.setTitle("User Profile - EduConnect");
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading Profile page: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
