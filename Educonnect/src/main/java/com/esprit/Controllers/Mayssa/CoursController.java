package com.esprit.Controllers.Mayssa;

import com.esprit.Models.*;
import com.esprit.Services.*;
import com.esprit.exceptions.ValidationException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

public class CoursController {
    // Form controls
    @FXML private TextField titreField;
    @FXML private TextArea descriptionArea;
    @FXML private TextArea contenuArea;
    @FXML private ComboBox<Categorie> categorieCombo;
    @FXML private TextField imagePathField;
    @FXML private Button uploadButton;
    @FXML private ImageView imagePreview;
    @FXML private Button saveButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;

    // Table components
    @FXML private TableView<Cours> coursTable;
    @FXML private TableColumn<Cours, String> titreColumn;
    @FXML private TableColumn<Cours, String> descriptionColumn;
    @FXML private TableColumn<Cours, String> contenuColumn;
    @FXML private TableColumn<Cours, String> categorieColumn;
    @FXML private TableColumn<Cours, String> imageColumn;

    private final CoursService coursService = new CoursService();
    private final CategorieService categorieService = new CategorieService();
    private FileChooser fileChooser;
    private File selectedImageFile;
    private Cours selectedCours;
    private ObservableList<Cours> coursList = FXCollections.observableArrayList();
    private ObservableList<Categorie> categories = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        initializeForm();
        initializeTableColumns();
        refreshTable();
        setupTableSelectionListener();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void initializeForm() {
        categories.setAll(categorieService.getAllCategories());
        categorieCombo.setItems(categories);

        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        imagePreview.setImage(null);
    }

    private void initializeTableColumns() {
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        contenuColumn.setCellValueFactory(new PropertyValueFactory<>("contenu"));
        categorieColumn.setCellValueFactory(cellData -> {
            Categorie categorie = cellData.getValue().getCategorie();
            return new SimpleStringProperty(categorie != null ? categorie.getNomCategorie() : "");
        });
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("imagePath"));

        // Set up text wrapping for description and content columns
        descriptionColumn.setCellFactory(tc -> new TextWrappingTableCell());
        contenuColumn.setCellFactory(tc -> new TextWrappingTableCell());

        // Set up image display in table
        imageColumn.setCellFactory(tc -> new TableCell<Cours, String>() {
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);
                if (empty || imagePath == null || imagePath.isEmpty()) {
                    setGraphic(null);
                } else {
                    try {
                        File imageFile = new File(CoursService.UPLOAD_DIR + imagePath);
                        if (imageFile.exists()) {
                            imageView.setImage(new Image(imageFile.toURI().toString()));
                            setGraphic(imageView);
                        } else {
                            setGraphic(null);
                        }
                    } catch (Exception e) {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    // Custom TableCell for text wrapping
    private static class TextWrappingTableCell extends TableCell<Cours, String> {
        private final Text text;
        private final HBox container;

        public TextWrappingTableCell() {
            text = new Text();
            container = new HBox(text);
            container.setStyle("-fx-padding: 5px;");
            text.wrappingWidthProperty().bind(widthProperty().subtract(15));
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setPrefHeight(Control.USE_COMPUTED_SIZE);
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                setTooltip(null);
            } else {
                text.setText(item);
                setGraphic(container);
                setTooltip(new Tooltip(item));
            }
        }
    }

    private void setupTableSelectionListener() {
        coursTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    selectedCours = newSelection;
                    if (newSelection != null) {
                        setFormData(newSelection);
                        saveButton.setDisable(true);
                        updateButton.setDisable(false);
                        deleteButton.setDisable(false);
                    } else {
                        clearForm();
                    }
                }
        );
    }

    @FXML
    private void handleSave() {
        try {
            Cours cours = createCoursFromForm();
            coursService.addCours(cours);
            showAlert("Success", "Course created successfully", Alert.AlertType.INFORMATION);
            refreshTable();
            clearForm();
        } catch (ValidationException e) {
            showAlert("Validation Error", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Failed to create course: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedCours == null) {
            showAlert("Error", "No course selected to update", Alert.AlertType.ERROR);
            return;
        }

        try {
            Cours updatedCours = createCoursFromForm();
            updatedCours.setId(selectedCours.getId());
            coursService.updateCours(updatedCours);
            showAlert("Success", "Course updated successfully", Alert.AlertType.INFORMATION);
            refreshTable();
            clearForm();
        } catch (ValidationException e) {
            showAlert("Validation Error", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Failed to update course: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedCours == null) {
            showAlert("Error", "No course selected to delete", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Course");
        confirmation.setContentText("Are you sure you want to delete this course?");
        confirmation.setHeaderText(null);

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    coursService.deleteCours(selectedCours.getId());
                    showAlert("Success", "Course deleted successfully", Alert.AlertType.INFORMATION);
                    refreshTable();
                    clearForm();
                } catch (Exception e) {
                    showAlert("Error", "Failed to delete course: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    @FXML
    private void handleUpload() {
        File file = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());
        if (file != null) {
            try {
                selectedImageFile = file;
                imagePathField.setText(file.getName());
                imagePreview.setImage(new Image(file.toURI().toString()));
            } catch (Exception e) {
                showAlert("Error", "Failed to load image: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private Cours createCoursFromForm() throws ValidationException, IOException {
        validateFields();

        Cours cours = new Cours();
        cours.setTitre(titreField.getText().trim());
        cours.setDescription(descriptionArea.getText().trim());
        cours.setContenu(contenuArea.getText().trim());
        cours.setCategorie(categorieCombo.getValue());

        if (selectedImageFile != null) {
            cours.setImageFile(selectedImageFile);
            // The actual image path will be set by the service when saving the file
        }

        return cours;
    }

    private void validateFields() throws ValidationException {
        if (titreField.getText().trim().isEmpty()) {
            throw new ValidationException("Title is required");
        }
        if (titreField.getText().trim().length() < 3 || titreField.getText().trim().length() > 50) {
            throw new ValidationException("Title must be between 3-50 characters");
        }
        if (descriptionArea.getText().trim().isEmpty()) {
            throw new ValidationException("Description is required");
        }
        if (descriptionArea.getText().trim().length() < 10 || descriptionArea.getText().trim().length() > 255) {
            throw new ValidationException("Description must be between 10-255 characters");
        }
        if (contenuArea.getText().trim().isEmpty()) {
            throw new ValidationException("Content is required");
        }
        if (contenuArea.getText().trim().length() < 20) {
            throw new ValidationException("Content must be at least 20 characters");
        }
        if (categorieCombo.getValue() == null) {
            throw new ValidationException("Category is required");
        }
    }

    public void refreshTable() {
        coursList.setAll(coursService.getAllCours());
        coursTable.setItems(coursList);
    }

    private void setFormData(Cours cours) {
        titreField.setText(cours.getTitre());
        descriptionArea.setText(cours.getDescription());
        contenuArea.setText(cours.getContenu());
        categorieCombo.setValue(cours.getCategorie());
        imagePathField.setText(cours.getImagePath());

        if (cours.getImagePath() != null && !cours.getImagePath().isEmpty()) {
            try {
                File imageFile = new File(CoursService.UPLOAD_DIR + cours.getImagePath());
                if (imageFile.exists()) {
                    selectedImageFile = imageFile;
                    imagePreview.setImage(new Image(imageFile.toURI().toString()));
                } else {
                    imagePreview.setImage(null);
                }
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
                imagePreview.setImage(null);
            }
        } else {
            imagePreview.setImage(null);
        }
    }

    private void clearForm() {
        titreField.clear();
        descriptionArea.clear();
        contenuArea.clear();
        categorieCombo.getSelectionModel().clearSelection();
        imagePathField.clear();
        imagePreview.setImage(null);
        selectedImageFile = null;
        coursTable.getSelectionModel().clearSelection();
        selectedCours = null;
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