package com.example.educonnect.educonnect.Utils;

import com.example.educonnect.educonnect.Entities.UserRole;
import com.example.educonnect.educonnect.Main;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class RoleNavigation {

    public static void navigateUser(Stage stage, UserRole role) throws IOException {
        String fxmlResource;
        double width;
        double height;

        // Choisir le fichier FXML et dimensions selon le rôle
        if (role == UserRole.ADMIN) {
            fxmlResource = "/com/example/educonnect/educonnect/views/Dashboard.fxml";
            width = 1100.0;
            height = 600.0;
        } else {
            fxmlResource = "/com/example/educonnect/educonnect/views/UserAccount.fxml";
            width = 1200.0;
            height = 700.0;
        }

        // Afficher un loader
        MFXProgressSpinner progressIndicator = new MFXProgressSpinner();
        progressIndicator.setPrefSize(70, 70);
        progressIndicator.setStyle("-fx-progress-color: #0C162C;");

        VBox container = new VBox(progressIndicator);
        container.setAlignment(Pos.CENTER);

        // Mettre l'animation de chargement
        stage.setScene(new Scene(container, width, height));

        // Transition après délai (chargement simulé de 0.5s)
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlResource));
                        Parent root = loader.load();

                        // Transition de fondu entrant
                        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), root);
                        fadeIn.setFromValue(0.0);
                        fadeIn.setToValue(1.0);

                        stage.setScene(new Scene(root, width, height));
                        fadeIn.play();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                })
        );

        timeline.play();
    }
}
