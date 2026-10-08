package playlist;

public class Main {

    public static void main(String[] args) {
        if (args.length > 0) {
            Cli.lancer(args);
        } else {
            System.out.println("interface graphique pas encore faite, essayer -help");
        }
    }
}
