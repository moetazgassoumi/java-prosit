package com.esprit.Controllers.Mayssa;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainController {
    @FXML private BorderPane mainBorderPane;
    @FXML private TabPane mainTabPane;
    @FXML private Tab frontTab;
    @FXML private Tab coursTab;
    @FXML private Tab categoriesTab;

    @FXML
    public void initialize() {
        try {
//            // Load Front View (Accueil)
//            FXMLLoader frontLoader = new FXMLLoader(
//                    getClass().getResource("/com/esprit/cours_java/views/main_view.fxml")
//            );
//            frontTab.setContent(frontLoader.load());

            // Load Cours View
            FXMLLoader coursLoader = new FXMLLoader(
                    getClass().getResource("/com/esprit/cours_java/views/cours_view.fxml")
            );
            coursTab.setContent(coursLoader.load());

            // Load Categories View
            FXMLLoader categoriesLoader = new FXMLLoader(
                    getClass().getResource("/com/esprit/cours_java/views/categorie_view.fxml")
            );
            categoriesTab.setContent(categoriesLoader.load());

        } catch (IOException e) {
            System.err.println("Error loading tabs: " + e.getMessage());
            e.printStackTrace();
        }
    }
}