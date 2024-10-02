package com.morningstar.config;

import com.algolia.api.SearchClient;
import com.algolia.model.search.SearchForHits;
import com.algolia.model.search.SearchMethodParams;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class TestAlgolia {

    private final String APP_ID = "77NW0NMKQS";
    private final String API_KEY = "b7d692cdf9457a849c4d23fcdadc0879";

    @Test
    public void test1() throws Exception {
        // Fetch sample dataset
        URL url = new URI("https://dashboard.algolia.com/sample_datasets/movie.json").toURL();
        InputStream stream = url.openStream();
        ObjectMapper mapper = new ObjectMapper();
        List<JsonNode> movies = mapper.readValue(stream, new TypeReference<>() {});
        stream.close();

        // Connect and authenticate with your Algolia app
        SearchClient client = new SearchClient(APP_ID, API_KEY);

        // Save records in Algolia index
        client.saveObjects("movies_index", movies);
        client.close();
    }

    @Test
    public void test2(){
        final var indexName = "test-index";

        try (var client = new SearchClient(APP_ID, API_KEY)) {
            // Create a new record
            var body = new HashMap<>();
            body.put("objectID", "object-1");
            body.put("name", "test record");

            // Add the record to an index
            var addResponse = client.saveObject(indexName, body);

            // Wait until indexing is done
            client.waitForTask(indexName, addResponse.getTaskID());

            // Search for 'test'
            var responses = client.search(
                    new SearchMethodParams().addRequests(
                            new SearchForHits()
                                    .setIndexName(indexName)
                                    .setQuery("test")
                    ), body.getClass());
            System.out.println(responses);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}