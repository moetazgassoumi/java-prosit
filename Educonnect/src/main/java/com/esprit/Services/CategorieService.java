package com.esprit.Services;

import com.esprit.Connexion.DatabaseConnection;
import com.esprit.Models.Categorie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieService {
    private Connection connection;

    public CategorieService() {
        connection = DatabaseConnection.getConnection();
    }

    public List<Categorie> getAllCategories() {
        List<Categorie> categories = new ArrayList<>();
        String query = "SELECT * FROM categorie";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Categorie categorie = new Categorie();
                categorie.setId(rs.getInt("id"));
                categorie.setNomCategorie(rs.getString("nom_categorie"));
                categories.add(categorie);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching categories: " + e.getMessage());
        }
        return categories;
    }

    public void addCategorie(Categorie categorie) {
        String query = "INSERT INTO categorie (nom_categorie) VALUES (?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, categorie.getNomCategorie());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    categorie.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding category: " + e.getMessage());
        }
    }

    public void updateCategorie(Categorie categorie) {
        String query = "UPDATE categorie SET nom_categorie = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, categorie.getNomCategorie());
            pstmt.setInt(2, categorie.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating category: " + e.getMessage());
        }
    }

    public void deleteCategorie(int id) {
        String query = "DELETE FROM categorie WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting category: " + e.getMessage());
        }
    }

    public boolean categoryExists(String nomCategorie) {
        String query = "SELECT COUNT(*) FROM categorie WHERE LOWER(nom_categorie) = LOWER(?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, nomCategorie);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking category existence: " + e.getMessage());
        }
        return false;
    }
}