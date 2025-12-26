import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String dataFile = "CNN_Articels.csv";
        String searchFile = "search.txt";
        String stopFile = "stop_words_en.txt";

        SearchEngine se = new SearchEngine();
        se.loadStops(stopFile);

        // load default
        se.loadData(dataFile, 10007, 0.5, "PAF", "DH");

        Scanner sc = new Scanner(System.in);
        boolean run = true;

        while (run) {
            System.out.println("\n--- SEARCH ENGINE ---");
            System.out.println("1. Search ID");
            System.out.println("2. Search Text");
            System.out.println("3. Run Benchmark");
            System.out.println("4. Exit");
            System.out.print("Choice: ");

            String op = sc.next();

            if (op.equals("1")) {
                System.out.print("ID: ");
                String id = sc.next();
                se.searchId(id);
            }
            else if (op.equals("2")) {
                System.out.print("Text: ");
                sc.nextLine();
                String text = sc.nextLine();
                se.searchText(text);
            }
            else if (op.equals("3")) {
                System.out.println("Running Benchmark...");
                se.benchmark(dataFile, searchFile);
            }
            else if (op.equals("4")) {
                run = false;
            }
            else {
                System.out.println("Invalid. Please select a new option.");
            }
        }
    }
}