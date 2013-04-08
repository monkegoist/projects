package com.devexperts.gft;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class Authenticator {

    private static final Logger log = Logger.getLogger(Authenticator.class);

    private final Configuration configuration;
    private final ObjectMapper mapper = new ObjectMapper();

    public Authenticator(Configuration configuration) {
        this.configuration = configuration;
    }

    public DefaultHttpClient authenticate() throws Exception {

        log.info("Logging in to Jira: " + "[username=" + configuration.getLogin() + "]");

        // 1. construct JSON string object from user details
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", configuration.getLogin());
        map.put("password", configuration.getPassword());
        String userDetails = mapper.writeValueAsString(map);

        // 2. create data to be posted to server
        ClientConnectionManager connectionManager = new DefaultHttpClient().getConnectionManager();
        DefaultHttpClient httpClient =
                new DefaultHttpClient(new PoolingClientConnectionManager(connectionManager.getSchemeRegistry()));
        HttpPost httpPost = new HttpPost(configuration.getAuthUrl());
        StringEntity stringEntity = new StringEntity(userDetails);
        stringEntity.setContentType("application/json");
        httpPost.setEntity(stringEntity);

        // 3. post data and parse response
        HttpResponse response = httpClient.execute(httpPost);
        int status = response.getStatusLine().getStatusCode();

        if (status != HttpStatus.SC_OK)
            throw new IllegalStateException("Failed to login! HTTP status: " + status);

        JsonNode tree = mapper.readTree(response.getEntity().getContent());
        JsonNode session = tree.get("session");

        // 4. store cookie
        CookieStore cookieStore = new BasicCookieStore();
        BasicClientCookie cookie =
                new BasicClientCookie(session.get("name").getTextValue(), session.get("value").getTextValue());
        cookie.setDomain(configuration.getHostName());
        cookie.setPath("/");
        cookieStore.addCookie(cookie);
        httpClient.setCookieStore(cookieStore);

        log.info("Successfully logged in");

        return httpClient;
    }
}
