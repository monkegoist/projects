package com.goeuro.test;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;

/**
 * Parse array of JSON values to objects of given type.
 *
 * @param <T> object type
 */
public class DataParser<T> {

    private static final Logger log = LogManager.getLogger(DataParser.class);

    private final ObjectMapper mapper = new ObjectMapper();
    private final Class<T> type;

    private final Function<JsonNode, T> PARSE_FUNCTION = new Function<JsonNode, T>() {
        @Override
        public T apply(JsonNode jsonNode) {
            try {
                return mapper.readValue(jsonNode, type);
            } catch (IOException e) {
                log.error("Failed to parse: '" + jsonNode.toString() + "'", e);
                return null;
            }
        }
    };

    public DataParser(Class<T> type) {
        this.type = type;
    }

    /**
     * @param content json
     * @throws IllegalArgumentException content is either null or empty; API incompatibility found
     * @throws IOException              parse error
     */
    public List<T> parse(String content) throws IOException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(content), "Content cannot be null or empty");

        JsonNode tree = mapper.readTree(content);
        if (!tree.isArray()) {
            throw new IllegalArgumentException("API has changed?");
        }

        return Lists.newArrayList(Iterators.transform(tree.getElements(), PARSE_FUNCTION));
    }
}
