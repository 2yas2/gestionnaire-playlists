package playlist;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ExportXSPF implements Exporteur {

    @Override
    public void exporter(Playlist playlist, File sortie) throws IOException {
        List<String> lignes = new ArrayList<>();
        lignes.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        lignes.add("<playlist version=\"1\" xmlns=\"http://xspf.org/ns/0/\">");
        lignes.add("  <title>" + echapper(playlist.getNom()) + "</title>");
        lignes.add("  <trackList>");
        for (Musique m : playlist.getMusiques()) {
            lignes.add("    <track>");
            lignes.add("      <location>" + echapper(m.getFichier().toURI().toString()) + "</location>");
            lignes.add("      <title>" + echapper(m.getTitre()) + "</title>");
            if (!m.getArtiste().isEmpty()) {
                lignes.add("      <creator>" + echapper(m.getArtiste()) + "</creator>");
            }
            if (!m.getAlbum().isEmpty()) {
                lignes.add("      <album>" + echapper(m.getAlbum()) + "</album>");
            }
            lignes.add("    </track>");
        }
        lignes.add("  </trackList>");
        lignes.add("</playlist>");
        Files.write(sortie.toPath(), lignes, StandardCharsets.UTF_8);
    }

    private String echapper(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    @Override
    public String extension() {
        return "xspf";
    }
}
