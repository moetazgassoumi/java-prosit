package com.esprit.Services;

import com.esprit.Connexion.DatabaseConnection;
import com.esprit.Models.Categorie;
import com.esprit.Models.Cours;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CoursService {
    private Connection connection;
    public static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/cours/";

    public CoursService() {
        connection = DatabaseConnection.getConnection();
        // Create upload directory if it doesn't exist
        new File(UPLOAD_DIR).mkdirs();
    }

    public List<Cours> getAllCours() {
        List<Cours> coursList = new ArrayList<>();
        String query = "SELECT c.*, cat.nom_categorie FROM cours c LEFT JOIN categorie cat ON c.categorie_id = cat.id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Cours cours = new Cours();
                cours.setId(rs.getInt("id"));
                cours.setTitre(rs.getString("titre"));
                cours.setDescription(rs.getString("description"));
                cours.setContenu(rs.getString("contenu"));
                cours.setImagePath(rs.getString("image_path"));

                Categorie categorie = new Categorie();
                categorie.setId(rs.getInt("categorie_id"));
                categorie.setNomCategorie(rs.getString("nom_categorie"));
                cours.setCategorie(categorie);

                coursList.add(cours);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching courses: " + e.getMessage());
        }
        return coursList;
    }

    public void addCours(Cours cours) throws IOException {
        String query = "INSERT INTO cours (titre, description, contenu, categorie_id, image_path) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            // Save image if exists
            if (cours.getImageFile() != null) {
                String uniqueFileName = saveUploadedFile(cours.getImageFile());
                cours.setImagePath(uniqueFileName);
            }

            pstmt.setString(1, cours.getTitre());
            pstmt.setString(2, cours.getDescription());
            pstmt.setString(3, cours.getContenu());
            pstmt.setInt(4, cours.getCategorie().getId());
            pstmt.setString(5, cours.getImagePath());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cours.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding course: " + e.getMessage());
        }
    }

    public void updateCours(Cours cours) throws IOException {
        String query = "UPDATE cours SET titre = ?, description = ?, contenu = ?, categorie_id = ?, image_path = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            // Handle image update
            if (cours.getImageFile() != null) {
                // Delete old image if exists
                if (cours.getImagePath() != null) {
                    File oldImage = new File(UPLOAD_DIR + cours.getImagePath());
                    if (oldImage.exists()) {
                        oldImage.delete();
                    }
                }
                // Save new image
                String uniqueFileName = saveUploadedFile(cours.getImageFile());
                cours.setImagePath(uniqueFileName);
            }

            pstmt.setString(1, cours.getTitre());
            pstmt.setString(2, cours.getDescription());
            pstmt.setString(3, cours.getContenu());
            pstmt.setInt(4, cours.getCategorie().getId());
            pstmt.setString(5, cours.getImagePath());
            pstmt.setInt(6, cours.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating course: " + e.getMessage());
        }
    }

    public void deleteCours(int id) {
        // First get the course to delete its image
        Cours cours = getCoursById(id);
        if (cours != null && cours.getImagePath() != null) {
            File imageFile = new File(UPLOAD_DIR + cours.getImagePath());
            if (imageFile.exists()) {
                imageFile.delete();
            }
        }

        String query = "DELETE FROM cours WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting course: " + e.getMessage());
        }
    }

    private Cours getCoursById(int id) {
        String query = "SELECT * FROM cours WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Cours cours = new Cours();
                    cours.setId(rs.getInt("id"));
                    cours.setTitre(rs.getString("titre"));
                    cours.setDescription(rs.getString("description"));
                    cours.setContenu(rs.getString("contenu"));
                    cours.setImagePath(rs.getString("image_path"));
                    return cours;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching course: " + e.getMessage());
        }
        return null;
    }

    private String saveUploadedFile(File file) throws IOException {
        // Generate unique filename
        String originalFileName = file.getName();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        // Save file to upload directory
        File destination = new File(UPLOAD_DIR + uniqueFileName);
        Files.copy(file.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

        return uniqueFileName;
    }
}