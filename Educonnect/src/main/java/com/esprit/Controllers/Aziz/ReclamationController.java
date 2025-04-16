package com.esprit.Controllers.Aziz;

import com.esprit.Models.Reclamation;
import com.esprit.Models.Reponse;
import com.esprit.Services.ReclamationService;
import com.esprit.Services.ReponseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.time.LocalDateTime;

public class ReclamationController {
    @FXML private ListView<Reclamation> reclamationsList;
    @FXML private TextArea messageField;
    @FXML private ListView<Reponse> reponsesList;
    @FXML private TextArea reponseField;

    private final ReclamationService reclamationService = new ReclamationService();
    private final ReponseService reponseService = new ReponseService();
    private final ObservableList<Reclamation> observableReclamations = FXCollections.observableArrayList();
    private final ObservableList<Reponse> observableReponses = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configure list views
        reclamationsList.setItems(observableReclamations);
        reponsesList.setItems(observableReponses);

        // Set cell factories
        reclamationsList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Reclamation item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getMessage() + " (" + item.getDateCreation() + ")");
                }
            }
        });

        reponsesList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Reponse item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getContenu() + " (" + item.getDateCreation() + ")");
                }
            }
        });

        // Load data
        refreshReclamations();

        // Selection listener
        reclamationsList.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        messageField.setText(newVal.getMessage());
                        showReponses(newVal);
                    }
                });
    }

    @FXML
    private void handleUpdateReclamation() {
        Reclamation selected = reclamationsList.getSelectionModel().getSelectedItem();
        String message = messageField.getText().trim();

        if (selected != null && !message.isEmpty()) {
            selected.setMessage(message);
            reclamationService.updateReclamation(selected);
            refreshReclamations();
        } else {
            showAlert("Error", "Please select a reclamation and enter a new message");
        }
    }

    @FXML
    private void handleDeleteReclamation() {
        Reclamation selected = reclamationsList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            reclamationService.deleteReclamation(selected.getId());
            messageField.clear();
            refreshReclamations();
            observableReponses.clear();
        }
    }

    @FXML
    private void handleAddReponse() {
        Reclamation selected = reclamationsList.getSelectionModel().getSelectedItem();
        String contenu = reponseField.getText().trim();

        if (selected != null && !contenu.isEmpty()) {
            Reponse newReponse = new Reponse(contenu, LocalDateTime.now(), selected);
            reponseService.addReponse(newReponse);
            reponseField.clear();
            showReponses(selected);
        } else {
            showAlert("Error", "Please select a reclamation and enter a response");
        }
    }

    public void refreshReclamations() {
        observableReclamations.setAll(reclamationService.getAllReclamations());
    }

    private void showReponses(Reclamation reclamation) {
        if (reclamation != null) {
            observableReponses.setAll(reponseService.getReponsesByReclamation(reclamation.getId()));
        } else {
            observableReponses.clear();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}