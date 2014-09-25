package com.goeuro.test;

import com.google.common.net.UrlEscapers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

public final class Launcher {

    private static final Logger log = LogManager.getLogger(Launcher.class);

    public static void main(String[] args) {
        validateInput(args);
        final String input = args[0];
        log.info("User input: '" + input + "'");

        // 1. fetch data from server
        String serverResponse = null;
        try {
            DataFetcher dataFetcher = new DataFetcher();
            serverResponse = dataFetcher.fetch(Configuration.BASE_API_URL + UrlEscapers.urlFragmentEscaper().escape(input));
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
            log.info("Sorry, nothing found");
        } else {
            try {
                DataWriter.writeCsv(Configuration.OUTPUT_FILE_NAME, locations);
                log.info("Completed successfully! See '" + Configuration.OUTPUT_FILE_NAME + "' for results.");
            } catch (IOException e) {
                exit("Failed to save data to file", e);
            }
        }
    }

    private static void validateInput(String[] args) {
        if (args.length != 1) {
            log.error("Invalid number of arguments");
            log.info("Usage: java -jar GoEuroTest.jar \"STRING\"");
            System.exit(1);
        }
    }

    private static void exit(String errorMessage, Exception cause) {
        log.error(errorMessage, cause);
        System.exit(1);
    }
}
