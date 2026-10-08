package playlist;

import java.util.ArrayList;
import java.util.List;

public class Playlist {

    private String nom;
    private List<Musique> musiques = new ArrayList<>();

    public Playlist(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }

    public List<Musique> getMusiques() {
        return musiques;
    }

    public void ajouter(Musique m) {
        musiques.add(m);
    }

    public int taille() {
        return musiques.size();
    }
}
