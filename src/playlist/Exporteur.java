package playlist;

import java.io.File;
import java.io.IOException;

public interface Exporteur {

    void exporter(Playlist playlist, File sortie) throws IOException;

    String extension();
}
