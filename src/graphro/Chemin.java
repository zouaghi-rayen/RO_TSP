package graphro;

import java.util.List;

public class Chemin {
    private int cout_chemin;
    private List<Sommet> chemin;

    public Chemin(int cout, List<Sommet> chemin) {
        this.cout_chemin = cout;
        this.chemin = chemin;
    }

    public int getCout() {
        return this.cout_chemin;
    }

    public List<Sommet> getChemin() { return this.chemin; }
}
