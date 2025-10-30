package graphro;

import java.io.IOException;

/**
 *
 * @author gdelmondo
 */
public class GraphROEleve {

    private final GrapheListe graphe;

    public GraphROEleve(String nomFichier) throws IOException {
        this.graphe = new GrapheListe(nomFichier);
    }

    public void afficherGraphe() {
        System.out.println(this.graphe);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            GraphROEleve app = new GraphROEleve("data/graphe.txt");
            app.afficherGraphe();

        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier: " + e.getMessage());
            e.printStackTrace();
        }
    }
}