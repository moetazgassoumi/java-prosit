package com.esprit.Controllers.Karim;

import com.esprit.Models.Event;
import com.esprit.Services.EventService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FrontController {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private FlowPane coursesContainer;

    private final EventService eventService = new EventService();

    @FXML
    public void initialize() {
        loadEvents();
        setupContainerStyle();
    }

    private void loadEvents() {
        List<Event> events = eventService.getAllEvents();
        for (Event event : events) {
            coursesContainer.getChildren().add(createEventCard(event));
        }
    }
    @FXML
    private void goToHome() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Views/homepage.fxml"));
            Stage stage = (Stage) scrollPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("EduConnect - Home");
        } catch (Exception e) {
            System.err.println("Error loading home page: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private VBox createEventCard(Event event) {
        VBox card = new VBox();
        card.getStyleClass().add("event-card");
        card.setAlignment(Pos.TOP_CENTER);
        card.setSpacing(10);
        card.setPadding(new Insets(15));
        card.setPrefWidth(300);
        card.setMaxWidth(300);

        // Image - Fixed image loading
        ImageView imageView = new ImageView();
        imageView.setFitWidth(270);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(true);

        try {
            if (event.getImagePath() != null && !event.getImagePath().isEmpty()) {
                // Try loading from resources first
                InputStream imageStream = getClass().getResourceAsStream("/images/events/" + event.getImagePath());
                if (imageStream != null) {
                    imageView.setImage(new Image(imageStream));
                } else {
                    // Fallback to file system if not found in resources
                    imageView.setImage(new Image("file:uploads/events/" + event.getImagePath()));
                }
            } else {
                // Default placeholder if no image specified
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            try {
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
            } catch (Exception ex) {
                System.err.println("Couldn't load placeholder image either");
            }
        }

        // Title
        Label titleLabel = new Label(event.getTitle());
        titleLabel.getStyleClass().add("event-title");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(270);

        // Start DateTime
        Label startLabel = new Label("Starts: " + event.getStartDatetime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        startLabel.getStyleClass().add("event-datetime");

        // Location
        Label locationLabel = new Label("Location: " + event.getLocation());
        locationLabel.getStyleClass().add("event-location");

        // Category
        Label categoryLabel = new Label();
        if (event.getCategory() != null) {
            categoryLabel.setText("Category: " + event.getCategory().getName());
        }
        categoryLabel.getStyleClass().add("event-category");

        // Description
        Text descriptionText = new Text(event.getDescription());
        descriptionText.setWrappingWidth(270);
        descriptionText.getStyleClass().add("event-description");

        card.getChildren().addAll(imageView, titleLabel, startLabel, locationLabel, categoryLabel, descriptionText);
        return card;
    }

    private void setupContainerStyle() {
        coursesContainer.setPadding(new Insets(20));
        coursesContainer.setHgap(20);
        coursesContainer.setVgap(20);
        coursesContainer.setAlignment(Pos.CENTER);

        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }
}