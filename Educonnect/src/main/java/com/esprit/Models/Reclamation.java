package com.esprit.Models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Reclamation {
    private int id;
    private String message;
    private LocalDateTime dateCreation;
    private List<Reponse> reponses;

    // Default constructor
    public Reclamation() {
        this.dateCreation = LocalDateTime.now();
        this.reponses = new ArrayList<>();
    }

    // Constructor with message only
    public Reclamation(String message) {
        this();
        this.message = message;
    }

    // Full constructor
    public Reclamation(String message, LocalDateTime dateCreation) {
        this(message); // Calls the message-only constructor
        this.dateCreation = dateCreation;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public List<Reponse> getReponses() { return reponses; }
    public void addReponse(Reponse reponse) {
        reponse.setReclamation(this);
        this.reponses.add(reponse);
    }
}