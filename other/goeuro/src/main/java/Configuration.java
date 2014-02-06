/**
 * Just a basic holder for some configuration parameters of the application.
 */
public class Configuration {
    private Configuration() {
        // nop
    }

    public static final String BASE_API_URL = "https://api.goeuro.com/api/v1/suggest/position/en/name/";
    public static final String CHARSET = "UTF-8";
    public static final String OUTPUT_FILE_NAME = "suggestions.csv";
}
