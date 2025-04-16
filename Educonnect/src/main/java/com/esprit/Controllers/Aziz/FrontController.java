package com.esprit.Controllers.Aziz;

import com.esprit.Models.Reclamation;
import com.esprit.Models.Reponse;
import com.esprit.Services.ReclamationService;
import com.esprit.Services.ReponseService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FrontController {
    @FXML private VBox reclamationsContainer;
    @FXML private ScrollPane scrollPane;
    @FXML private Button addReclamationBtn;

    private final ReclamationService reclamationService = new ReclamationService();
    private final ReponseService reponseService = new ReponseService();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    @FXML
    public void initialize() {
        loadReclamations();
        styleScrollPane();
        addReclamationBtn.setOnAction(e -> showAddReclamationDialog());
    }

    private void loadReclamations() {
        reclamationsContainer.getChildren().clear();
        List<Reclamation> reclamations = reclamationService.getAllReclamations();

        for (Reclamation reclamation : reclamations) {
            reclamationsContainer.getChildren().add(createReclamationCard(reclamation));
        }
    }

    private VBox createReclamationCard(Reclamation reclamation) {
        VBox card = new VBox();
        card.getStyleClass().add("reclamation-card");
        card.setPadding(new Insets(15));
        card.setSpacing(10);

        HBox header = new HBox();
        header.setSpacing(10);

        Label titleLabel = new Label(reclamation.getMessage());
        titleLabel.setFont(Font.font(14));
        titleLabel.setStyle("-fx-font-weight: bold;");
        titleLabel.setWrapText(true);

        Label dateLabel = new Label(reclamation.getDateCreation().format(formatter));
        dateLabel.setTextFill(Color.GRAY);
        dateLabel.setFont(Font.font(12));

        header.getChildren().addAll(titleLabel, dateLabel);

        Button detailsBtn = new Button("View Responses");
        detailsBtn.getStyleClass().add("details-btn");
        detailsBtn.setOnAction(e -> showResponsesDialog(reclamation));

        card.getChildren().addAll(header, detailsBtn);
        return card;
    }

    private void showResponsesDialog(Reclamation reclamation) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Responses for Reclamation #" + reclamation.getId());

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setPrefSize(600, 400);
        dialogPane.getButtonTypes().add(ButtonType.CLOSE);

        VBox content = new VBox();
        content.setSpacing(15);
        content.setPadding(new Insets(15));

        // Reclamation info
        HBox reclamationInfo = new HBox();
        reclamationInfo.setSpacing(10);
        Label reclamationLabel = new Label("Reclamation: " + reclamation.getMessage());
        reclamationLabel.setStyle("-fx-font-weight: bold;");
        reclamationInfo.getChildren().add(reclamationLabel);

        // Responses list
        VBox responsesContainer = new VBox();
        responsesContainer.setSpacing(10);
        responsesContainer.getStyleClass().add("responses-container");

        List<Reponse> responses = reponseService.getReponsesByReclamation(reclamation.getId());
        if (responses.isEmpty()) {
            responsesContainer.getChildren().add(new Label("No responses yet"));
        } else {
            for (Reponse response : responses) {
                responsesContainer.getChildren().add(createResponseBox(response));
            }
        }

        // Add response section
        HBox addResponseBox = new HBox();
        addResponseBox.setSpacing(10);

        TextArea responseField = new TextArea();
        responseField.setPromptText("Enter your response...");
        responseField.setPrefHeight(60);

        Button addResponseBtn = new Button("Add Response");
        addResponseBtn.getStyleClass().add("add-btn");
        addResponseBtn.setOnAction(e -> {
            String responseText = responseField.getText().trim();
            if (!responseText.isEmpty()) {
                Reponse newResponse = new Reponse(responseText, LocalDateTime.now(), reclamation);
                reponseService.addReponse(newResponse);
                responsesContainer.getChildren().add(createResponseBox(newResponse));
                responseField.clear();
            }
        });

        addResponseBox.getChildren().addAll(responseField, addResponseBtn);
        content.getChildren().addAll(reclamationInfo, responsesContainer, addResponseBox);
        dialogPane.setContent(content);

        dialog.showAndWait();
    }

    private Node createResponseBox(Reponse response) {
        VBox responseBox = new VBox();
        responseBox.getStyleClass().add("response-box");
        responseBox.setPadding(new Insets(10));
        responseBox.setSpacing(5);

        Label contentLabel = new Label(response.getContenu());
        contentLabel.setWrapText(true);

        Label dateLabel = new Label(response.getDateCreation().format(formatter));
        dateLabel.setTextFill(Color.GRAY);
        dateLabel.setStyle("-fx-font-size: 11;");

        responseBox.getChildren().addAll(contentLabel, dateLabel);
        return responseBox;
    }

    private void showAddReclamationDialog() {
        Dialog<Reclamation> dialog = new Dialog<>();
        dialog.setTitle("Create New Reclamation");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        VBox content = new VBox();
        content.setSpacing(10);
        content.setPadding(new Insets(15));

        TextArea messageField = new TextArea();
        messageField.setPromptText("Enter your reclamation...");
        messageField.setPrefHeight(100);

        content.getChildren().add(messageField);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK && !messageField.getText().trim().isEmpty()) {
                return new Reclamation(messageField.getText().trim());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(reclamation -> {
            reclamationService.addReclamation(reclamation);
            reclamationsContainer.getChildren().add(createReclamationCard(reclamation));
        });
    }

    private void styleScrollPane() {
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f5f5f5; -fx-border-color: transparent;");
        reclamationsContainer.setSpacing(15);
        reclamationsContainer.setPadding(new Insets(15));
    }
}