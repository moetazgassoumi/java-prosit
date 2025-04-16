package com.esprit.Controllers;

import com.esprit.Controllers.Aziz.ReclamationController;
import com.esprit.Controllers.Aziz.ReponseController;
import com.esprit.Controllers.Karim.CategoryController;
import com.esprit.Controllers.Karim.EventController;
import com.esprit.Controllers.Mayssa.CategorieController;
import com.esprit.Controllers.Mayssa.CoursController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class AdminDashboardController {
    @FXML private StackPane contentArea;
    @FXML private VBox dashboardView;
    @FXML private Label totalUsersLabel;
    @FXML private Label pendingReclamationsLabel;
    @FXML private Label activeCoursesLabel;
    @FXML private Label upcomingEventsLabel;

    // Initialize dashboard with default view
    @FXML
    public void initialize() {
        showDashboard();
        // Load initial stats (you would replace with actual data)
        totalUsersLabel.setText("124");
        pendingReclamationsLabel.setText("18");
        activeCoursesLabel.setText("24");
        upcomingEventsLabel.setText("5");
    }

    // Navigation handlers
    @FXML
    private void showDashboard() {
        contentArea.getChildren().setAll(dashboardView);
    }


    @FXML
    private void showUserManagement() {
        try {
            // Charger la vue de gestion des utilisateurs
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/user-table.fxml"));
            Parent userTableView = loader.load();

            // Récupérer le contrôleur pour charger les données des utilisateurs
            UserTableController controller = loader.getController();

            // Si vous souhaitez afficher les utilisateurs d'un rôle spécifique, vous pouvez passer un rôle
            // Par exemple, afficher tous les utilisateurs avec le rôle "MEMBRE" (vous pouvez personnaliser cette logique)
            controller.loadData("MEMBRE");  // Remplacer par le rôle désiré ou dynamique

            // Mettre à jour le contenu de `contentArea` avec la vue des utilisateurs
            contentArea.getChildren().setAll(userTableView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load user management view", Alert.AlertType.ERROR);
        }
    }





    @FXML
    private void showResponse() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Reclamations/reponse_view.fxml"));
            Parent coursView = loader.load();

            // Get controller and refresh data if needed
            ReponseController controller = loader.getController();
            controller.refreshReponses();

            contentArea.getChildren().setAll(coursView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load reclamation view", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void showAllReclamations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Reclamations/reclamation_view.fxml"));
            Parent coursView = loader.load();

            // Get controller and refresh data if needed
            ReclamationController controller = loader.getController();
            controller.refreshReclamations();

            contentArea.getChildren().setAll(coursView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load reclamation view", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void showCourses() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Cours/cours_view.fxml"));
            Parent coursView = loader.load();

            // Get controller and refresh data if needed
            CoursController controller = loader.getController();
            controller.refreshTable();

            contentArea.getChildren().setAll(coursView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load courses view", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void showCategorie() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Cours/categorie_view.fxml"));
            Parent categorieView = loader.load();

            // Get controller and refresh data
            CategorieController controller = loader.getController();
            controller.refreshTable();

            contentArea.getChildren().setAll(categorieView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load categories view", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void showUpcomingEvents() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Events/event_view.fxml"));
            Parent eventView = loader.load();  // Changed from VBox to Parent

            EventController controller = loader.getController();
            controller.refreshEventTable();

            contentArea.getChildren().setAll(eventView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load events view", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void showCategory() {
        try {
            // Changed from CreateEventView to CategoryView
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Events/category_view.fxml"));
            Parent categoryView = loader.load();

            // Get controller and refresh data if needed
            CategoryController controller = loader.getController();
            controller.refreshTable();

            contentArea.getChildren().setAll(categoryView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load category view", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleLogout() {
        // Implement logout logic
        System.out.println("Logging out...");
    }

    // Helper method to load views
    private void loadView(String fxmlPath) {
        try {
            VBox view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load view: " + fxmlPath, Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}