import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.*;
public class AffectationHashMap {
    Map<Employe,Department> employeDep = new HashMap<>();

    void ajouterEmployeDepartement(Employe e, Department d){
        employeDep.put(e,d);
    }
    void afficherEmployesEtDepartements(){
        for(Map.Entry<Employe,Department> e : employeDep.entrySet()){
            System.out.println(e.getKey().getNom() + " " + e.getKey().getPrenom() + " is in the " + e.getValue().getNmbrEmploye()+ " Departement");
        }
    }

    void supprimerEmploye(Employe e){
        employeDep.remove(e);
    }
    void supprimerEmployeEtDepartement(Employe e, Department d){
        Department dep = employeDep.get(e);
        if(d.equals(dep)){
            supprimerEmploye(e);
        }
    }
    void afficherEmployes(){
        for(Employe e : employeDep.keySet()){
            System.out.println(e);
        }

        Set<Employe> keys = employeDep.keySet();
        for (Employe key : keys) {
            System.out.println(key);
        }
    }
    void afficherDepartements(){
        for(Department d : employeDep.values()){
            System.out.println(d);
        }
        Collection<Department> values = employeDep.values();
        for (Department value : values) {
            System.out.println(value);
        }
    }
    boolean rechercherEmploye (Employe e){
        return employeDep.containsKey(e);
    }
    boolean rechercherDepartement (Department d){
        return employeDep.containsValue(d);
    }
    TreeMap<Employe, Department> trierMap(){
        Comparator<Employe> c = new Comparator<Employe>() {
            @Override
            public int compare(Employe o1, Employe o2) {
                return o1.getId()- o2.getId();
            }
        };
        TreeMap<Employe, Department> tree = new TreeMap<>(c);
        tree.putAll(employeDep);
        return tree;

    }

}
