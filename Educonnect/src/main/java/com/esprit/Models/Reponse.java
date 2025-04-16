package com.esprit.Models;

import java.time.LocalDateTime;

public class Reponse {
    private int id;
    private String contenu;
    private LocalDateTime dateCreation;
    private Reclamation reclamation;

    // Default constructor
    public Reponse() {
        this.dateCreation = LocalDateTime.now();
    }

    // Constructor without reclamation (for standalone use)
    public Reponse(String contenu, LocalDateTime dateCreation) {
        this();
        this.contenu = contenu;
        this.dateCreation = dateCreation;
    }

    // Full constructor
    public Reponse(String contenu, LocalDateTime dateCreation, Reclamation reclamation) {
        this(contenu, dateCreation);
        this.reclamation = reclamation;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public Reclamation getReclamation() { return reclamation; }
    public void setReclamation(Reclamation reclamation) { this.reclamation = reclamation; }
}