package com.esprit.Controllers.Karim;

import com.esprit.Models.Category;
import com.esprit.Services.CategoryService;
import com.esprit.exceptions.UniqueConstraintException;
import com.esprit.exceptions.ValidationException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class CategoryController {
    @FXML private TextField nameField;
    @FXML private TableView<Category> categoryTable;
    @FXML private TableColumn<Category, String> nameColumn;
    @FXML private Button saveButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;

    private final CategoryService categoryService = new CategoryService();
    private Category selectedCategory;

    @FXML
    public void initialize() {
        // Initialize table column
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Load initial data
        refreshTable();

        // Add selection listener
        setupTableSelectionListener();

        // Disable update button initially
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void setupTableSelectionListener() {
        categoryTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    selectedCategory = newSelection;
                    if (newSelection != null) {
                        populateForm(newSelection);
                        saveButton.setDisable(true);
                        updateButton.setDisable(false);
                        deleteButton.setDisable(false);
                    } else {
                        clearForm();
                        saveButton.setDisable(false);
                        updateButton.setDisable(true);
                        deleteButton.setDisable(true);
                    }
                }
        );
    }

    @FXML
    private void handleSaveCategory() {
        try {
            validateFields();
            Category category = new Category(nameField.getText().trim());
            categoryService.addCategory(category);
            showAlert("Success", "Category created successfully", Alert.AlertType.INFORMATION);
            clearForm();
            refreshTable();
        } catch (UniqueConstraintException e) {
            showAlert("Duplicate Category", e.getMessage(), Alert.AlertType.ERROR);
        } catch (ValidationException e) {
            showAlert("Validation Error", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Failed to save category: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleUpdateCategory() {
        if (selectedCategory == null) {
            showAlert("Error", "No category selected to update", Alert.AlertType.ERROR);
            return;
        }

        try {
            validateFields();
            selectedCategory.setName(nameField.getText().trim());
            categoryService.updateCategory(selectedCategory);
            showAlert("Success", "Category updated successfully", Alert.AlertType.INFORMATION);
            clearForm();
            refreshTable();
        } catch (UniqueConstraintException e) {
            showAlert("Duplicate Category", e.getMessage(), Alert.AlertType.ERROR);
        } catch (ValidationException e) {
            showAlert("Validation Error", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Failed to update category: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDeleteCategory() {
        if (selectedCategory == null) {
            showAlert("Error", "Please select a category to delete", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Category");
        confirmation.setHeaderText("Confirm Deletion");
        confirmation.setContentText("Are you sure you want to delete this category?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    categoryService.deleteCategory(selectedCategory.getId());
                    showAlert("Success", "Category deleted successfully", Alert.AlertType.INFORMATION);
                    refreshTable();
                    clearForm();
                } catch (ValidationException e) {
                    showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    @FXML
    private void handleClearForm() {
        clearForm();
    }

    private void validateFields() throws ValidationException {
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            throw new ValidationException("Category name is required");
        }
    }

    public void refreshTable() {
        categoryTable.setItems(categoryService.getAllCategories());
    }

    private void populateForm(Category category) {
        nameField.setText(category != null ? category.getName() : "");
    }

    private void clearForm() {
        nameField.clear();
        categoryTable.getSelectionModel().clearSelection();
        selectedCategory = null;
        saveButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}