package com.esprit.Controllers.Karim;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

public class MainController {
    @FXML
    private TabPane mainTabPane;

    @FXML
    private void initialize() {
        // Initialize with the first tab selected
        mainTabPane.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    @FXML
    private void showEventManagement() {
        mainTabPane.getSelectionModel().select(0); // Events tab is first (index 0)
    }

    @FXML
    private void showCategoryManagement() {
        mainTabPane.getSelectionModel().select(1); // Categories tab is second (index 1)
    }
}