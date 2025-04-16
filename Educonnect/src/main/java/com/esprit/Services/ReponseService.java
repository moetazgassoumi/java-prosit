package com.esprit.Services;


import com.esprit.Connexion.DatabaseConnection;
import com.esprit.Models.Reclamation;
import com.esprit.Models.Reponse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReponseService {
    private Connection connection;

    public ReponseService() {
        connection = DatabaseConnection.getConnection();
    }
    public List<Reponse> getAllReponses() {
        List<Reponse> reponses = new ArrayList<>();
        String query = "SELECT r.*, rec.message as reclamation_message FROM reponse r " +
                "LEFT JOIN reclamation rec ON r.reclamation_id = rec.id " +
                "ORDER BY r.date_creation DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Reponse reponse = new Reponse();
                reponse.setId(rs.getInt("id"));
                reponse.setContenu(rs.getString("contenu"));
                reponse.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());

                // If you want to include reclamation information
                if (rs.getInt("reclamation_id") != 0) {
                    Reclamation reclamation = new Reclamation();
                    reclamation.setId(rs.getInt("reclamation_id"));
                    reclamation.setMessage(rs.getString("reclamation_message"));
                    reponse.setReclamation(reclamation);
                }

                reponses.add(reponse);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all responses: " + e.getMessage());
        }
        return reponses;
    }

    public List<Reponse> getReponsesByReclamation(int reclamationId) {
        List<Reponse> reponses = new ArrayList<>();
        String query = "SELECT * FROM reponse WHERE reclamation_id = ? ORDER BY date_creation";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, reclamationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Reponse reponse = new Reponse();
                    reponse.setId(rs.getInt("id"));
                    reponse.setContenu(rs.getString("contenu"));
                    reponse.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
                    reponses.add(reponse);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching responses: " + e.getMessage());
        }
        return reponses;
    }

    public void addReponse(Reponse reponse) {
        String query = "INSERT INTO reponse (contenu, date_creation, reclamation_id) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, reponse.getContenu());
            pstmt.setTimestamp(2, Timestamp.valueOf(reponse.getDateCreation()));
            pstmt.setInt(3, reponse.getReclamation().getId());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reponse.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding response: " + e.getMessage());
        }
    }

    public void updateReponse(Reponse reponse) {
        String query = "UPDATE reponse SET contenu = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, reponse.getContenu());
            pstmt.setInt(2, reponse.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating response: " + e.getMessage());
        }
    }

    public void deleteReponse(int id) {
        String query = "DELETE FROM reponse WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting response: " + e.getMessage());
        }
    }

    public boolean reponseExists(String contenu) {
        String query = "SELECT COUNT(*) FROM reponse WHERE LOWER(contenu) = LOWER(?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, contenu);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking response existence: " + e.getMessage());
        }
        return false;
    }
}