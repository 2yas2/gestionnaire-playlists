package playlist;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        if (args.length > 0) {
            Cli.lancer(args);
        } else {
            SwingUtilities.invokeLater(() -> new FenetrePlaylist().setVisible(true));
        }
    }
}
