package graphro;

public class Sommet {
    String nom;
    boolean intersection;

    public Sommet(String nom, boolean intersection) {
        this.nom = nom;
        this.intersection = intersection;
    }

    public boolean estIntersection() {
        return intersection;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || !(obj instanceof Sommet))
            return false;
        Sommet other = (Sommet) obj;
        return nom.equals(other.nom);
    }

    public String getNom(){
        return this.nom;
    }


    @Override
    public int hashCode() {
        return nom.hashCode();
    }
}
