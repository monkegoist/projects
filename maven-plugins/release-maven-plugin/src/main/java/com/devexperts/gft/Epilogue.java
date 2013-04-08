package com.devexperts.gft;

import com.devexperts.gft.utils.MavenPomXml;
import com.devexperts.gft.utils.MavenVersion;
import com.devexperts.gft.utils.scm.ScmException;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

/**
 * Author: Andrew Logvinov
 * Date: 9/25/11
 * Time: 5:08 PM
 * <p/>
 * This class contains code that performs post-release work:
 * 1) Sets new component snapshot version in pom.xml
 * 2) Reverts core.version back to initial state (if necessary)
 * 3) Commits changes to SCM system
 *
 * @description This goal performs post-release work.
 * @goal commit-changes
 * @requiresOnline true
 */
public final class Epilogue extends AbstractDevexMojo {

    private static final String SEPARATOR = System.getProperty("file.separator");

    /**
     * Path to working copy.
     *
     * @parameter alias="bundle.basedir.path" property="bundle.basedir.path"
     * @required
     */
    private File wcPath;

    private File wcPomPath;
    private MavenPomXml pomXml;
    private String commitMessage;

    @Override
    public void execute() throws MojoExecutionException {

        checkVersions();

        wcPomPath = new File(wcPath.getAbsolutePath() + SEPARATOR + "pom.xml");

        final String artifactId = getProjectArtifactId();
        final String projectVersion = getProjectVersion();

        // We should handle 2 cases: a) pre-release; b) general release
        Matcher matcher = PRE_RELEASE_PATTERN.matcher(projectVersion);
        if (matcher.matches()) {
            changePomsPreRelease();
        } else {
            if (!artifactId.contains("gscore") || projectVersion.contains("-")) {
                // Any component release OR Core release from branch
                changePomsGeneralRelease();
            } else {
                // Core release from trunk
                changePomsCoreTrunkRelease();
            }
        }

        try {
            getLog().info("Writing changes to main pom.xml file");
            MavenPomXml.writeXmlDocument(pomXml.getXmlDocument(), wcPomPath);
        } catch (IOException e) {
            final String message = "Failed to modify pom.xml";
            fail(message, e);
        }

        getLog().info("Committing changes to repository...");
        getLog().info("Path to working copy: " + wcPath.getAbsolutePath());
        try {
            String status = getScmProvider().commit(commitMessage, wcPath);
            getLog().info("Status: " + status);
        } catch (ScmException e) {
            final String message = "Failed to commit changes to repository";
            fail(message, e);
        }

        getLog().info("All done!");
    }

    private void changePomsGeneralRelease() throws MojoExecutionException {

        final String currentVersion = getProjectVersion();
        final String nextSnapshotVersion = getMavenVersion().getNextSnapshotVersion();
        final String nextReleaseVersion = getMavenVersion().getNextReleaseVersion();

        setExternalVersion(nextSnapshotVersion, wcPath);
        pomXml = pomXmlFromFile(wcPomPath);

        if (!currentVersion.contains("-"))
            pomXml.setPropertyValue("bundle.trunk.pre-release", nextReleaseVersion + "-a");

        if (isBundleCoreDependent()) {
            String originalCoreVersion = "";
            // Try to retrieve original Core version
            try {
                final String wcBackupPomPath = wcPath.getAbsolutePath() + SEPARATOR + "pom.xml.versionsBackup";
                originalCoreVersion = new MavenPomXml(new File(wcBackupPomPath)).getPropertyValue("core.version");
            } catch (Exception e) {
                getLog().error(e);
            }
            pomXml.setPropertyValue("core.version", originalCoreVersion);
        }

        commitMessage = "New snapshot version: " + nextSnapshotVersion;
    }

    private void changePomsPreRelease() throws MojoExecutionException {

        String[] arr = getProjectVersion().split("\\-");
        char nextSuffix = (char) (arr[1].charAt(0) + 1);
        final String nextPreReleaseVersion = arr[0] + "-" + nextSuffix;

        setExternalVersion(
                getProjectArtifactId().contains("gscore") ?
                        "TRUNK-SNAPSHOT" :
                        MavenVersion.fromString(arr[0]).getCurrentSnapshotVersion(),
                wcPath
        );

        pomXml = pomXmlFromFile(wcPomPath);
        pomXml.setPropertyValue("bundle.trunk.pre-release", nextPreReleaseVersion);

        commitMessage = "New pre-release version: " + nextPreReleaseVersion;
    }

    private void changePomsCoreTrunkRelease() throws MojoExecutionException {

        final int next = Integer.valueOf(getProjectVersion()) + 1;
        final String nextReleaseVersion = String.valueOf(next);
        final String nextPreReleaseVersion = nextReleaseVersion + "-a";

        setExternalVersion("TRUNK-SNAPSHOT", wcPath);

        pomXml = pomXmlFromFile(wcPomPath);
        pomXml.setPropertyValue("bundle.trunk.release", nextReleaseVersion);
        pomXml.setPropertyValue("bundle.trunk.pre-release", nextPreReleaseVersion);

        commitMessage = "New release version: " + nextReleaseVersion;
    }
}