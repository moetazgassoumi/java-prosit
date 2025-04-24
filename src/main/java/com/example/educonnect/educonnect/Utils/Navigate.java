package com.example.educonnect.educonnect.Utils;

import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class Navigate {

    private static final int width = 1100;
    private static final int height = 600;

    public static void navigate(Node component, String fxmlPath, Stage stage) {
        component.setDisable(true); // Prevent multiple clicks

        MFXProgressSpinner progressIndicator = new MFXProgressSpinner();
        progressIndicator.setPrefSize(70, 70);
        progressIndicator.setStyle("-fx-progress-color: #0C162C;");

        VBox container = new VBox(progressIndicator);
        container.setAlignment(Pos.CENTER);

        Parent currentRoot = stage.getScene().getRoot();
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), currentRoot);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        FXMLLoader fxmlLoader = new FXMLLoader(com.example.educonnect.educonnect.Main.class.getResource(fxmlPath));
        Parent newRoot;
        try {
            newRoot = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            component.setDisable(false);
            return;
        }

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), newRoot);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> {
                    stage.setScene(new Scene(newRoot, width, height));
                    fadeIn.play();
                    component.setDisable(false);
                })
        );

        fadeOut.setOnFinished(e -> timeline.play());
        fadeOut.play();

        stage.setScene(new Scene(container, width, height));
    }
}
