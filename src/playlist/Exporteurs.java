package playlist;

public class Exporteurs {

    // renvoie l'exporteur du type demande, ou null si le type n'existe pas
    public static Exporteur pour(String type) {
        switch (type.toLowerCase()) {
            case "m3u":
                return new ExportM3U();
            case "xspf":
                return new ExportXSPF();
            default:
                return null;
        }
    }
}
