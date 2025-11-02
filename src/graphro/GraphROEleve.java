package graphro;

import java.io.IOException;
import java.util.List;

public class GraphROEleve {

    public static int calculerCoutCheminComplet(GrapheListe graphe, List<Sommet> cheminComplet) {
        int coutTotal = 0;
        for (int i = 0; i < cheminComplet.size() - 1; i++) {
            int cout = graphe.getCoutArc(cheminComplet.get(i), cheminComplet.get(i + 1));
            if (cout == Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            }
            coutTotal += cout;
        }
        return coutTotal;
    }

    public static void testerGraphe(String cheminFichier) {
        try {
            System.out.println("\n========================================================");
            System.out.println("=== TEST DU GRAPHE : " + cheminFichier + " ===");
            System.out.println("========================================================");

            GrapheListe graphe = new GrapheListe(cheminFichier);

            Sommet depart = null;
            for (Sommet s : graphe.sommets()) {
                if (s.nom.equals("Depot")) {
                    depart = s;
                    break;
                }
            }
            if (depart == null) {
                throw new IllegalArgumentException("Le sommet de départ 'Depot' n'a pas été trouvé dans le graphe.");
            }

            VoyageurDeCommerce vdcUtils = new VoyageurDeCommerce(graphe);
            BranchAndBoundTSP tspSolverBB = new BranchAndBoundTSP(vdcUtils, depart);

            System.out.println("--- Exécution de l'algorithme Branch and Bound (Solution exacte) ---");

            List<Sommet> cheminOptimalBB = tspSolverBB.algoBranchAndBound();

            int coutTotalBB = calculerCoutCheminComplet(graphe, cheminOptimalBB);

            System.out.printf("Chemin optimal BB (%d sommets) - Coût total : %d\n", cheminOptimalBB.size(), coutTotalBB);
            for (Sommet s : cheminOptimalBB) {
                System.out.printf("=> %s\n", s.nom);
            }

            System.out.println("\n------------------------------------------------------------------");

            System.out.println("--- Exécution de l'algorithme 2-Opt (Heuristique) ---");
            List<Sommet> cheminOptimal2opt = vdcUtils.algoOptim2opt(depart);

            int coutTotal2opt = calculerCoutCheminComplet(graphe, cheminOptimal2opt);

            System.out.printf("Chemin optimal 2-opt (%d sommets) - Coût total : %d\n", cheminOptimal2opt.size(), coutTotal2opt);
            for (Sommet s : cheminOptimal2opt) {
                System.out.printf("=> %s\n", s.nom);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            System.err.println("⚠️ Erreur : " + e.getMessage());
        }
    }

    public static void main(String[] args) 
    {
        testerGraphe("data/graphe.txt");

        testerGraphe("data/graphe_sans_metro.txt");
    }
}

