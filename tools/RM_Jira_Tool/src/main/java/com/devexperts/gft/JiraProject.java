package com.devexperts.gft;

public class JiraProject {

    private final String jiraKey;
    private final String name;
    private final String versionPattern;

    public JiraProject(String jiraKey, String name, String versionPattern) {
        this.jiraKey = jiraKey;
        this.name = name;
        this.versionPattern = versionPattern;
    }

    public String getJiraKey() {
        return jiraKey;
    }

    public String getName() {
        return name;
    }

    public String getVersionPattern() {
        return versionPattern;
    }

    @Override
    public String toString() {
        return "JiraProject{" +
                "jiraKey='" + jiraKey + '\'' +
                ", name='" + name + '\'' +
                ", versionPattern='" + versionPattern + '\'' +
                '}';
    }
}
