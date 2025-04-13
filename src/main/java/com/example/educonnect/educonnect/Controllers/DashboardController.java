package com.example.educonnect.educonnect.Controllers;

import com.example.educonnect.educonnect.Entities.UserRole;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class DashboardController {

    @FXML
    private ComboBox<UserRole> roleFilterComboBox;

    @FXML
    private AnchorPane contentPane;

    @FXML
    public void initialize() {
        roleFilterComboBox.getItems().setAll(UserRole.values());
        roleFilterComboBox.setOnAction(event -> {
            UserRole selectedRole = roleFilterComboBox.getValue();
            if (selectedRole != null) {
                loadUserTable(selectedRole.name());
            }
        });
    }

    private void loadUserTable(String role) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/educonnect/educonnect/Views/user-table.fxml"));
            Parent userTableView = loader.load();

            UserTableController controller = loader.getController();
            controller.loadData(role);

            contentPane.getChildren().setAll(userTableView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
