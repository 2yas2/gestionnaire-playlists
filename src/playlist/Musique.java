package playlist;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

public class Musique {

    private File fichier;
    private String titre = "";
    private String artiste = "";
    private String album = "";

    public Musique(File fichier) {
        this.fichier = fichier;
        lireTags();
        if (titre.isEmpty()) {
            titre = nomSansExtension();
        }
    }

    // lit le tag id3v1 qui est dans les 128 derniers octets du mp3
    private void lireTags() {
        try (RandomAccessFile f = new RandomAccessFile(fichier, "r")) {
            if (f.length() < 128) {
                return;
            }
            f.seek(f.length() - 128);
            byte[] tag = new byte[128];
            f.readFully(tag);
            String debut = new String(tag, 0, 3, StandardCharsets.ISO_8859_1);
            if (!debut.equals("TAG")) {
                return;
            }
            titre = texte(tag, 3, 30);
            artiste = texte(tag, 33, 30);
            album = texte(tag, 63, 30);
        } catch (IOException e) {
            System.out.println("impossible de lire " + fichier.getName());
        }
    }

    private String texte(byte[] tag, int debut, int longueur) {
        String s = new String(tag, debut, longueur, StandardCharsets.ISO_8859_1);
        return s.replace("\0", "").trim();
    }

    private String nomSansExtension() {
        String nom = fichier.getName();
        int point = nom.lastIndexOf('.');
        if (point > 0) {
            return nom.substring(0, point);
        }
        return nom;
    }

    public File getFichier() {
        return fichier;
    }

    public String getTitre() {
        return titre;
    }

    public String getArtiste() {
        return artiste;
    }

    public String getAlbum() {
        return album;
    }

    public long getTaille() {
        return fichier.length();
    }

    public String getFormat() {
        String nom = fichier.getName();
        int point = nom.lastIndexOf('.');
        if (point < 0) {
            return "";
        }
        return nom.substring(point + 1).toLowerCase();
    }

    public String infos() {
        String s = "fichier : " + fichier.getName() + "\n";
        s += "chemin : " + fichier.getAbsolutePath() + "\n";
        s += "format : " + getFormat() + "\n";
        s += "taille : " + getTaille() + " octets\n";
        s += "titre : " + titre + "\n";
        s += "artiste : " + (artiste.isEmpty() ? "inconnu" : artiste) + "\n";
        s += "album : " + (album.isEmpty() ? "inconnu" : album);
        return s;
    }

    @Override
    public String toString() {
        if (artiste.isEmpty()) {
            return titre;
        }
        return artiste + " - " + titre;
    }
}
