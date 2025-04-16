package com.esprit.Controllers.Aziz;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainController {
    @FXML private BorderPane mainBorderPane;
    @FXML private TabPane mainTabPane;
    @FXML private Tab reclamationTab;
    @FXML private Tab reponseTab;

    @FXML
    public void initialize() {
        try {
            // Load Reclamation View
            FXMLLoader reclamationLoader = new FXMLLoader(
                    getClass().getResource("/com/esprit/reclamation_java/views/reclamation_view.fxml"));
            reclamationTab.setContent(reclamationLoader.load());

            // Load Reponse View
            FXMLLoader reponseLoader = new FXMLLoader(
                    getClass().getResource("/com/esprit/reclamation_java/views/reponse_view.fxml"));
            reponseTab.setContent(reponseLoader.load());

        } catch (IOException e) {
            System.err.println("Error loading tabs: " + e.getMessage());
            e.printStackTrace();
        }
    }
}