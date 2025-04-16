package com.esprit.Models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Categorie {
    private int id;
    private final StringProperty nomCategorie = new SimpleStringProperty();

    public Categorie() {}

    public Categorie(String nomCategorie) {
        this.nomCategorie.set(nomCategorie);
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNomCategorie() { return nomCategorie.get(); }
    public void setNomCategorie(String nomCategorie) { this.nomCategorie.set(nomCategorie); }

    public StringProperty nomCategorieProperty() { return nomCategorie; }

    @Override
    public String toString() {
        return getNomCategorie();
    }
}