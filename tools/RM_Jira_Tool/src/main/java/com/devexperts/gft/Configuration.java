package com.devexperts.gft;

import com.google.common.base.Joiner;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

/*
 * Example is attached to the project.
 */
public class Configuration {

    private static final Logger log = Logger.getLogger(Configuration.class);

    private static final Configuration instance = new Configuration();

    public static Configuration getInstance() {
        return instance;
    }

    // jira login
    private final String login;
    // jira password
    private final String password;

    // jira base url
    private final String baseUrl;
    // jira url to access api
    private final String apiUrl;
    // jira url to authenticate
    private final String authUrl;

    // jira hostname
    private final String hostName;
    // jira port
    private final int port;
    // jira access scheme (http or https)
    private final String scheme;

    // name of file to save results to
    private final String resultsFile;

    // list of jira projects
    private final List<JiraProject> projects;

    private Configuration() {

        String file = System.getProperty("config.file");
        checkNotNull(file);
        log.info("Reading configuration from " + file);

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(new File(file)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize application!", e);
        }

        // Initialize Jira properties
        login = properties.getProperty("jira.login");
        checkNotNull(login);
        password = properties.getProperty("jira.password");
        checkNotNull(password);
        baseUrl = properties.getProperty("jira.base_url");
        checkNotNull(baseUrl);
        apiUrl = baseUrl + "rest/api/latest/";
        authUrl = baseUrl + "rest/auth/latest/session";
        hostName = properties.getProperty("jira.hostname");
        checkNotNull(hostName);
        String port = properties.getProperty("jira.port");
        checkNotNull(port);
        this.port = Integer.parseInt(port);
        scheme = properties.getProperty("jira.scheme");
        checkNotNull(scheme);

        resultsFile = properties.getProperty("results.file");
        checkNotNull(resultsFile);

        // Initialize Jira projects
        String totalProjects = properties.getProperty("project.total");
        checkNotNull(totalProjects);
        int numProjects = Integer.parseInt(totalProjects);
        projects = new ArrayList<JiraProject>();
        for (int i = 0; i < numProjects; i++) {
            String jiraKey = properties.getProperty("project" + i + ".jira_key");
            checkNotNull(jiraKey);
            String name = properties.getProperty("project" + i + ".name");
            checkNotNull(name);
            String versionPattern = properties.getProperty("project" + i + ".version_pattern");
            checkNotNull(versionPattern);
            projects.add(new JiraProject(jiraKey, name, versionPattern));
        }

        // save configuration parameters to file
        logConfiguration();
    }

    private void logConfiguration() {
        log.info("Application properties have been initialized:");
        log.info("jira.login = '" + login + "'");
        log.info("jira.hostname = '" + hostName + "'");
        log.info("jira.port = '" + port + "'");
        log.info("jira.scheme = '" + scheme + "'");
        log.info("jira.base_url = '" + baseUrl + "'");
        log.info("results.file = '" + resultsFile + "'");
        log.info("API url = '" + apiUrl + "'");
        log.info("AUTH url = '" + authUrl + "'");
        log.info("Projects = '" + Joiner.on(", ").join(projects) + "'");
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public String getHostName() {
        return hostName;
    }

    public int getPort() {
        return port;
    }

    public String getScheme() {
        return scheme;
    }

    public String getResultsFile() {
        return resultsFile;
    }

    public List<JiraProject> getProjects() {
        return projects;
    }
}
