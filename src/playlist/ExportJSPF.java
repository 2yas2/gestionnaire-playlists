package playlist;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ExportJSPF implements Exporteur {

    @Override
    public void exporter(Playlist playlist, File sortie) throws IOException {
        List<String> lignes = new ArrayList<>();
        lignes.add("{");
        lignes.add("  \"playlist\": {");
        lignes.add("    \"title\": \"" + echapper(playlist.getNom()) + "\",");
        lignes.add("    \"track\": [");
        List<Musique> musiques = playlist.getMusiques();
        for (int i = 0; i < musiques.size(); i++) {
            Musique m = musiques.get(i);
            String s = "      {\"location\": [\"" + echapper(m.getFichier().toURI().toString()) + "\"]";
            s += ", \"title\": \"" + echapper(m.getTitre()) + "\"";
            if (!m.getArtiste().isEmpty()) {
                s += ", \"creator\": \"" + echapper(m.getArtiste()) + "\"";
            }
            if (!m.getAlbum().isEmpty()) {
                s += ", \"album\": \"" + echapper(m.getAlbum()) + "\"";
            }
            s += "}";
            if (i < musiques.size() - 1) {
                s += ",";
            }
            lignes.add(s);
        }
        lignes.add("    ]");
        lignes.add("  }");
        lignes.add("}");
        Files.write(sortie.toPath(), lignes, StandardCharsets.UTF_8);
    }

    private String echapper(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    @Override
    public String extension() {
        return "jspf";
    }
}
