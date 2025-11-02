package graphro;

import java.util.*;

public class Chemin {
    private final int cout;
    private final List<Sommet> chemin;

    public Chemin(int cout, List<Sommet> chemin) {
        this.cout = cout;
        this.chemin = (chemin == null) ? null : new ArrayList<>(chemin);
    }

    public int getCout() {
        return cout;
    }

    public List<Sommet> getChemin() {
        return (chemin == null) ? null : new ArrayList<>(chemin);
    }

    @Override
    public String toString() {
        return "Chemin{cout=" + cout + ", chemin=" + chemin + "}";
    }
}

