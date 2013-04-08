package com.devexperts.gft;

import com.devexperts.gft.utils.MavenPomXml;
import com.devexperts.gft.utils.MavenVersion;
import com.devexperts.gft.utils.scm.ScmFactory;
import com.devexperts.gft.utils.scm.ScmProvider;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.jdom2.JDOMException;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

/**
 * Author: Andrew Logvinov
 * Date: 10/1/11
 * Time: 7:12 PM
 */
public abstract class AbstractDevexMojo extends AbstractMojo {

    // Main pom.xml
    protected static final File MAIN_POM = new File("pom.xml");

    // Backup pom.xml to restore original Core version
    protected static final File BACKUP_POM = new File("pom.xml.versionsBackup");

    // Regex for validating pre-release version
    protected static final Pattern PRE_RELEASE_PATTERN = Pattern.compile("^\\d+\\-[a-z]$");

    private ScmProvider projectScmProvider;
    private MavenVersion mavenVersion;
    private boolean isScmUrlValid;

    // --------------- Properties injected by Maven ---------------

    /**
     * Injected MavenProject object.
     *
     * @parameter property="project"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * Injected MavenSession object.
     *
     * @parameter property="session"
     * @required
     * @readonly
     */
    private MavenSession session;

    /**
     * Injected Maven BuildPluginManager object.
     *
     * @component
     * @required
     */
    private BuildPluginManager pluginManager;

    /**
     * @parameter property="project.version"
     */
    private String projectVersion;

    /**
     * @parameter property="project.artifactId"
     */
    private String projectArtifactId;

    /**
     * SCM repository URL in Maven format (see http://maven.apache.org/pom.html#SCM for reference).
     *
     * @parameter property="project.scm.connection"
     */
    private String scmUrl;

    /**
     * Tag prefix in SCM repository.
     *
     * @parameter property="bundle.release.prefix" default-value="R-"
     */
    private String tagPrefix;

    /**
     * Branch prefix in SCM repository.
     *
     * @parameter property="bundle.branch.prefix" default-value="B-"
     */
    private String branchPrefix;

    /**
     * Specifies if this bundle depends on Core.
     *
     * @parameter property="bundle.depends.core" default-value="true"
     */
    private boolean isCoreDependent;

    /**
     * @parameter property="core.version"
     */
    private String coreVersion;

    /**
     * @parameter property="bundle.trunk.release"
     */
    private String bundleTrunkRelease;

    /**
     * @parameter property="bundle.trunk.pre-release"
     */
    private String bundleTrunkPreRelease;

    // --------------- Main method for all mojos ---------------

    public abstract void execute() throws MojoExecutionException, MojoFailureException;

    // --------------- Interface ---------------

    /**
     * Check that ${project.version} and ${core.version} (if project depends on Core) do not contain SNAPSHOT.
     *
     * @throws MojoExecutionException
     */
    public void checkVersions() throws MojoExecutionException {
        if (getProjectVersion().contains("SNAPSHOT")) {
            final String message = "${project.version} property must not contain SNAPSHOT! Was: " + getProjectVersion();
            fail(message);
        }
        if (isBundleCoreDependent() && (getCoreVersion().contains("SNAPSHOT"))) {
            final String message = "${core.version} property must not contain SNAPSHOT! Was: " + getCoreVersion();
            fail(message);
        }
    }

    /**
     * Get valid SCM URL.
     *
     * @return String
     * @throws MojoExecutionException
     */
    public String getScmUrl() throws MojoExecutionException {
        validateScmUrl();
        String url = scmUrl;
        url = url.replaceFirst("^scm:", "");
        url = url.replaceFirst("^[a-z]+:", "");
        if (!url.endsWith("/"))
            url += "/";
        return url;
    }

    /**
     * Get valid SCM provider.
     *
     * @return ScmProvider
     * @throws MojoExecutionException
     */
    public ScmProvider getScmProvider() throws MojoExecutionException {
        if (projectScmProvider == null) {
            validateScmUrl();
            StringTokenizer st = new StringTokenizer(scmUrl, ":");
            // We don't need first token since it's going to be 'scm'
            st.nextToken();
            String provider = st.nextToken();
            if (provider.equals("svn")) {
                projectScmProvider = ScmFactory.getSvnImpl();
            } else {
                final String message = "Incorrect SCM specified. Only SVN is supported at the moment.";
                rollbackFail(message);
            }
        }
        return projectScmProvider;
    }

    /**
     * This method can be used to set necessary version to current project and all of the child projects.
     * <p/>
     * Please note, it cannot be used when you need to set version in some other project from current one.
     * Use {@link #setExternalVersion(String, File)} in this case instead.
     *
     * @param version version to set
     * @throws MojoExecutionException
     */
    public void setVersion(String version) throws MojoExecutionException {
        executeMojo(
                // version 2.0 is too verbose, rolled back to 1.3.1
                plugin(
                        "org.codehaus.mojo",
                        "versions-maven-plugin",
                        "1.3.1"
                ),
                goal("set"),
                configuration(
                        element(name("newVersion"), version)
                ),
                executionEnvironment(
                        getProject(),
                        getSession(),
                        getPluginManager()
                )
        );
    }

    /**
     * This method can be used to set necessary version to the project with its root in {@code path}.
     * <p/>
     * If you need to set version to the current project, consider using {@link #setVersion(String)} instead.
     *
     * @param version version to set
     * @param path project root location
     * @throws MojoExecutionException
     */
    public void setExternalVersion(String version, File path) throws MojoExecutionException {
        final String command = "org.codehaus.mojo:versions-maven-plugin:1.3.1:set " +
                "-DnewVersion=" + version + " -DgenerateBackupPoms=false";
        executeMojo(
                plugin(
                        "org.codehaus.mojo",
                        "exec-maven-plugin",
                        "1.2.1"
                ),
                goal("exec"),
                configuration(
                        element(name("executable"), "mvn"),
                        element(name("commandlineArgs"), command),
                        element(name("workingDirectory"), path.getAbsolutePath())
                ),
                executionEnvironment(
                        getProject(),
                        getSession(),
                        getPluginManager()
                )
        );
    }

    /**
     * This method returns {@link MavenPomXml} object constructed from a given path.
     *
     * @param path path to pom.xml
     * @return pom.xml representation as {@link MavenPomXml} object
     * @throws MojoExecutionException
     */
    public MavenPomXml pomXmlFromFile(File path) throws MojoExecutionException {
        final String message = "Failed to read contents of pom.xml";
        try {
            return new MavenPomXml(path);
        } catch (JDOMException e) {
            fail(message, e);
        } catch (IOException e) {
            fail(message, e);
        }
        return null;
    }

    public MavenProject getProject() {
        return project;
    }

    public MavenSession getSession() {
        return session;
    }

    public BuildPluginManager getPluginManager() {
        return pluginManager;
    }

    public String getProjectArtifactId() {
        return projectArtifactId;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public String getTagPrefix() {
        return tagPrefix;
    }

    public String getBranchPrefix() {
        return branchPrefix;
    }

    public boolean isBundleCoreDependent() {
        return isCoreDependent;
    }

    public String getCoreVersion() {
        return coreVersion;
    }

    public String getBundleTrunkRelease() {
        return bundleTrunkRelease;
    }

    public String getBundleTrunkPreRelease() {
        return bundleTrunkPreRelease;
    }

    // needs to be initialized lazily
    public MavenVersion getMavenVersion() {
        if (mavenVersion == null)
            mavenVersion = MavenVersion.fromString(getProjectVersion());
        return mavenVersion;
    }

    // --------------- Methods to fail the build ---------------

    public void fail(String message) throws MojoExecutionException {
        getLog().error(message);
        throw new MojoExecutionException("");
    }

    public void fail(String message, Exception e) throws MojoExecutionException {
        getLog().error(message);
        throw new MojoExecutionException("", e);
    }

    public void rollbackFail(String message) throws MojoExecutionException {
        getLog().error(message);
        getLog().error("RELEASE FAILED");
        rollbackPomChanges();
        throw new MojoExecutionException("");
    }

    // --------------- Private methods ---------------

    private void validateScmUrl() throws MojoExecutionException {
        if (!isScmUrlValid) {
            StringTokenizer st = new StringTokenizer(scmUrl, ":");
            if (st.countTokens() < 3 || !st.nextToken().equals("scm")) {
                final String message = "Malformed SCM connection URL. See http://maven.apache.org/pom.html#SCM for reference.";
                rollbackFail(message);
            }
            isScmUrlValid = true;
        }
    }

    private void rollbackPomChanges() throws MojoExecutionException {
        if (BACKUP_POM.exists()) {
            final String command = "org.codehaus.mojo:versions-maven-plugin:2.0:revert";
            getLog().info("Performing rollback of pom.xml changes...");
            executeMojo(
                    plugin(
                            "org.codehaus.mojo",
                            "exec-maven-plugin",
                            "1.2.1"
                    ),
                    goal("exec"),
                    configuration(
                            element(name("executable"), "mvn"),
                            element(name("commandlineArgs"), command),
                            element(name("workingDirectory"), ".")
                    ),
                    executionEnvironment(
                            getProject(),
                            getSession(),
                            getPluginManager()
                    )
            );
            getLog().info("Rollback completed");
        } else {
            getLog().info("Cannot perform rollback since pom.xml.versionsBackup file doesn't exist");
        }
    }

    // --------------- Visible for testing only ---------------

    void setScmUrl(String scmUrl) {
        this.scmUrl = scmUrl;
    }
}