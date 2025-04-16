package com.esprit.Models;

import com.esprit.exceptions.ValidationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Category {
    private Integer id;
    private String name;
    private ObservableList<Event> events = FXCollections.observableArrayList();

    // Constructors
    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObservableList<Event> getEvents() {
        return events;
    }

    // Relationship management methods
    public void addEvent(Event event) {
        if (!events.contains(event)) {
            events.add(event);
            event.setCategory(this);
        }
    }

    public void removeEvent(Event event) {
        if (events.remove(event)) {
            if (event.getCategory() == this) {
                event.setCategory(null);
            }
        }
    }

    // Validation method
    public void validate() throws ValidationException {
        // Not empty validation
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Category name cannot be empty");
        }

        // Letters only validation (Regex)
        if (!name.matches("^[a-zA-Z]+$")) {
            throw new ValidationException("Category name must only contain letters");
        }
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return name.equals(category.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}