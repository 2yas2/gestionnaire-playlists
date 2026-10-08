package playlist;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ExportM3U implements Exporteur {

    @Override
    public void exporter(Playlist playlist, File sortie) throws IOException {
        List<String> lignes = new ArrayList<>();
        lignes.add("#EXTM3U");
        for (Musique m : playlist.getMusiques()) {
            lignes.add("#EXTINF:-1," + m);
            lignes.add(m.getFichier().getAbsolutePath());
        }
        Files.write(sortie.toPath(), lignes, StandardCharsets.UTF_8);
    }

    @Override
    public String extension() {
        return "m3u";
    }
}
