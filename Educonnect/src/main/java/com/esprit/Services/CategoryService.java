package com.esprit.Services;

import com.esprit.Connexion.DatabaseConnection;
import com.esprit.Models.Category;
import com.esprit.exceptions.UniqueConstraintException;
import com.esprit.exceptions.ValidationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class CategoryService {
    private final Connection connection;

    public CategoryService() {
        this.connection = DatabaseConnection.getConnection();
    }

    // Create
    public void addCategory(Category category) throws ValidationException, UniqueConstraintException {
        String query = "INSERT INTO categories (name) VALUES (?)";

        validateCategory(category);

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, category.getName());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    category.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new UniqueConstraintException("Category name already exists");
        } catch (SQLException e) {
            throw new ValidationException("Failed to add category: " + e.getMessage());
        }
    }

    // Read
    public ObservableList<Category> getAllCategories() {
        ObservableList<Category> categories = FXCollections.observableArrayList();
        String query = "SELECT id, name FROM categories ORDER BY name";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Category category = new Category(resultSet.getString("name"));
                category.setId(resultSet.getInt("id"));
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public Category getCategoryById(int id) {
        String query = "SELECT name FROM categories WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Category category = new Category(resultSet.getString("name"));
                    category.setId(id);
                    return category;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update
    public void updateCategory(Category category) throws ValidationException, UniqueConstraintException {
        String query = "UPDATE categories SET name = ? WHERE id = ?";

        validateCategory(category);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, category.getName());
            statement.setInt(2, category.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new ValidationException("Category not found");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new UniqueConstraintException("Category name already exists");
        } catch (SQLException e) {
            throw new ValidationException("Failed to update category: " + e.getMessage());
        }
    }

    // Delete
    public void deleteCategory(int id) throws ValidationException {
        if (isCategoryInUse(id)) {
            throw new ValidationException("Cannot delete category - it is being used by events");
        }

        String query = "DELETE FROM categories WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new ValidationException("Category not found");
            }
        } catch (SQLException e) {
            throw new ValidationException("Failed to delete category: " + e.getMessage());
        }
    }

    // Validation
    private void validateCategory(Category category) throws ValidationException {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new ValidationException("Category name cannot be empty");
        }
        if (!category.getName().matches("^[a-zA-Z]+$")) {
            throw new ValidationException("Category name must contain only letters");
        }
    }

    private boolean isCategoryInUse(int categoryId) {
        String query = "SELECT COUNT(*) FROM events WHERE category_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, categoryId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}