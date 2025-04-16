package com.esprit.Controllers.Mayssa;

import com.esprit.Models.Cours;
import com.esprit.Services.CoursService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class FrontController implements Initializable {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private FlowPane coursesContainer;

    private final CoursService coursService = new CoursService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupUI();
        loadCourses();
    }

    private void setupUI() {
        // Initialize scrollPane if it's null
        if (scrollPane != null) {
            scrollPane.setFitToWidth(true);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        }

        // Initialize coursesContainer
        if (coursesContainer != null) {
            coursesContainer.setPadding(new Insets(20));
            coursesContainer.setHgap(20);
            coursesContainer.setVgap(20);
            coursesContainer.setAlignment(Pos.CENTER);
        }
    }

    private void loadCourses() {
        if (coursesContainer == null) return;

        coursesContainer.getChildren().clear();
        List<Cours> coursList = coursService.getAllCours();

        for (Cours cours : coursList) {
            coursesContainer.getChildren().add(createCourseCard(cours));
        }
    }

    private VBox createCourseCard(Cours cours) {
        VBox card = new VBox();
        card.getStyleClass().add("course-card");
        card.setAlignment(Pos.TOP_CENTER);
        card.setSpacing(10);
        card.setPadding(new Insets(15));
        card.setPrefWidth(300);
        card.setMaxWidth(300);
        // Course image
        ImageView imageView = new ImageView();
        imageView.getStyleClass().add("course-image");
        imageView.setFitWidth(270);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        try {
            if (cours.getImagePath() != null && !cours.getImagePath().isEmpty()) {
                File imageFile = new File("uploads/cours/" + cours.getImagePath());
                if (imageFile.exists()) {
                    imageView.setImage(new Image(imageFile.toURI().toString()));
                } else {
                    imageView.setImage(new Image(getClass().getResourceAsStream("/images/course-placeholder.png")));
                }
            } else {
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/course-placeholder.png")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Course title
        Label titleLabel = new Label(cours.getTitre());
        titleLabel.getStyleClass().add("course-title");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(270);

        // Course category
        Label categoryLabel = new Label();
        if (cours.getCategorie() != null) {
            categoryLabel.setText("Category: " + cours.getCategorie().getNomCategorie());
        }
        categoryLabel.getStyleClass().add("course-category");

        // Course description
        Text descriptionText = new Text(cours.getDescription());
        descriptionText.getStyleClass().add("course-description");
        descriptionText.setWrappingWidth(270);

        // Add click handler
        card.setOnMouseClicked(event -> openCourseDetails(cours));

        card.getChildren().addAll(imageView, titleLabel, categoryLabel, descriptionText);
        return card;
    }

    private void openCourseDetails(Cours cours) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/CourseDetails.fxml"));
            Parent root = loader.load();

            // Pass the course data to the details controller if needed
            // CourseDetailsController controller = loader.getController();
            // controller.setCourseData(cours);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(cours.getTitre());
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    @FXML
    private void goToEvents() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Views/Events/front_view.fxml"));
            Stage stage = (Stage) scrollPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("EduConnect - Events");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}