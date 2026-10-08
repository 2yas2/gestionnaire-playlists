package playlist;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
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

    private void lireTags() {
        try (RandomAccessFile f = new RandomAccessFile(fichier, "r")) {
            if (!lireId3v2(f)) {
                lireId3v1(f);
            }
        } catch (IOException e) {
            System.out.println("impossible de lire " + fichier.getName());
        }
    }

    // tag id3v2 (versions 2.3 et 2.4) au debut du fichier
    private boolean lireId3v2(RandomAccessFile f) throws IOException {
        if (f.length() < 10) {
            return false;
        }
        byte[] entete = new byte[10];
        f.seek(0);
        f.readFully(entete);
        if (entete[0] != 'I' || entete[1] != 'D' || entete[2] != '3') {
            return false;
        }
        int version = entete[3];
        if (version != 3 && version != 4) {
            return false;
        }
        int taille = tailleSynchsafe(entete, 6);
        if (taille > f.length() - 10) {
            return false;
        }
        byte[] tag = new byte[taille];
        f.readFully(tag);

        int pos = 0;
        if ((entete[5] & 0x40) != 0) {
            pos = (version == 4) ? tailleSynchsafe(tag, 0) : 4 + entier(tag, 0);
        }
        while (pos + 10 <= taille && tag[pos] != 0) {
            String id = new String(tag, pos, 4, StandardCharsets.ISO_8859_1);
            int tailleFrame = (version == 4) ? tailleSynchsafe(tag, pos + 4) : entier(tag, pos + 4);
            int debut = pos + 10;
            if (tailleFrame <= 1 || debut + tailleFrame > taille) {
                break;
            }
            if (id.equals("TIT2")) {
                titre = texteFrame(tag, debut, tailleFrame);
            } else if (id.equals("TPE1")) {
                artiste = texteFrame(tag, debut, tailleFrame);
            } else if (id.equals("TALB")) {
                album = texteFrame(tag, debut, tailleFrame);
            }
            pos = debut + tailleFrame;
        }
        return !titre.isEmpty() || !artiste.isEmpty() || !album.isEmpty();
    }

    private int tailleSynchsafe(byte[] b, int debut) {
        return (b[debut] << 21) | (b[debut + 1] << 14) | (b[debut + 2] << 7) | b[debut + 3];
    }

    private int entier(byte[] b, int debut) {
        return ((b[debut] & 0xFF) << 24) | ((b[debut + 1] & 0xFF) << 16)
                | ((b[debut + 2] & 0xFF) << 8) | (b[debut + 3] & 0xFF);
    }

    // le premier octet d'une frame de texte donne l'encodage
    private String texteFrame(byte[] tag, int debut, int longueur) {
        int encodage = tag[debut];
        Charset charset = StandardCharsets.ISO_8859_1;
        if (encodage == 1) {
            charset = StandardCharsets.UTF_16;
        } else if (encodage == 2) {
            charset = StandardCharsets.UTF_16BE;
        } else if (encodage == 3) {
            charset = StandardCharsets.UTF_8;
        }
        String s = new String(tag, debut + 1, longueur - 1, charset);
        return s.replace("\0", "").trim();
    }

    // tag id3v1 dans les 128 derniers octets (titre limite a 30 caracteres)
    private void lireId3v1(RandomAccessFile f) throws IOException {
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
