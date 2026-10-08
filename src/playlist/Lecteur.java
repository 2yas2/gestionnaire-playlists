package playlist;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class Lecteur {

    private Player player;

    public void lire(File fichier) throws IOException, JavaLayerException {
        stop();
        Player courant = new Player(new BufferedInputStream(new FileInputStream(fichier)));
        player = courant;
        Thread thread = new Thread(() -> {
            try {
                courant.play();
            } catch (JavaLayerException e) {
                System.out.println("erreur de lecture : " + e.getMessage());
            }
        });
        thread.start();
    }

    public void stop() {
        if (player != null) {
            player.close();
            player = null;
        }
    }
}
