import java.io.File;
import java.util.*;

public class SearchEngine {
    Dictionary<String, Article> artMap;
    Dictionary<String, Dictionary<String, Integer>> idxMap;
    Set<String> stops = new HashSet<>();

    String delimiters = "[-+=\\s\\r\\n1234567890’'\"(){}<>\\[\\]:,‒–—―…!\\.«»-‐\\?‘’“”;/⁄␠·&@\\*\\\\•\\^¤¢$€£¥₩₪†‡°¡¿¬#№%‰‱¶′§~¨_\\|¦⁂☞∴‽※]+";

    public void loadStops(String path) {
        try {
            File f = new File(path);
            Scanner sc = new Scanner(f);
            while (sc.hasNext()) {
                String str = sc.nextLine();
                stops.add(str.trim().toLowerCase());
            }
            sc.close();
        } catch (Exception e) {
            System.out.println("Error: Stop words file.");
        }
    }

    public void loadData(String path, int cap, double lf, String h, String c) {
        System.out.println("Settings: " + h + " / " + c + " / LF=" + lf);
        long[] res = runLoad(path, cap, lf, h, c);
        System.out.println("Indexing Time: " + res[0] + " ms");
        System.out.println("Collision Count: " + res[1]);
    }

    private long[] runLoad(String path, int cap, double lf, String h, String c) {
        artMap = new Dictionary<>(cap, lf, h, c);
        idxMap = new Dictionary<>(cap * 5, lf, h, c);

        long t1 = System.nanoTime();
        try {
            File f = new File(path);
            Scanner sc = new Scanner(f);

            if (sc.hasNext()) {
                sc.nextLine(); // skip header
            }

            while (sc.hasNext()) {
                String line = sc.nextLine();
                List<String> row = CSVUtils.split(line);

                if (row.size() >= 10) {
                    // get data from row
                    String id = row.get(0);
                    String author = row.get(1);
                    String date = row.get(2);
                    String cat = row.get(3);
                    String url = row.get(5);
                    String title = row.get(6);
                    String txt = row.get(9);

                    Article a = new Article(id, author, date, cat, title, url, txt);
                    artMap.put(id, a);

                    // split words
                    String[] words = txt.split(delimiters);
                    for (int i = 0; i < words.length; i++) {
                        String w = words[i];
                        String clean = w.trim().toLowerCase();

                        if (clean.isEmpty()) {
                            continue;
                        }
                        if (stops.contains(clean)) {
                            continue;
                        }

                        Dictionary<String, Integer> map = idxMap.get(clean);
                        if (map == null) {
                            map = new Dictionary<>(100, lf, h, c);
                            idxMap.put(clean, map);
                        }

                        Integer cnt = map.get(id);
                        if (cnt == null) {
                            map.put(id, 1);
                        } else {
                            map.put(id, cnt + 1);
                        }
                    }
                }
            }
            sc.close();
        } catch (Exception e) {
            return new long[]{0, 0};
        }

        long t2 = System.nanoTime();
        long diff = (t2 - t1) / 1000000;
        return new long[]{diff, idxMap.getCols()};
    }

    public void searchId(String id) {
        Article a = artMap.get(id);
        if (a != null) {
            System.out.println(a);
        } else {
            System.out.println("Not found.");
        }
    }

    public void searchText(String text) {
        Map<String, Integer> scores = new HashMap<>();
        String[] words = text.split(delimiters);

        for (int i = 0; i < words.length; i++) {
            String w = words[i];
            String clean = w.trim().toLowerCase();

            if (clean.isEmpty()) {
                continue;
            }
            if (stops.contains(clean)) {
                continue;
            }

            Dictionary<String, Integer> map = idxMap.get(clean);
            if (map != null) {
                List<String> ids = map.keys();
                // calculate scores
                for (int j = 0; j < ids.size(); j++) {
                    String id = ids.get(j);
                    int val = map.get(id);

                    if (scores.containsKey(id)) {
                        scores.put(id, scores.get(id) + val);
                    } else {
                        scores.put(id, val);
                    }
                }
            }
        }

        if (scores.isEmpty()) {
            System.out.println("No results.");
            return;
        }

        List<Score> list = new ArrayList<>();
        for (String key : scores.keySet()) {
            list.add(new Score(key, scores.get(key)));
        }

        // simple sort
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.size() - 1 - i; j++) {
                if (list.get(j).pts < list.get(j + 1).pts) {
                    Score temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }

        System.out.println("--- Top 5 ---");
        int limit = 5;
        if (list.size() < 5) {
            limit = list.size();
        }

        for (int i = 0; i < limit; i++) {
            Score s = list.get(i);
            System.out.println("#" + (i + 1) + " Score: " + s.pts);
            System.out.println(artMap.get(s.id));
            System.out.println("---------------");
        }
    }

    public void benchmark(String dataPath, String searchPath) {
        double[] lfs = {0.5, 0.8};
        String[] hashes = {"SSF", "PAF"};
        String[] colls = {"LP", "DH"};
        int cap = 10007;


        // string format for interface
        System.out.println("\nBENCHMARK RESULTS");
        System.out.println(String.format("%-4s %-4s %-4s %-15s %-12s %-20s",
                "LF", "Hash", "Coll", "Collisions", "Index(ms)", "Avg Search Time(ms)"));
        System.out.println("-----------------------------------------------------------------");

        for (int i = 0; i < lfs.length; i++) {
            for (int j = 0; j < hashes.length; j++) {
                for (int k = 0; k < colls.length; k++) {
                    double lf = lfs[i];
                    String h = hashes[j];
                    String c = colls[k];

                    long[] loadRes = runLoad(dataPath, cap, lf, h, c);
                    double searchTime = runSearchTest(searchPath);

                    System.out.println(String.format("%.1f  %-4s %-4s %-15d %-12d %-20.6f",
                            lf, h, c, loadRes[1], loadRes[0], searchTime));
                }
            }
        }
        System.out.println("-----------------------------------------------------------------");
    }

    private double runSearchTest(String path) {
        List<String> words = new ArrayList<>();
        try {
            Scanner sc = new Scanner(new File(path));
            while (sc.hasNext()) {
                words.add(sc.next());
            }
            sc.close();
        } catch (Exception e) {
            return 0;
        }

        long t1 = System.nanoTime();
        for (int i = 0; i < words.size(); i++) {
            idxMap.get(words.get(i));
        }
        long t2 = System.nanoTime();

        double avg = (double)(t2 - t1) / words.size();
        return avg / 1000000.0; // convert to ms
    }

    class Score {
        String id;
        int pts;
        Score(String id, int pts) {
            this.id = id;
            this.pts = pts;
        }
    }
}