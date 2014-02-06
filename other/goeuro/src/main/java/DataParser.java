import com.google.common.base.Strings;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * JSON data parser.
 */
public class DataParser<T> {

    private final ObjectMapper mapper = new ObjectMapper();
    private final Class<T> type;

    public DataParser(Class<T> type) {
        this.type = type;
    }

    /**
     * We expect to find "results" root element with array of child values of type <code>T</code>.
     *
     * @param content json
     * @param <T>     content type
     * @return list of objects or empty list in case content is null or empty
     * @throws Exception parse error
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> parse(String content) throws Exception {
        if (Strings.isNullOrEmpty(content))
            return Collections.emptyList();

        JsonNode tree = mapper.readTree(content);
        JsonNode rootNode = tree.get("results");
        assert rootNode.isArray();

        List<T> values = new ArrayList<>();
        Iterator<JsonNode> iterator = rootNode.getElements();

        while (iterator.hasNext())
            values.add((T) mapper.readValue(iterator.next(), type));

        return values;
    }
}