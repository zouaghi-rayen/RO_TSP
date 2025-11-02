package graphro;

import java.util.*;

public class VoyageurDeCommerce {

    private final GrapheListe graphe;
    private Map<String, Chemin> cachePlusCourtsChemins;
    private GrapheListe grapheCompletAdresses;

    public VoyageurDeCommerce(GrapheListe graphe) {
        this.graphe = graphe;
        this.cachePlusCourtsChemins = new HashMap<>();
    }

    public GrapheListe getGrapheCompletAdresses() {
        return this.grapheCompletAdresses;
    }
    
    public void creerGrapheCompletDesAdresses(List<Sommet> adresses) {
        grapheCompletAdresses = new GrapheListe(adresses.size());
        cachePlusCourtsChemins = new HashMap<>();
        
        for (Sommet adresse : adresses) {
            grapheCompletAdresses.ajouterSommet(adresse);
        }
        
        for (Sommet adresse1 : adresses) {
            for (Sommet adresse2 : adresses) {
                if (!adresse1.equals(adresse2)) {
                    String cle = adresse1.nom + "-" + adresse2.nom;
                    Chemin chemin;
                    if (cachePlusCourtsChemins.containsKey(cle)) {
                        chemin = cachePlusCourtsChemins.get(cle);
                    } else {
                        chemin = calculerPlusCourtChemin(adresse1, adresse2);
                        cachePlusCourtsChemins.put(cle, chemin);
                    }
                    if (chemin != null && chemin.getCout() != Integer.MAX_VALUE && chemin.getChemin() != null && !chemin.getChemin().isEmpty()) {
			    grapheCompletAdresses.ajouterArc(adresse1, adresse2, chemin.getCout());
		    } else {
			    System.out.println("Aucun chemin entre " + adresse1.nom + " et " + adresse2.nom);
}

                }
            }
        }
    }

    /**
     * Calcule le plus court chemin entre deux sommets.
     */
    private Chemin calculerPlusCourtChemin(Sommet source, Sommet destination) {        
        //BFS pour graphes non pondérés
        if (source.equals(destination)) {
            return new Chemin(0, Collections.singletonList(source));
        }
        
        Queue<Sommet> queue = new LinkedList<>();
        Map<Sommet, Sommet> predecesseurs = new HashMap<>();
        Map<Sommet, Integer> distances = new HashMap<>();
        
        queue.add(source);
        distances.put(source, 0);
        predecesseurs.put(source, null);
        
        while (!queue.isEmpty()) {
            Sommet courant = queue.poll();
            int distCourante = distances.get(courant);
            
            if (courant.equals(destination)) {
                List<Sommet> chemin = new ArrayList<>();
                Sommet s = destination;
                while (s != null) {
                    chemin.add(0, s);
                    s = predecesseurs.get(s);
                }
                return new Chemin(distCourante, chemin);
            }
            
            LinkedList<Arc> voisins = graphe.voisins(courant);
            for (Arc arc : voisins) {
                Sommet voisin = arc.destination();
                if (!distances.containsKey(voisin)) {
                    distances.put(voisin, distCourante + arc.valeur());
                    predecesseurs.put(voisin, courant);
                    queue.add(voisin);
                }
            }
        }
        
        // Pas de chemin trouvé
        return new Chemin(Integer.MAX_VALUE, null);
    }

    public List<Sommet> algoOptim2opt(Sommet depart) {
        List<Sommet> adresses = getPtsLivraison(depart);

        creerGrapheCompletDesAdresses(adresses);

        List<Sommet> tour = initialiserTour(adresses, depart);

        // Appliquer l'algorithme 2-opt sur le graphe complet des adresses
        boolean amelioration;
        int meilleurCout = calculCoutTour(tour);
        do {
            amelioration = false;
            int tailleTour = tour.size();

            for (int i = 1; i < tailleTour - 2; i++) {
                for (int j = i + 1; j < tailleTour - 1; j++) {
                    List<Sommet> nouveauTour = echangeDeuxOpt(tour, i, j);
                    int nouveauCout = calculCoutTour(nouveauTour);

                    if (nouveauCout < meilleurCout) {
                        tour = nouveauTour;
                        meilleurCout = nouveauCout;
                        amelioration = true;
                    }
                }
            }
        } while (amelioration);
        return this.reconstituerCheminComplet(tour);
    }

    public List<Sommet> echangeDeuxOpt(List<Sommet> tour, int i, int j) 
    {
        List<Sommet> nouveauTour = new ArrayList<>(tour.subList(0, i));
        List<Sommet> sousListe = new ArrayList<>(tour.subList(i, j + 1));
        Collections.reverse(sousListe);
        nouveauTour.addAll(sousListe);
        nouveauTour.addAll(tour.subList(j + 1, tour.size()));
        return nouveauTour;
    }

    public int calculCoutTour(List<Sommet> tour) {
        int coutTotal = 0;
        for (int i = 0; i < tour.size() - 1; i++) {
            Sommet s1 = tour.get(i);
            Sommet s2 = tour.get(i + 1);
            int cout = grapheCompletAdresses.valeurArc(s1, s2);
            if (cout == -1 || cout == Integer.MAX_VALUE) {
                return Integer.MAX_VALUE; // Pas de chemin entre s1 et s2
            }
            coutTotal += cout;
        }
        return coutTotal;
    }

    public List<Sommet> reconstituerCheminComplet(List<Sommet> tour) {
        List<Sommet> cheminComplet = new ArrayList<>();

        for (int i = 0; i < tour.size() - 1; i++) {
            Sommet s1 = tour.get(i);
            Sommet s2 = tour.get(i + 1);
            Chemin cheminResultat = obtenirCheminResultat(s1, s2);
            List<Sommet> chemin = cheminResultat.getChemin();

            if (chemin == null) {
                throw new IllegalStateException("Aucun chemin trouvé entre " + s1.nom + " et " + s2.nom);
            }

            for (Sommet s : chemin) {
                if (!cheminComplet.isEmpty() && s.equals(cheminComplet.get(cheminComplet.size() - 1))) {
                    continue;
                }
                cheminComplet.add(s);
            }
        }
        return cheminComplet;
    }

    public Chemin obtenirCheminResultat(Sommet s1, Sommet s2) {
        String cle1 = s1.nom + "-" + s2.nom;

        if (cachePlusCourtsChemins.containsKey(cle1)) {
            return cachePlusCourtsChemins.get(cle1);
        } else {
            Chemin chemin = calculerPlusCourtChemin(s1, s2);
            cachePlusCourtsChemins.put(cle1, chemin);
            return chemin;
        }
    }

    public List<Sommet> getPtsLivraison(Sommet depart) {
        List<Sommet> ptsLivraison = new ArrayList<>();
        
        for (Sommet s : graphe.sommets()) {
            if (s.valeurMarque() == 0) {  
                ptsLivraison.add(s);
            }
        }
        
        if (!ptsLivraison.contains(depart)) {
            ptsLivraison.add(0, depart);
        } else {
            ptsLivraison.remove(depart);
            ptsLivraison.add(0, depart);
        }
        return ptsLivraison;
    }

    public List<Sommet> initialiserTour(List<Sommet> ptsLivraison, Sommet depart) {
        List<Sommet> tour = new ArrayList<>(ptsLivraison);
        tour.add(depart);
        return tour;
    }
}
