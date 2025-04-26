package com.example.educonnect.educonnect.Controllers;

import com.example.educonnect.educonnect.Entities.User;
import com.example.educonnect.educonnect.Entities.UserRole;
import com.example.educonnect.educonnect.Repository.UserRepository;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import com.itextpdf.layout.properties.UnitValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;

public class UserTableController {

    @FXML private Label roleLabel;
    @FXML private ComboBox<String> roleFilterComboBox;
    @FXML private TextField searchField;
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
    private ObservableList<User> originalUserList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        roleFilterComboBox.getItems().addAll("ADMIN", "FORMATEUR", "MEMBRE");

        nomColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNom()));
        prenomColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPrenom()));
        emailColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        cinColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCIN()).asObject());
        telColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getTelephone()).asObject());
        lieuColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLieu()));
        adresseColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAdresse()));
        dateNssColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDateNss().toString()));
        roleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRole().toString()));
    }

    @FXML
    public void handleRoleSelection() {
        String selectedRole = roleFilterComboBox.getValue();
        if (selectedRole != null && !selectedRole.isEmpty()) {
            loadData(selectedRole);
        }
    }

    @FXML
    public void loadData(String role) {
        try {
            UserRole selectedRole = UserRole.valueOf(role.toUpperCase());
            roleLabel.setText("Rôle : " + selectedRole);

            ObservableList<User> users = FXCollections.observableArrayList(userRepo.getUsersByRole(role.toUpperCase()));
            originalUserList.setAll(users);
            userTableView.setItems(originalUserList);

            if (selectedRole == UserRole.FORMATEUR) {
                specialiteColumn.setVisible(true);
                salaireColumn.setVisible(true);
            } else {
                specialiteColumn.setVisible(false);
                salaireColumn.setVisible(false);
            }

        } catch (IllegalArgumentException | SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Erreur : " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    public void handleSearch() {
        String keyword = searchField.getText().toLowerCase();

        ObservableList<User> filteredList = originalUserList.filtered(user ->
                String.valueOf(user.getCIN()).contains(keyword) ||
                        user.getEmail().toLowerCase().contains(keyword) ||
                        String.valueOf(user.getTelephone()).contains(keyword)
        );

        userTableView.setItems(filteredList);
    }

    @FXML
    public void handleExportPDF() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer en PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File file = fileChooser.showSaveDialog(null);
            if (file == null) return;

            PdfWriter writer = new PdfWriter(file.getAbsolutePath());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Liste complète des utilisateurs\n\n"));

            float[] columnWidths = {1, 1, 2, 1, 1, 1, 2, 1, 1, 1, 1}; // Proportions
            Table table = new Table(columnWidths);
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell("Nom");
            table.addHeaderCell("Prénom");
            table.addHeaderCell("Email");
            table.addHeaderCell("CIN");
            table.addHeaderCell("Téléphone");
            table.addHeaderCell("Lieu");
            table.addHeaderCell("Adresse");
            table.addHeaderCell("Date Naissance");
            table.addHeaderCell("Rôle");
            table.addHeaderCell("Spécialité");
            table.addHeaderCell("Salaire");

            for (User user : userTableView.getItems()) {
                table.addCell(user.getNom());
                table.addCell(user.getPrenom());
                table.addCell(user.getEmail());
                table.addCell(String.valueOf(user.getCIN()));
                table.addCell(String.valueOf(user.getTelephone()));
                table.addCell(user.getLieu());
                table.addCell(user.getAdresse());
                table.addCell(user.getDateNss().toString());
                table.addCell(user.getRole().toString());

                if (user.getRole() == UserRole.FORMATEUR) {
                    table.addCell(user.getSpecialite() != null ? user.getSpecialite() : "-");
                    table.addCell(String.valueOf(user.getSalaire()));
                } else {
                    table.addCell("-");
                    table.addCell("-");
                }
            }

            document.add(table);
            document.close();

            new Alert(Alert.AlertType.INFORMATION, "Export PDF réussi !").showAndWait();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur : Fichier introuvable.").showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l'export PDF : " + e.getMessage()).showAndWait();
        }
    }
    @FXML
    public void handleBrowse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            photoField.setText(file.getAbsolutePath());
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

}
