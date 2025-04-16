package com.esprit.Controllers.Aziz;

import com.esprit.Models.Reponse;
import com.esprit.Services.ReponseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.time.LocalDateTime;

public class ReponseController {
    @FXML private ListView<Reponse> reponsesList;
    @FXML private TextArea reponseField;

    private final ReponseService reponseService = new ReponseService();
    private final ObservableList<Reponse> observableReponses = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configure list view
        reponsesList.setItems(observableReponses);

        // Set cell factory to display response content and date
        reponsesList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Reponse item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Display response content, date, and associated reclamation if available
                    String reclamationInfo = item.getReclamation() != null ?
                            " (Reclamation: " + item.getReclamation().getId() + ")" : "";
                    setText(item.getContenu() + " - " + item.getDateCreation() + reclamationInfo);
                }
            }
        });

        // Load all responses from database
        refreshReponses();

        // Selection listener to show selected response in text area
        reponsesList.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        reponseField.setText(newVal.getContenu());
                    }
                });
    }

    @FXML
    private void handleAddReponse() {
        String contenu = reponseField.getText().trim();
        if (!contenu.isEmpty()) {
            // Note: You'll need to associate with a reclamation in a real scenario
            Reponse newReponse = new Reponse(contenu, LocalDateTime.now(), null);
            reponseService.addReponse(newReponse);
            reponseField.clear();
            refreshReponses();
        } else {
            showAlert("Error", "Response content cannot be empty");
        }
    }

    @FXML
    private void handleUpdateReponse() {
        Reponse selected = reponsesList.getSelectionModel().getSelectedItem();
        String contenu = reponseField.getText().trim();

        if (selected != null && !contenu.isEmpty()) {
            selected.setContenu(contenu);
            reponseService.updateReponse(selected);
            reponseField.clear();
            refreshReponses();
        } else {
            showAlert("Error", "Please select a response and enter new content");
        }
    }

    @FXML
    private void handleDeleteReponse() {
        Reponse selected = reponsesList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            reponseService.deleteReponse(selected.getId());
            reponseField.clear();
            refreshReponses();
        }
    }

    public void refreshReponses() {
        // Get all responses from the database
        observableReponses.setAll(reponseService.getAllReponses());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}