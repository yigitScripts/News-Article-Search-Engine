import java.util.ArrayList;
import java.util.List;

public class CSVUtils {

    public static List<String> split(String line) {
        List<String> list = new ArrayList<>();

        // splits by comma but ignores commas inside quotes
        String regex = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

        String[] tokens = line.split(regex, -1);

        for (int i = 0; i < tokens.length; i++) {
            String str = tokens[i].trim();

            if (str.startsWith("\"") && str.endsWith("\"")) {
                if (str.length() > 1) {
                    str = str.substring(1, str.length() - 1);
                }
            }

            str = str.replace("\"\"", "\"");

            list.add(str);
        }

        return list;
    }
}