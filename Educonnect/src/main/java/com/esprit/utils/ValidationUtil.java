package com.esprit.utils;

import com.esprit.Models.Category;
import com.esprit.Models.Event;
import com.esprit.exceptions.ValidationException;

import java.io.File;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

public final class ValidationUtil {

    // Constants
    private static final long MAX_IMAGE_SIZE = 2 * 1024 * 1024; // 2MB
    private static final String[] ALLOWED_IMAGE_EXTENSIONS = {".jpg", ".jpeg", ".png"};
    private static final String LETTERS_ONLY_REGEX = "^[a-zA-Z\\s]+$";
    private static final int MIN_EVENT_DURATION = 15; // minutes
    private static final int MAX_EVENT_DURATION = 1440; // 24 hours

    private ValidationUtil() {
        // Private constructor to prevent instantiation
    }

    // Category validations (unchanged)
    public static void validateCategory(Category category) throws ValidationException {
        Objects.requireNonNull(category, "Category cannot be null");
        validateNotEmpty(category.getName(), "Category name");
        validateLettersOnly(category.getName(), "Category name");
        validateStringLength(category.getName(), "Category name", 3, 50);
    }

    public static void validateCategoryUnique(Category category, List<Category> existingCategories)
            throws ValidationException {
        Objects.requireNonNull(existingCategories, "Existing categories list cannot be null");

        if (existingCategories.stream()
                .anyMatch(existing ->
                        !existing.equals(category) &&
                                existing.getName().equalsIgnoreCase(category.getName()))) {
            throw new ValidationException("Category name must be unique");
        }
    }

    // Updated Event validations to match property names
    public static void validateEvent(Event event) throws ValidationException {
        Objects.requireNonNull(event, "Event cannot be null");

        // Basic field validations
        validateNotEmpty(event.getTitle(), "Title");
        validateStringLength(event.getTitle(), "Title", 5, 100);

        validateNotEmpty(event.getLocation(), "Location");
        validateStringLength(event.getLocation(), "Location", 5, 100);

        validatePositive(event.getDuration(), "Duration");
        validateDurationRange(event.getDuration());

        validatePositive(event.getMaxParticipants(), "Maximum participants");
        validateMaxParticipants(event.getMaxParticipants());

        // Date validations
        validateDateRange(event.getStartDatetime(), event.getEndDatetime());

        // Category validation
        if (event.getCategory() == null) {
            throw new ValidationException("Category is required");
        }

        // Description validation (optional)
        if (event.getDescription() != null && !event.getDescription().isEmpty()) {
            validateStringLength(event.getDescription(), "Description", 0, 500);
        }

        // Image path validation
        if (event.getImagePath() != null && !event.getImagePath().isEmpty()) {
            validateStringLength(event.getImagePath(), "Image path", 0, 255);
        }
    }

    // Field-level validations (unchanged)
    public static void validateNotEmpty(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty");
        }
    }

    public static void validateLettersOnly(String value, String fieldName) throws ValidationException {
        if (!value.matches(LETTERS_ONLY_REGEX)) {
            throw new ValidationException(fieldName + " must contain only letters and spaces");
        }
    }

    public static void validateStringLength(String value, String fieldName, int min, int max)
            throws ValidationException {
        if (value.length() < min) {
            throw new ValidationException(
                    String.format("%s must be at least %d characters long", fieldName, min));
        }
        if (value.length() > max) {
            throw new ValidationException(
                    String.format("%s cannot exceed %d characters", fieldName, max));
        }
    }

    public static void validatePositive(Integer value, String fieldName) throws ValidationException {
        if (value == null || value <= 0) {
            throw new ValidationException(fieldName + " must be a positive number");
        }
    }

    public static void validateDurationRange(int duration) throws ValidationException {
        if (duration < MIN_EVENT_DURATION) {
            throw new ValidationException(
                    String.format("Duration must be at least %d minutes", MIN_EVENT_DURATION));
        }
        if (duration > MAX_EVENT_DURATION) {
            throw new ValidationException(
                    String.format("Duration cannot exceed %d minutes (24 hours)", MAX_EVENT_DURATION));
        }
    }

    public static void validateMaxParticipants(int maxParticipants) throws ValidationException {
        if (maxParticipants > 1000) {
            throw new ValidationException("Maximum participants cannot exceed 1000");
        }
    }

    public static void validateDateRange(LocalDateTime start, LocalDateTime end) throws ValidationException {
        Objects.requireNonNull(start, "Start date cannot be null");
        Objects.requireNonNull(end, "End date cannot be null");

        if (start.isAfter(end)) {
            throw new ValidationException("Start date must be before end date");
        }
        if (start.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Start date cannot be in the past");
        }
        if (ChronoUnit.MINUTES.between(start, end) < 15) {
            throw new ValidationException("Event must last at least 15 minutes");
        }
    }

    // Image validation (updated to work with imagePath)
    public static void validateImagePath(String imagePath) throws ValidationException {
        if (imagePath == null || imagePath.isEmpty()) {
            return;
        }

        // Check file extension
        String lowerPath = imagePath.toLowerCase();
        boolean validExtension = false;
        for (String ext : ALLOWED_IMAGE_EXTENSIONS) {
            if (lowerPath.endsWith(ext)) {
                validExtension = true;
                break;
            }
        }

        if (!validExtension) {
            throw new ValidationException(
                    "Only " + String.join(", ", ALLOWED_IMAGE_EXTENSIONS) + " images are allowed");
        }
    }

    // Keep the file validation for when you're actually uploading files
    public static void validateImageFile(File imageFile) throws ValidationException {
        if (imageFile == null) return;

        if (!imageFile.exists()) {
            throw new ValidationException("Image file does not exist");
        }

        if (imageFile.length() > MAX_IMAGE_SIZE) {
            throw new ValidationException(
                    String.format("Image size must be less than %dMB", MAX_IMAGE_SIZE / (1024 * 1024)));
        }

        validateImagePath(imageFile.getName());
    }
}