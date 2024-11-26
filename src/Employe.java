import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
public class Employe implements Comparable<Employe> {
    private int id;
    private String nom;
    private String prenom;
    private String Nomdep;
    private int garde;
    public Employe(int id, String nom, String prenom, String Nomdep, int garde) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.Nomdep = Nomdep;
        this.garde = garde;

    }
    public int getId() {
        return id;

    }
    public String getNom() {
        return nom;

    }
    public String getPrenom() {
        return prenom;

    }
    public String getNomdep() {
        return Nomdep;

    }
    public int getGarde() {
        return garde;
    }

    public void setGarde(int garde) {
        this.garde = garde;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    public void setNomdep(String Nomdep) {
        this.Nomdep = Nomdep;
    }


    @Override
    public String toString() {
        return "Employe [id=" + id + ", nom=" + nom + "]";
    }
    @Override
    public int compareTo(Employe o) {
        return this.nom.compareTo(o.getNom());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Employe employe = (Employe) obj;
        return id == employe.id;
    }

    @Override
    public int hashCode() {
        return Integer .hashCode(id);
    }
    
}
