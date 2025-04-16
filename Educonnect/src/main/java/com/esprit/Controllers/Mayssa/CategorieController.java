package com.esprit.Controllers.Mayssa;

import com.esprit.Models.Categorie;
import com.esprit.Services.CategorieService;
import com.esprit.exceptions.ValidationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class CategorieController {
    @FXML private TextField nomField;
    @FXML private TableView<Categorie> categorieTable;
    @FXML private TableColumn<Categorie, String> nomColumn;
    @FXML private Button saveButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;

    private final CategorieService categorieService = new CategorieService();
    private Categorie selectedCategorie;
    private ObservableList<Categorie> categories = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialize table columns
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nomCategorie"));

        // Load initial data
        refreshTable();

        // Setup table selection listener
        setupTableSelectionListener();

        // Set initial button states
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void setupTableSelectionListener() {
        categorieTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    selectedCategorie = newSelection;
                    if (newSelection != null) {
                        // Populate form with selected category
                        nomField.setText(newSelection.getNomCategorie());

                        // Update button states
                        saveButton.setDisable(true);
                        updateButton.setDisable(false);
                        deleteButton.setDisable(false);
                        clearButton.setDisable(false);
                    } else {
                        clearForm();
                    }
                }
        );
    }

    @FXML
    private void handleSave() {
        try {
            validateFields();
            Categorie categorie = new Categorie(nomField.getText().trim());
            categorieService.addCategorie(categorie);
            showAlert("Success", "Category created successfully", Alert.AlertType.INFORMATION);
            refreshTable();
            clearForm();
        } catch (ValidationException e) {
            showAlert("Validation Error", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Failed to create category: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedCategorie == null) {
            showAlert("Error", "No category selected to update", Alert.AlertType.ERROR);
            return;
        }

        try {
            validateFields();
            selectedCategorie.setNomCategorie(nomField.getText().trim());
            categorieService.updateCategorie(selectedCategorie);
            showAlert("Success", "Category updated successfully", Alert.AlertType.INFORMATION);
            refreshTable();
            clearForm();
        } catch (ValidationException e) {
            showAlert("Validation Error", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Failed to update category: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedCategorie == null) {
            showAlert("Error", "No category selected to delete", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Category");
        confirmation.setHeaderText("Confirm Deletion");
        confirmation.setContentText("Are you sure you want to delete this category?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    categorieService.deleteCategorie(selectedCategorie.getId());
                    showAlert("Success", "Category deleted successfully", Alert.AlertType.INFORMATION);
                    refreshTable();
                    clearForm();
                } catch (Exception e) {
                    showAlert("Error", "Failed to delete category: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    private void validateFields() throws ValidationException {
        String nom = nomField.getText().trim();

        if (nom.isEmpty()) {
            throw new ValidationException("Category name is required");
        }
        if (nom.length() < 3 || nom.length() > 25) {
            throw new ValidationException("Category name must be between 3-25 characters");
        }

        // Check if category name already exists (for new entries)
        if (selectedCategorie == null || !selectedCategorie.getNomCategorie().equals(nom)) {
            if (categorieService.categoryExists(nom)) {
                throw new ValidationException("Category name already exists");
            }
        }
    }

    public void refreshTable() {
        categories.setAll(categorieService.getAllCategories());
        categorieTable.setItems(categories);
        categorieTable.refresh();
    }

    private void clearForm() {
        nomField.clear();
        categorieTable.getSelectionModel().clearSelection();
        selectedCategorie = null;

        // Reset button states
        saveButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        clearButton.setDisable(false);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}