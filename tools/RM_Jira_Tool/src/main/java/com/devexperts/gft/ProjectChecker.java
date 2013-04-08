package com.devexperts.gft;

import com.google.common.base.Joiner;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ProjectChecker implements Runnable {

    private static final Logger log = Logger.getLogger(ProjectChecker.class);

    private final Configuration configuration;
    private final JiraProject jiraProject;
    private final Map<JiraProject, Boolean> results;
    private final DefaultHttpClient httpClient;
    private final HttpHost host;
    private final ObjectMapper mapper = new ObjectMapper();

    public ProjectChecker(Configuration configuration, JiraProject jiraProject, Map<JiraProject, Boolean> results,
                          DefaultHttpClient httpClient) {
        this.configuration = configuration;
        this.jiraProject = jiraProject;
        this.results = results;
        this.httpClient = httpClient;
        this.host = new HttpHost(configuration.getHostName(), configuration.getPort(), configuration.getScheme());
    }

    @Override
    public void run() {
        try {
            log.info("Checking project '" + jiraProject.getName() + "'");

            // 1. get list of versions associated with a project
            HttpGet httpGet = new HttpGet(configuration.getApiUrl() + "project/" + jiraProject.getJiraKey() + "/versions");
            HttpResponse response = httpClient.execute(host, httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (!checkStatus(statusCode))
                return;
            JsonNode versionsTree = mapper.readTree(response.getEntity().getContent());

            // 2. find version id of interest
            int versionId = getVersionId(versionsTree);

            // 3. get list of tickets with this fix version
            httpGet = new HttpGet(configuration.getApiUrl() + "search?jql=fixVersion=" + versionId);
            response = httpClient.execute(httpGet);
            statusCode = response.getStatusLine().getStatusCode();
            if (!checkStatus(statusCode))
                return;
            JsonNode issuesTree = mapper.readTree(response.getEntity().getContent());

            // 4. iterate over tickets and determine if any of them are in 'Waiting for Build' status
            Set<String> issues = getIssues(issuesTree);
            boolean result = isBuildNeeded(issues);
            log.info("Build needed for '" + jiraProject.getName() + "'? -- " + result);

            results.put(jiraProject, result);
        } catch (IOException e) {
            log.error("Failed to get information for project '" + jiraProject.getName() + "'", e);
        }
    }

    private boolean checkStatus(int statusCode) {
        if (statusCode != HttpStatus.SC_OK) {
            log.error("Failed to get versions for project '" + jiraProject.getJiraKey() + "'");
            return false;
        } else {
            return true;
        }
    }

    private boolean isBuildNeeded(Set<String> issues) throws IOException {
        boolean needed = false;
        HttpGet httpGet;
        for (String key : issues) {
            httpGet = new HttpGet(configuration.getApiUrl() + "issue/" + key);
            HttpResponse response = httpClient.execute(host, httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            checkStatus(statusCode);
            JsonNode tree = mapper.readTree(response.getEntity().getContent());
            JsonNode fields = tree.get("fields");
            JsonNode statusNode = fields.get("status");
            String status = statusNode.get("value").get("name").getTextValue();
            log.info("Key = '" + key + "', status = '" + status + "'");
            if ("Waiting for build".equals(status)) {
                needed = true;
                break;
            }
        }
        return needed;
    }

    private Set<String> getIssues(JsonNode tree) {
        Set<String> issues = new LinkedHashSet<String>();
        JsonNode subTree = tree.get("issues");
        for (int i = 0; i < subTree.size(); i++) {
            JsonNode node = subTree.get(i);
            issues.add(node.get("key").getTextValue());
        }
        log.info("List of issues: [" + Joiner.on(", ").join(issues) + "]");
        return issues;
    }

    private int getVersionId(JsonNode tree) {
        int versionId = -1;
        TreeMap<String, Integer> versions = new TreeMap<String, Integer>();
        for (int i = 0; i < tree.size(); i++) {
            JsonNode node = tree.get(i);
            int id = node.get("id").asInt();
            String name = node.get("name").getTextValue();
            if (name.startsWith(jiraProject.getVersionPattern()))
                versions.put(name, id);
        }
        if (!versions.isEmpty()) {
            Map.Entry<String, Integer> entry = versions.descendingMap().firstEntry();
            log.info("Version = '" + entry.getKey() + "'");
            versionId = entry.getValue();
            log.info("VersionId = '" + versionId + "'");
        }
        if (versionId == -1)
            log.error("Failed to find VersionId!");

        return versionId;
    }
}