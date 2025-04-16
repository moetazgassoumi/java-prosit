package com.esprit.Controllers;
import com.esprit.Entities.User;
import com.esprit.Entities.UserRole;
import com.esprit.Repository.UserRepository;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.SQLException;

public class UserTableController {

    @FXML private Label roleLabel;
    @FXML private TableView<User> userTableView;

    @FXML private TableColumn<User, String> nomColumn;
    @FXML private TableColumn<User, String> prenomColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, Integer> cinColumn;
    @FXML private TableColumn<User, Integer> telColumn;
    @FXML private TableColumn<User, String> lieuColumn;
    @FXML private TableColumn<User, String> adresseColumn;
    @FXML private TableColumn<User, String> dateNssColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> specialiteColumn;
    @FXML private TableColumn<User, Float> salaireColumn;

    @FXML private TextField emailField;
    @FXML private TextField telField;
    @FXML private TextField photoField;
    @FXML private Button updateBtn;
    @FXML private Button deleteBtn;
    @FXML private Button browseBtn;

    private final UserRepository userRepo = new UserRepository();
    private User selectedUser;

    @FXML
    public void initialize() {
        nomColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNom()));
        prenomColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPrenom()));
        emailColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        cinColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCIN()).asObject());
        telColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getTelephone()).asObject());
        lieuColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLieu()));
        adresseColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAdresse()));
        dateNssColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDateNss().toString()));
        roleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRole().toString()));

        userTableView.setOnMouseClicked(event -> {
            selectedUser = userTableView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                emailField.setText(selectedUser.getEmail());
                telField.setText(String.valueOf(selectedUser.getTelephone()));
                photoField.setText(selectedUser.getPhotoUrl());
            }
        });
    }

    public void loadData(String role) {
        try {
            UserRole selectedRole = UserRole.valueOf(role.toUpperCase());
            roleLabel.setText("Rôle : " + selectedRole);

            ObservableList<User> users = FXCollections.observableArrayList(userRepo.getUsersByRole(role.toUpperCase()));
            userTableView.setItems(users);

            if (selectedRole == UserRole.FORMATEUR) {
                specialiteColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSpecialite()));
                salaireColumn.setCellValueFactory(data -> new SimpleFloatProperty(data.getValue().getSalaire()).asObject());
                if (!userTableView.getColumns().contains(specialiteColumn)) {
                    userTableView.getColumns().addAll(specialiteColumn, salaireColumn);
                }
            } else {
                userTableView.getColumns().removeAll(specialiteColumn, salaireColumn);
            }

        } catch (IllegalArgumentException | SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Erreur : " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    public void handleUpdate() {
        if (selectedUser != null) {
            selectedUser.setEmail(emailField.getText());
            selectedUser.setTelephone(Integer.parseInt(telField.getText()));
            selectedUser.setPhotoUrl(photoField.getText());
            userRepo.updateEmailPhoneAndImage(selectedUser);
            loadData(selectedUser.getRole().toString());
        } else {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un utilisateur").showAndWait();
        }
    }

    @FXML
    public void handleDelete() {
        if (selectedUser != null) {
            userRepo.deleteEntity(selectedUser.getId());
            loadData(selectedUser.getRole().toString());
        } else {
            new Alert(Alert.AlertType.WARNING, "Aucun utilisateur sélectionné").showAndWait();
        }
    }

    @FXML
    public void handleBrowse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            photoField.setText(file.getAbsolutePath());
        }
    }
}