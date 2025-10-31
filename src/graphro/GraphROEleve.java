/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphro;

import java.io.IOException;
import java.util.List;

/**
 *
 *
 */
public class GraphROEleve {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // Lecture du graphe depuis le fichier
            GrapheListe graphe = new GrapheListe("data/graphe.txt");

            // Récupération du sommet de départ
            Sommet depart = null;
            for (Sommet s : graphe.sommets()) {
                if (s.nom.equals("Depot")) {
                    depart = s;
                    break;
                }
            }
            if (depart == null) {
                throw new IllegalArgumentException("Le sommet de départ 'A' n'a pas été trouvé dans le graphe.");
            }

            // Création de l'instance du voyageur de commerce
            VoyageurDeCommerce vdc = new VoyageurDeCommerce(graphe);
            List<Sommet> cheminOptimal = vdc.algoOptim2opt(depart);
            System.out.printf("Chemin optimal (%d sommets) :\n", cheminOptimal.size());
            for (Sommet s : cheminOptimal) {
                System.out.printf("=> %s\n", s.nom);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
