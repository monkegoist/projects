package com.goeuro.test;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Returns given URL response data as string.
 */
public class DataFetcher {

    private final HttpClient httpClient = HttpClients.createDefault();

    /**
     * @param url url to get
     * @return response data
     * @throws IllegalArgumentException url is either null or empty; response code is not {@link HttpStatus#SC_OK}
     * @throws IOException              error fetching or reading data
     */
    public String fetch(String url) throws IOException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(url), "URL cannot be null or empty");
        HttpGet request = new HttpGet(url);
        HttpResponse response = httpClient.execute(request);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            throw new IllegalArgumentException("Status code of URL = '" + url + "' is " + statusCode +
                    ", expected = " + HttpStatus.SC_OK);
        }

        return EntityUtils.toString(response.getEntity());
    }
}
