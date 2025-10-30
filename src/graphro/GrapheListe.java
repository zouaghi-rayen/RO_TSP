package graphro;

import java.io.*;
import java.util.*;

public class GrapheListe {

    private final Map<Sommet, List<Sommet>> adjacence;

    public GrapheListe() {
        adjacence = new HashMap<>();
    }

    public GrapheListe(String nomFichier) throws IOException {
        adjacence = new HashMap<>();
        lireGraphe(nomFichier);
    }

    public void ajouterSommet(Sommet s) {
        if (!adjacence.containsKey(s)) {
            adjacence.put(s, new ArrayList<>());
        }
    }

    public void ajouterArc(Sommet source, Sommet destination) {
        ajouterSommet(source);
        ajouterSommet(destination);
        adjacence.get(source).add(destination);
        adjacence.get(destination).add(source);
    }

    public List<Sommet> getAdjacents(Sommet s) {
        return adjacence.getOrDefault(s, new ArrayList<>());
    }

    public Collection<Sommet> sommets() {
        return adjacence.keySet();
    }

    /**
     * BFS pour trouver le plus court chemin
     * Avec toutes les arêtes de coût 1, BFS trouve automatiquement
     * le chemin avec le coût minimum (qui est égal au nombre d'arêtes)
     */
    public Chemin plusCourtChemin(Sommet source, Sommet destination) {
        // Queue pour BFS
        Queue<Sommet> queue = new LinkedList<>();

        // Map pour suivre les prédécesseurs
        Map<Sommet, Sommet> predecesseurs = new HashMap<>();

        // Set des sommets visités
        Set<Sommet> visites = new HashSet<>();

        // Initialisation
        queue.add(source);
        visites.add(source);
        predecesseurs.put(source, null);

        // BFS
        while (!queue.isEmpty()) {
            Sommet courant = queue.poll();

            // Si on a trouvé la destination
            if (courant.equals(destination)) {
                // Reconstruire le chemin
                List<Sommet> chemin = new ArrayList<>();
                Sommet actuel = destination;
                int coutTotal = 0;

                while (actuel != null) {
                    chemin.add(actuel);
                    if (predecesseurs.get(actuel) != null) {
                        coutTotal++; // Chaque arête coûte 1
                    }
                    actuel = predecesseurs.get(actuel);
                }

                Collections.reverse(chemin);
                return new Chemin(coutTotal, chemin);
            }

            // Explorer tous les voisins
            for (Sommet voisin : getAdjacents(courant)) {
                if (!visites.contains(voisin)) {
                    visites.add(voisin);
                    predecesseurs.put(voisin, courant);
                    queue.add(voisin);
                }
            }
        }

        // Aucun chemin trouvé
        return new Chemin(Integer.MAX_VALUE, null);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== GRAPHE ===\n");
        sb.append("Nombre de sommets: ").append(adjacence.size()).append("\n\n");

        // Calculer le nombre total d'arêtes (divisé par 2 car non-orienté)
        int totalAretes = 0;
        for (List<Sommet> voisins : adjacence.values()) {
            totalAretes += voisins.size();
        }
        sb.append("Nombre d'arêtes: ").append(totalAretes / 2).append("\n\n");

        sb.append("Liste d'adjacence:\n");
        sb.append("-".repeat(50)).append("\n");

        // Trier les sommets par nom pour un affichage ordonné
        List<Sommet> sommetsOrdonnes = new ArrayList<>(adjacence.keySet());
        sommetsOrdonnes.sort(Comparator.comparing(Sommet::getNom));

        for (Sommet sommet : sommetsOrdonnes) {
            sb.append(sommet.getNom());
            if (sommet.estIntersection()) {
                sb.append(" [INTERSECTION]");
            }
            sb.append(" -> ");

            List<Sommet> voisins = adjacence.get(sommet);
            if (voisins.isEmpty()) {
                sb.append("(aucun voisin)");
            } else {
                for (int i = 0; i < voisins.size(); i++) {
                    sb.append(voisins.get(i).getNom());
                    if (i < voisins.size() - 1) {
                        sb.append(", ");
                    }
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    public void lireGraphe(String nomFichier) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(nomFichier));
        String ligne;
        Map<String, Sommet> mapSommets = new HashMap<>();

        while ((ligne = br.readLine()) != null) {
            ligne = ligne.trim();
            if (ligne.isEmpty() || ligne.startsWith("#")) {
                continue;
            }

            String[] tokens = ligne.split("\\s+");
            if (tokens[0].equals("V")) {
                String nomSommet = tokens[1];
                boolean estIntersection = Boolean.parseBoolean(tokens[2]);
                Sommet sommet = new Sommet(nomSommet, estIntersection);
                this.ajouterSommet(sommet);
                mapSommets.put(nomSommet, sommet);
            } else if (tokens[0].equals("E")) {
                String sourceNom = tokens[1];
                String destinationNom = tokens[2];

                Sommet source = mapSommets.get(sourceNom);
                Sommet destination = mapSommets.get(destinationNom);

                if (source == null || destination == null) {
                    throw new IllegalArgumentException("Sommet non défini pour l'arête : " + ligne);
                }

                this.ajouterArc(source, destination);
            } else {
                throw new IllegalArgumentException("Ligne invalide dans le fichier : " + ligne);
            }
        }

        br.close();
    }
}