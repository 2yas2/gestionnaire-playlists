package playlist;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Cli {

    public static void lancer(String[] args) {
        String commande = args[0];
        if (commande.equals("-help")) {
            aide();
        } else if (commande.equals("-h")) {
            if (args.length < 2) {
                System.out.println("il faut donner un fichier : -h <fichier>");
                return;
            }
            metadonnees(args[1]);
        } else if (commande.equals("-u")) {
            selectionner();
        } else if (commande.equals("-o")) {
            if (args.length < 2) {
                System.out.println("il faut donner un type : -o <m3u|xspf|jspf>");
                return;
            }
            creer(args[1]);
        } else {
            System.out.println("commande inconnue : " + commande);
            aide();
        }
    }

    private static void aide() {
        System.out.println("utilisation :");
        System.out.println("  sans argument    ouvre l'interface graphique");
        System.out.println("  -h <fichier>     affiche les metadonnees d'une musique");
        System.out.println("  -u               selectionne toutes les musiques du repertoire courant");
        System.out.println("  -o <type>        cree la playlist de la selection (m3u, xspf ou jspf)");
        System.out.println("  -help            affiche cette aide");
    }

    private static void metadonnees(String chemin) {
        File f = new File(chemin);
        if (!f.isFile()) {
            System.out.println("fichier introuvable : " + chemin);
            return;
        }
        System.out.println(new Musique(f).infos());
    }

    private static void selectionner() {
        File dossier = new File(System.getProperty("user.dir"));
        List<Musique> musiques = Selection.trouverMusiques(dossier);
        try {
            Selection.sauvegarder(musiques, new File(Selection.FICHIER));
        } catch (IOException e) {
            System.out.println("erreur : " + e.getMessage());
            return;
        }
        System.out.println(musiques.size() + " musique(s) selectionnee(s) dans " + dossier);
    }

    private static void creer(String type) {
        Exporteur exporteur = Exporteurs.pour(type);
        if (exporteur == null) {
            System.out.println("type inconnu : " + type + " (m3u, xspf ou jspf)");
            return;
        }
        File fichierSelection = new File(Selection.FICHIER);
        if (!fichierSelection.isFile()) {
            System.out.println("pas de selection, lancer d'abord -u");
            return;
        }
        try {
            List<Musique> selection = Selection.charger(fichierSelection);
            Playlist playlist = new Playlist("playlist");
            int total = selection.size();
            System.out.println("chargement de " + total + " musique(s)...");
            for (int i = 0; i < total; i++) {
                Musique m = selection.get(i);
                playlist.ajouter(m);
                System.out.println("[" + (i + 1) + "/" + total + "] ajoutee : " + m.getTitre()
                        + " | " + (m.getArtiste().isEmpty() ? "inconnu" : m.getArtiste())
                        + " | " + (m.getAlbum().isEmpty() ? "inconnu" : m.getAlbum())
                        + " | " + m.getTaille() + " octets");
            }
            File sortie = new File("playlist." + exporteur.extension());
            exporteur.exporter(playlist, sortie);
            System.out.println("playlist creee : " + sortie.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("erreur : " + e.getMessage());
        }
    }
}
