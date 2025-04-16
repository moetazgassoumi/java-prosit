package com.esprit.Models;

import javafx.beans.property.*;

import java.io.File;
import java.time.LocalDateTime;

public class Event {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> startDatetime = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> endDatetime = new SimpleObjectProperty<>();
    private final StringProperty location = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final IntegerProperty duration = new SimpleIntegerProperty();
    private final IntegerProperty maxParticipants = new SimpleIntegerProperty();
    private final StringProperty imagePath = new SimpleStringProperty();
    private final ObjectProperty<Category> category = new SimpleObjectProperty<>();
    private transient File imageFile;

    public Event() {}

    public Event(String title, LocalDateTime startDatetime, LocalDateTime endDatetime,
                 String location, String description, int duration,
                 int maxParticipants, String imagePath, Category category) {
        setTitle(title);
        setStartDatetime(startDatetime);
        setEndDatetime(endDatetime);
        setLocation(location);
        setDescription(description);
        setDuration(duration);
        setMaxParticipants(maxParticipants);
        setImagePath(imagePath);
        setCategory(category);
    }

    // Property getters
    public IntegerProperty idProperty() { return id; }
    public StringProperty titleProperty() { return title; }
    public ObjectProperty<LocalDateTime> startDatetimeProperty() { return startDatetime; }
    public ObjectProperty<LocalDateTime> endDatetimeProperty() { return endDatetime; }
    public StringProperty locationProperty() { return location; }
    public StringProperty descriptionProperty() { return description; }
    public IntegerProperty durationProperty() { return duration; }
    public IntegerProperty maxParticipantsProperty() { return maxParticipants; }
    public StringProperty imagePathProperty() { return imagePath; }
    public ObjectProperty<Category> categoryProperty() { return category; }

    // Regular getters and setters
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public String getTitle() { return title.get(); }
    public void setTitle(String title) { this.title.set(title); }
    public LocalDateTime getStartDatetime() { return startDatetime.get(); }
    public void setStartDatetime(LocalDateTime startDatetime) { this.startDatetime.set(startDatetime); }
    public LocalDateTime getEndDatetime() { return endDatetime.get(); }
    public void setEndDatetime(LocalDateTime endDatetime) { this.endDatetime.set(endDatetime); }
    public String getLocation() { return location.get(); }
    public void setLocation(String location) { this.location.set(location); }
    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }
    public int getDuration() { return duration.get(); }
    public void setDuration(int duration) {
        if (duration <= 0) throw new IllegalArgumentException("Duration must be positive");
        this.duration.set(duration);
    }
    public int getMaxParticipants() { return maxParticipants.get(); }
    public void setMaxParticipants(int maxParticipants) {
        if (maxParticipants <= 0) throw new IllegalArgumentException("Max participants must be positive");
        this.maxParticipants.set(maxParticipants);
    }
    public String getImagePath() { return imagePath.get(); }
    public void setImagePath(String imagePath) { this.imagePath.set(imagePath); }
    public Category getCategory() { return category.get(); }
    public void setCategory(Category category) { this.category.set(category); }
    public File getImageFile() { return imageFile; }
    public void setImageFile(File imageFile) { this.imageFile = imageFile; }

    @Override
    public String toString() { return getTitle(); }
}