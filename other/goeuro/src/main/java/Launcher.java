import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * I thought I'd mention some of the things that could be improved / introduced given more time.
 * 1) use logging (f.e., log4j) instead of system.out
 * 2) introduce CsvAware interface with a single method asCsv() and make Location implement it so that
 * data writer can be generified
 * 3) exception handling could be improved to some extent, but any problem investigation will boil down
 * to stack trace investigation any way
 */
public class Launcher {

    public static void main(String[] args) {
        validateInput(args);
        final String input = args[0];

        // 1. fetch data from server
        String serverResponse = null;
        try {
            DataFetcher dataFetcher = new DataFetcher();
            serverResponse = dataFetcher.fetch(Configuration.BASE_API_URL + URLEncoder.encode(input, Configuration.CHARSET));
        } catch (Exception e) {
            exit("Failed to fetch suggestions for the given input", e);
        }

        // 2. parse it
        List<Location> locations = null;
        try {
            DataParser<Location> dataParser = new DataParser<>(Location.class);
            locations = dataParser.parse(serverResponse);
        } catch (Exception e) {
            exit("Failed to parse data", e);
        }

        // 3. save as csv
        assert locations != null;
        if (locations.isEmpty()) {
            System.out.println("No locations were found");
        } else {
            try {
                new DataWriter().writeCsv(new File(Configuration.OUTPUT_FILE_NAME), locations);
                System.out.println("Completed successfully! See '" + Configuration.OUTPUT_FILE_NAME + "' for results.");
            } catch (IOException e) {
                exit("Failed to save data to file", e);
            }
        }
    }

    private static void validateInput(String[] args) {
        if (args.length != 1) {
            System.out.println("Invalid number of arguments");
            System.out.println("Usage: java -jar GoEuroTest.jar \"STRING\"");
            System.exit(1);
        }
    }

    private static void exit(String errorMessage, Exception cause) {
        System.out.println(errorMessage);
        cause.printStackTrace();
        System.exit(1);
    }
}