package playlist;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Selection {

    public static final String FICHIER = "selection.txt";

    // prend toutes les musiques mp3 d'un repertoire
    public static List<Musique> trouverMusiques(File dossier) {
        List<Musique> liste = new ArrayList<>();
        File[] fichiers = dossier.listFiles();
        if (fichiers == null) {
            return liste;
        }
        Arrays.sort(fichiers);
        for (File f : fichiers) {
            if (f.isFile() && f.getName().toLowerCase().endsWith(".mp3")) {
                liste.add(new Musique(f));
            }
        }
        return liste;
    }

    public static void sauvegarder(List<Musique> musiques, File sortie) throws IOException {
        List<String> lignes = new ArrayList<>();
        for (Musique m : musiques) {
            lignes.add(m.getFichier().getAbsolutePath());
        }
        Files.write(sortie.toPath(), lignes);
    }

    public static List<Musique> charger(File entree) throws IOException {
        List<Musique> liste = new ArrayList<>();
        for (String ligne : Files.readAllLines(entree.toPath())) {
            File f = new File(ligne);
            if (f.isFile()) {
                liste.add(new Musique(f));
            }
        }
        return liste;
    }
}
