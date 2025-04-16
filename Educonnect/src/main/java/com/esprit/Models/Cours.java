package com.esprit.Models;

import java.io.File;

public class Cours {
    private int id;
    private String titre;
    private String description;
    private String contenu;
    private Categorie categorie;
    private String imagePath;
    private transient File imageFile; // transient to avoid serialization issues

    public Cours() {}

    public Cours(String titre, String description, String contenu, Categorie categorie) {
        this.titre = titre;
        this.description = description;
        this.contenu = contenu;
        this.categorie = categorie;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) { this.categorie = categorie; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public File getImageFile() { return imageFile; }
    public void setImageFile(File imageFile) { this.imageFile = imageFile; }

    @Override
    public String toString() {
        return titre;
    }
}