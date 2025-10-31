package graphro;

import java.io.*;
import java.util.*;

public class GrapheListe {

    private final Map<Sommet, List<Arc>> adjacence;

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

    public void ajouterArc(Sommet source, Sommet destination, int cout) {
        ajouterSommet(source);
        ajouterSommet(destination);
        adjacence.get(source).add(new Arc(destination, cout));
        adjacence.get(destination).add(new Arc(source, cout));
    }

    public List<Arc> getAdjacents(Sommet s) {
        return adjacence.getOrDefault(s, new ArrayList<>());
    }

    public Collection<Sommet> sommets() {
        return adjacence.keySet();
    }

    public int getCoutArc(Sommet source, Sommet destination) {
        for (Arc arc : adjacence.getOrDefault(source, new ArrayList<>())) {
            if (arc.getDestination().equals(destination)) {
                return arc.getCout();
            }
        }
        return Integer.MAX_VALUE;
    }

    public Chemin plusCourtChemin(Sommet source, Sommet destination) {
        if (source.equals(destination)) {
            return new Chemin(0, Collections.singletonList(source));
        }

        // Deux files pour la recherche bidirectionnelle
        Queue<Sommet> queueAvant = new LinkedList<>();
        Queue<Sommet> queueArriere = new LinkedList<>();

        // Maps pour stocker les distances et prédécesseurs depuis le début
        Map<Sommet, Integer> distAvant = new HashMap<>();
        Map<Sommet, Sommet> predAvant = new HashMap<>();

        // Maps pour stocker les distances et successeurs depuis la fin
        Map<Sommet, Integer> distArriere = new HashMap<>();
        Map<Sommet, Sommet> succArriere = new HashMap<>();

        // Initialisation
        queueAvant.add(source);
        distAvant.put(source, 0);

        queueArriere.add(destination);
        distArriere.put(destination, 0);

        Sommet pointRencontre = null;
        int meilleureDist = Integer.MAX_VALUE;

        // Recherche bidirectionnelle
        while (!queueAvant.isEmpty() || !queueArriere.isEmpty()) {
            // Expansion depuis le début
            if (!queueAvant.isEmpty()) {
                Sommet courant = queueAvant.poll();
                int distCourante = distAvant.get(courant);

                // Si on a déjà une meilleure solution, arrêter cette direction
                if (distCourante >= meilleureDist) {
                    queueAvant.clear();
                } else {
                    for (Arc arc : getAdjacents(courant)) {
                        Sommet voisin = arc.getDestination();

                        if (!distAvant.containsKey(voisin)) {
                            distAvant.put(voisin, distCourante + 1);
                            predAvant.put(voisin, courant);
                            queueAvant.add(voisin);

                            // Vérifier si on rencontre la recherche arrière
                            if (distArriere.containsKey(voisin)) {
                                int distTotale = distAvant.get(voisin) + distArriere.get(voisin);
                                if (distTotale < meilleureDist) {
                                    meilleureDist = distTotale;
                                    pointRencontre = voisin;
                                }
                            }
                        }
                    }
                }
            }

            // Expansion depuis la fin
            if (!queueArriere.isEmpty()) {
                Sommet courant = queueArriere.poll();
                int distCourante = distArriere.get(courant);

                // Si on a déjà une meilleure solution, arrêter cette direction
                if (distCourante >= meilleureDist) {
                    queueArriere.clear();
                } else {
                    for (Arc arc : getAdjacents(courant)) {
                        Sommet voisin = arc.getDestination();

                        if (!distArriere.containsKey(voisin)) {
                            distArriere.put(voisin, distCourante + 1);
                            succArriere.put(voisin, courant);
                            queueArriere.add(voisin);

                            // Vérifier si on rencontre la recherche avant
                            if (distAvant.containsKey(voisin)) {
                                int distTotale = distAvant.get(voisin) + distArriere.get(voisin);
                                if (distTotale < meilleureDist) {
                                    meilleureDist = distTotale;
                                    pointRencontre = voisin;
                                }
                            }
                        }
                    }
                }
            }
        }

        // Pas de chemin trouvé
        if (pointRencontre == null) {
            return new Chemin(Integer.MAX_VALUE, null);
        }

        // Reconstruire le chemin complet
        List<Sommet> chemin = new ArrayList<>();

        // Partie avant (source -> point de rencontre)
        Sommet actuel = pointRencontre;
        List<Sommet> partieAvant = new ArrayList<>();
        while (actuel != null) {
            partieAvant.add(actuel);
            actuel = predAvant.get(actuel);
        }
        Collections.reverse(partieAvant);
        chemin.addAll(partieAvant);

        // Partie arrière (point de rencontre -> destination)
        actuel = succArriere.get(pointRencontre);
        while (actuel != null) {
            chemin.add(actuel);
            actuel = succArriere.get(actuel);
        }

        return new Chemin(meilleureDist, chemin);
    }

    public void lireGraphe(String nomFichier) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(nomFichier));
        String ligne;
        Map<String, Sommet> mapSommets = new HashMap<>();

        while ((ligne = br.readLine()) != null) {
            ligne = ligne.trim();
            if (ligne.isEmpty() || ligne.startsWith("#")) {
                continue; // Ignorer les lignes vides ou les commentaires
            }

            String[] tokens = ligne.split("\\s+");
            if (tokens[0].equals("V")) {
                // Définition d'un sommet
                String nomSommet = tokens[1];
                boolean estIntersection = Boolean.parseBoolean(tokens[2]);
                Sommet sommet = new Sommet(nomSommet, estIntersection);
                this.ajouterSommet(sommet);
                mapSommets.put(nomSommet, sommet);
            } else if (tokens[0].equals("E")) {
                // Définition d'une arête
                String sourceNom = tokens[1];
                String destinationNom = tokens[2];
                int cout = Integer.parseInt(tokens[3]);

                Sommet source = mapSommets.get(sourceNom);
                Sommet destination = mapSommets.get(destinationNom);

                if (source == null || destination == null) {
                    throw new IllegalArgumentException("Sommet non défini pour l'arête : " + ligne);
                }

                this.ajouterArc(source, destination, cout);
            } else {
                throw new IllegalArgumentException("Ligne invalide dans le fichier : " + ligne);
            }
        }

        br.close();
    }
}
