package com.esprit.Services;

import com.esprit.Connexion.DatabaseConnection;
import com.esprit.Models.Reclamation;
import com.esprit.Models.Reponse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReclamationService {
    private Connection connection;

    public ReclamationService() {
        connection = DatabaseConnection.getConnection();
    }

    public List<Reclamation> getAllReclamations() {
        List<Reclamation> reclamations = new ArrayList<>();
        String query = "SELECT * FROM reclamation ORDER BY date_creation DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Reclamation reclamation = new Reclamation();
                reclamation.setId(rs.getInt("id"));
                reclamation.setMessage(rs.getString("message"));
                reclamation.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());

                // Load responses for this reclamation
                reclamation.getReponses().addAll(getReponsesForReclamation(reclamation.getId()));
                reclamations.add(reclamation);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching reclamations: " + e.getMessage());
        }
        return reclamations;
    }

    public void addReclamation(Reclamation reclamation) {
        String query = "INSERT INTO reclamation (message, date_creation) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, reclamation.getMessage());
            pstmt.setTimestamp(2, Timestamp.valueOf(reclamation.getDateCreation()));
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reclamation.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding reclamation: " + e.getMessage());
        }
    }

    public void updateReclamation(Reclamation reclamation) {
        String query = "UPDATE reclamation SET message = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, reclamation.getMessage());
            pstmt.setInt(2, reclamation.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating reclamation: " + e.getMessage());
        }
    }

    public void deleteReclamation(int id) {
        // Responses will be deleted automatically due to ON DELETE CASCADE
        String query = "DELETE FROM reclamation WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting reclamation: " + e.getMessage());
        }
    }

    public boolean reclamationExists(String message) {
        String query = "SELECT COUNT(*) FROM reclamation WHERE LOWER(message) = LOWER(?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, message);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking reclamation existence: " + e.getMessage());
        }
        return false;
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

    private List<Reponse> getReponsesForReclamation(int reclamationId) {
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

    public void deleteReponse(int id) {
        String query = "DELETE FROM reponse WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting response: " + e.getMessage());
        }
    }
}