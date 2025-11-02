package graphro;

import java.util.*;

/**
 * Branch & Bound (Best-First) pour TSP sur les adresses (points de livraison).
 * Repose sur le "graphe complet des adresses" fourni par VoyageurDeCommerce.
 */
public class BranchAndBoundTSP {

    private final VoyageurDeCommerce vdcUtils;
    private final List<Sommet> adresses; 
    private final Sommet depart;

    private class Etat {
        final List<Sommet> cheminPartiel; 
        final int coutActuel;
        final int borneInferieure;

        Etat(List<Sommet> cheminPartiel, int coutActuel, int borneInferieure) {
            this.cheminPartiel = cheminPartiel;
            this.coutActuel = coutActuel;
            this.borneInferieure = borneInferieure;
        }
    }

    public BranchAndBoundTSP(VoyageurDeCommerce vdc, Sommet depart) {
        this.vdcUtils = vdc;
        this.depart = depart;
        List<Sommet> tmp = new ArrayList<>(vdcUtils.getPtsLivraison(depart));
        if (!tmp.isEmpty() && !tmp.get(0).equals(depart)) {
            tmp.remove(depart);
            tmp.add(0, depart);
        }
        this.adresses = tmp;
        vdcUtils.creerGrapheCompletDesAdresses(this.adresses); // construit grapheCompletAdresses + cache
    }

    public List<Sommet> algoBranchAndBound() {
        if (adresses.size() < 2) return Collections.emptyList();

        GrapheListe gComplet = vdcUtils.getGrapheCompletAdresses();

        List<Sommet> tourInit = vdcUtils.initialiserTour(adresses, depart);
        int borneSuperieure = vdcUtils.calculCoutTour(tourInit);
        List<Sommet> meilleurTourAdresses = new ArrayList<>(tourInit); 

        PriorityQueue<Etat> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.borneInferieure));

        List<Sommet> initial = new ArrayList<>();
        initial.add(depart);
        int borneInit = calculerBorneInferieure(initial, 0, adresses, depart, gComplet);
        pq.add(new Etat(initial, 0, borneInit));

        List<Sommet> candidats = new ArrayList<>(adresses);
        candidats.remove(depart);

        while (!pq.isEmpty()) {
            Etat etat = pq.poll();

            if (etat.borneInferieure >= borneSuperieure) continue;

            List<Sommet> chemin = etat.cheminPartiel;
            Sommet dernier = chemin.get(chemin.size() - 1);

            if (chemin.size() == adresses.size()) {
                int coutRetour = gComplet.valeurArc(dernier, depart);
                if (coutRetour == -1 || coutRetour == Integer.MAX_VALUE) continue;
                int coutTotal = etat.coutActuel + coutRetour;
                if (coutTotal < borneSuperieure) {
                    borneSuperieure = coutTotal;
                    meilleurTourAdresses = new ArrayList<>(chemin);
                    meilleurTourAdresses.add(depart); 
                }
                continue;
            }

            for (Sommet next : candidats) {
                if (chemin.contains(next)) continue;
                int coutArc = gComplet.valeurArc(dernier, next);
                if (coutArc == -1 || coutArc == Integer.MAX_VALUE) continue; 

                List<Sommet> nouveauChemin = new ArrayList<>(chemin);
                nouveauChemin.add(next);
                int nouveauCout = etat.coutActuel + coutArc;

                int borneInf = calculerBorneInferieure(nouveauChemin, nouveauCout, adresses, depart, gComplet);
                if (borneInf < borneSuperieure) {
                    pq.add(new Etat(nouveauChemin, nouveauCout, borneInf));
                }
            }
        }

        if (meilleurTourAdresses == null || meilleurTourAdresses.isEmpty()) return Collections.emptyList();
        return vdcUtils.reconstituerCheminComplet(meilleurTourAdresses);
    }

    private int calculerBorneInferieure(List<Sommet> cheminPartiel, int coutActuel,
                                        List<Sommet> toutesAdresses, Sommet depart, GrapheListe gComplet) {

        Set<Sommet> nonVisites = new HashSet<>(toutesAdresses);
        nonVisites.removeAll(cheminPartiel);
        Sommet dernier = cheminPartiel.get(cheminPartiel.size() - 1);
        nonVisites.add(dernier);

        int sommeMin2 = 0;

        for (Sommet s : nonVisites) {
            List<Integer> couts = new ArrayList<>();
            for (Sommet dest : toutesAdresses) {
                if (s.equals(dest)) continue;
                int c = gComplet.valeurArc(s, dest);
                if (c != -1 && c != Integer.MAX_VALUE) couts.add(c);
            }
            if (!toutesAdresses.contains(depart)) { 
                int cDepot = gComplet.valeurArc(s, depart);
                if (cDepot != -1 && cDepot != Integer.MAX_VALUE) couts.add(cDepot);
            }

            Collections.sort(couts);
            if (couts.size() >= 2) {
                sommeMin2 += couts.get(0) + couts.get(1);
            } else if (couts.size() == 1) {
                sommeMin2 += couts.get(0);
            } else {
                return Integer.MAX_VALUE;
            }
        }

        int borne = coutActuel + (sommeMin2 + 1) / 2;
        return borne;
    }
}
