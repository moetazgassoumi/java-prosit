package com.example.educonnect.educonnect.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.educonnect.educonnect.Entities.User;
import com.example.educonnect.educonnect.Interfaces.EntityCrud;
import com.example.educonnect.educonnect.Utils.DataBase;
import java.sql.*;
import com.example.educonnect.educonnect.Entities.UserRole;
import com.example.educonnect.educonnect.Exceptions.DatabaseException;


import java.security.NoSuchAlgorithmException;

import static com.example.educonnect.educonnect.Repository.AuthRepository.hashPassword2;

public class UserRepository implements EntityCrud<User> {
    private final Connection conn = DataBase.getInstance().getConnection();

    public int countusers() throws SQLException {
        String query = "SELECT COUNT(*) FROM user";
        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet resultSet = ps.executeQuery();
        if (resultSet.next()){
            return resultSet.getInt("COUNT");
        }else {
            return 0;
        }
    }
    public List <User> getUsersByRole(String role) throws SQLException{
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user WHERE role = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, role);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setCIN(rs.getInt("CIN"));
            user.setNom(rs.getString("nom"));
            user.setPrenom(rs.getString("prenom"));
            user.setEmail(rs.getString("email"));
            user.setTelephone(rs.getInt("telephone"));
            user.setLieu(rs.getString("lieu"));
            user.setAdresse(rs.getString("adresse"));
            user.setDateNss(rs.getDate("dateNss"));
            user.setStatus(rs.getString("status"));
            user.setSpecialite(rs.getString("specialite"));
            user.setPhotoUrl(rs.getString("photoUrl"));
            users.add(user);

        }
        return users;
    }
    @Override
    //create
    public void addEntity(User user) throws DatabaseException {
        String sql;
        if (user.getRole() == UserRole.FORMATEUR) {
            sql = "INSERT INTO user (nom, prenom, CIN, telephone, email, password, dateNss, lieu, adresse, role, salaire, specialite, photoUrl) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            sql = "INSERT INTO user (nom, prenom, CIN, telephone, email, password, dateNss, lieu, adresse, role, photoUrl) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setInt(3, user.getCIN());
            pstmt.setInt(4, user.getTelephone());
            pstmt.setString(5, user.getEmail());
            pstmt.setString(6, hashPassword2(user.getPassword())); // Hash du mot de passe
            pstmt.setDate(7, new Date(user.getDateNss().getTime()));
            pstmt.setString(8, user.getLieu());
            pstmt.setString(9, user.getAdresse());
            pstmt.setString(10, user.getRole().toString());

            if (user.getRole() == UserRole.FORMATEUR) {
                pstmt.setFloat(11, user.getSalaire());
                pstmt.setString(12, user.getSpecialite());
                pstmt.setString(13, user.getPhotoUrl());
            } else {
                pstmt.setString(11, user.getPhotoUrl());
            }

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);
                    user.setId(userId);
                }
            }

            System.out.println("Utilisateur ajouté avec succès.");

        } catch (SQLException | NoSuchAlgorithmException e) {
            throw new DatabaseException("Échec de l'insertion de l'utilisateur", e);
        }

    }
    @Override
    //update
    public void updateEntity(User user) throws DatabaseException {
        String sql;
        if (user.getRole() == UserRole.FORMATEUR) {
            sql = "UPDATE user SET nom = ?, prenom = ?, CIN = ?, telephone = ?, email = ?, password = ?, dateNss = ?, lieu = ?, adresse = ?, role = ?, salaire = ?, specialite = ?, photoUrl = ? WHERE id = ?";
        } else {
            sql = "UPDATE user SET nom = ?, prenom = ?, CIN = ?, telephone = ?, email = ?, password = ?, dateNss = ?, lieu = ?, adresse = ?, role = ?, photoUrl = ? WHERE id = ?";
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setInt(3, user.getCIN());
            pstmt.setInt(4, user.getTelephone());
            pstmt.setString(5, user.getEmail());
            pstmt.setString(6, hashPassword2(user.getPassword())); // Hashage du mot de passe
            pstmt.setDate(7, new Date(user.getDateNss().getTime()));
            pstmt.setString(8, user.getLieu());
            pstmt.setString(9, user.getAdresse());
            pstmt.setString(10, user.getRole().toString());

            if (user.getRole() == UserRole.FORMATEUR) {
                pstmt.setFloat(11, user.getSalaire());
                pstmt.setString(12, user.getSpecialite());
                pstmt.setString(13, user.getPhotoUrl());
                pstmt.setInt(14, user.getId()); // ID pour la clause WHERE
            } else {
                pstmt.setString(11, user.getPhotoUrl());
                pstmt.setInt(12, user.getId()); // ID pour la clause WHERE
            }

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("User updated successfully.");
            } else {
                System.out.println("User not found.");
            }
        } catch (SQLException | NoSuchAlgorithmException e) {
            throw new DatabaseException("Failed to update user", e);
        }
    }
    //delete
    @Override
    public void deleteEntity(int id) throws DatabaseException {
        String sql = "DELETE FROM user WHERE id = ?";
        try(PreparedStatement psmt = conn.prepareStatement(sql)){
            psmt.setInt(1,id);
            int rowsAffected = psmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User deleted successfully.");
            }else {
                System.out.println("User not found .");
            }
        }catch (SQLException e) {
            throw new DatabaseException("failed to delete user",e);
        }
    }
    @Override
    public List<User> displayEntities() throws DatabaseException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user";
        try(Statement stmt= conn.createStatement();ResultSet rs = stmt.executeQuery(sql)){
            while (rs.next()){
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setCIN(rs.getInt("CIN"));
                user.setEmail(rs.getString("email"));
                user.setTelephone(rs.getInt("telephone"));
                user.setLieu(rs.getString("lieu"));
                user.setAdresse(rs.getString("adresse"));
                user.setDateNss(rs.getDate("dateNess"));
                user.setRole(UserRole.valueOf(rs.getString("role")));
                user.setSpecialite(rs.getString("specialite"));
                user.setSalaire(rs.getFloat("salaire"));
                user.setPhotoUrl(rs.getString("photo"));
                users.add(user);
            }
        }catch (SQLException e){
            throw new DatabaseException("failed to retrieve user",e);
        }
        return users;
    }
    @Override
    public User findEntity(int id) {
        return null;
    }

    public User getUserByEmail(String email) {
        String query = "SELECT * FROM user WHERE email = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setNom(rs.getString("nom"));
                    user.setPrenom(rs.getString("prenom"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(UserRole.valueOf(rs.getString("role")));
                    user.setTelephone(rs.getInt("telephone"));
                    user.setDateNss(rs.getDate("dateNess"));
                    user.setLieu(rs.getString("lieu"));
                    user.setAdresse(rs.getString("adresse"));
                    user.setPhotoUrl(rs.getString("photo"));
                    return user;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve user by email", e);
        }

        return null; // Return null if the user is not found
    }
}
