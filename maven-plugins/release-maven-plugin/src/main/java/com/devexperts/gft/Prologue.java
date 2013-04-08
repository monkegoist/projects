package com.devexperts.gft;

import com.devexperts.gft.utils.MavenPomXml;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.IOException;
import java.util.regex.Matcher;

/**
 * Author: Andrew Logvinov
 * Date: 9/25/11
 * Time: 1:40 AM
 * <p/>
 * This class contains code that makes the following preparations before release:
 * 1) If project is Core-dependent, checks core.version
 * 2) Makes a copy of pom.xml (pom_release.xml) with correct release version(s)
 *
 * @aggregator
 * @description This goal makes preparations before release.
 * @goal prepare
 * @requiresDirectInvocation true
 */
public final class Prologue extends AbstractDevexMojo {

    /**
     * @parameter property="pre-release" default-value="false"
     */
    private boolean preRelease;

    private MavenPomXml pomXml;

    @Override
    public void execute() throws MojoExecutionException {

        final String artifactId = getProjectArtifactId();
        final String projectVersion = getProjectVersion();

        getLog().info("Initial ${project.version} in pom.xml: " + projectVersion);

        // We should handle 2 cases: a) pre-release; b) general release
        if (preRelease) {
            preparePreRelease();
        } else {
            if (!artifactId.contains("gscore") || !projectVersion.equals("TRUNK-SNAPSHOT")) {
                // Any component release OR Core release from branch
                prepareGeneralRelease();
            } else {
                // Core release from trunk
                prepareCoreTrunkRelease();
            }
        }

        try {
            MavenPomXml.writeXmlDocument(pomXml.getXmlDocument(), MAIN_POM);
        } catch (IOException e) {
            final String message = "Failed to write release changes to pom.xml";
            rollbackFail(message);
        }
    }

    private void prepareGeneralRelease() throws MojoExecutionException {
        setReleaseVersion(getMavenVersion().getCurrentReleaseVersion());

        if (isBundleCoreDependent()) {
            checkCoreVersion();
            final String releaseCoreVersion = getCoreVersion();
            getLog().info("Release ${core.version}: " + releaseCoreVersion);
            pomXml.setPropertyValue("core.version", releaseCoreVersion);
        }
    }

    private void preparePreRelease() throws MojoExecutionException {
        getLog().info("Making pre-release from TRUNK");
        final String releaseVersion = getBundleTrunkPreRelease();

        // Validation
        final Matcher matcher = PRE_RELEASE_PATTERN.matcher(releaseVersion);
        if (!matcher.matches())
            throw new MojoExecutionException("Malformed ${bundle.trunk.pre-release} property value");

        setReleaseVersion(releaseVersion);
    }

    private void prepareCoreTrunkRelease() throws MojoExecutionException {
        getLog().info("Making release from TRUNK");
        setReleaseVersion(getBundleTrunkRelease());
    }

    private void setReleaseVersion(String version) throws MojoExecutionException {
        getLog().info("Release ${project.version} in pom.xml: " + version);
        setVersion(version);
        pomXml = pomXmlFromFile(MAIN_POM);
    }

    private void checkCoreVersion() throws MojoExecutionException {
        final String coreVersion = getCoreVersion();
        getLog().info("${bundle.depends.core} property is set to 'true'. Checking Core version...");
        getLog().info("${core.version} = " + coreVersion);
        if (coreVersion.contains("SNAPSHOT")) {
            final String message = "${core.version} must not contain 'SNAPSHOT'!";
            rollbackFail(message);
        } else {
            getLog().info("${core.version} is valid");
        }
    }
}