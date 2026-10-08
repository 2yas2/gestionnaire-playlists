package playlist;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FenetrePlaylist extends JFrame {

    private Playlist playlist = new Playlist("playlist");
    private DefaultListModel<Musique> modele = new DefaultListModel<>();
    private JList<Musique> liste = new JList<>(modele);
    private JComboBox<String> types = new JComboBox<>(new String[] {"m3u", "xspf", "jspf"});
    private JLabel etat = new JLabel("aucune musique");

    public FenetrePlaylist() {
        super("Gestionnaire de playlists");

        JPanel haut = new JPanel(new FlowLayout());
        JButton importer = new JButton("Importer...");
        JButton courant = new JButton("Repertoire courant");
        JButton exporter = new JButton("Exporter...");
        haut.add(importer);
        haut.add(courant);
        haut.add(new JLabel("Type :"));
        haut.add(types);
        haut.add(exporter);

        JPanel bas = new JPanel(new GridLayout(2, 1));
        bas.add(etat);

        add(haut, BorderLayout.NORTH);
        add(new JScrollPane(liste), BorderLayout.CENTER);
        add(bas, BorderLayout.SOUTH);

        importer.addActionListener(e -> importer());
        courant.addActionListener(e -> ajouterRepertoireCourant());
        exporter.addActionListener(e -> exporter());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
    }

    private void ajouter(Musique m) {
        for (Musique existante : playlist.getMusiques()) {
            if (existante.getFichier().equals(m.getFichier())) {
                return;
            }
        }
        playlist.ajouter(m);
        modele.addElement(m);
    }

    private void importer() {
        JFileChooser choix = new JFileChooser();
        choix.setMultiSelectionEnabled(true);
        choix.setFileFilter(new FileNameExtensionFilter("musiques mp3", "mp3"));
        if (choix.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            for (File f : choix.getSelectedFiles()) {
                ajouter(new Musique(f));
            }
            etat.setText(playlist.taille() + " musique(s)");
        }
    }

    private void ajouterRepertoireCourant() {
        File dossier = new File(System.getProperty("user.dir"));
        List<Musique> trouvees = Selection.trouverMusiques(dossier);
        for (Musique m : trouvees) {
            ajouter(m);
        }
        etat.setText(trouvees.size() + " musique(s) trouvee(s) dans " + dossier.getName());
    }

    private void exporter() {
        if (playlist.taille() == 0) {
            etat.setText("rien a exporter");
            return;
        }
        String type = (String) types.getSelectedItem();
        Exporteur exporteur = Exporteurs.pour(type);
        JFileChooser choix = new JFileChooser();
        choix.setSelectedFile(new File("playlist." + exporteur.extension()));
        if (choix.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                exporteur.exporter(playlist, choix.getSelectedFile());
                etat.setText("playlist exportee : " + choix.getSelectedFile().getName());
            } catch (IOException e) {
                etat.setText("erreur : " + e.getMessage());
            }
        }
    }
}
