import java.util.Objects;

public class Department {
    private int id;
    private String nomDep;
    private int nmbrEmploye ;
    public Department(int id, String nomDep, int nmbrEmployee) {
        this.id = id;
        this.nomDep = nomDep;
        this.nmbrEmploye = nmbrEmployee;
    }
    public Department(){}
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNomDep() {
        return nomDep;
    }
    public void setNomDep(String nomDep) {
        this.nomDep = nomDep;
    }
    public int getNmbrEmploye() {
        return nmbrEmploye;
    }
    public void setNmbrEmploye(int nmbrEmployee) {
        this.nmbrEmploye = nmbrEmployee;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return that.id == id && Objects.equals(nomDep,that.nomDep) ;
    }
    @Override
    public String toString() {
        return "Department{"+"id=" +id+",nomDep=" +nomDep+",nmbrEmploye="+nmbrEmploye+'}';
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, nomDep) ;
    }
    public int compareTo(Department o) {
        return id-o.getId() ;
    }
}
