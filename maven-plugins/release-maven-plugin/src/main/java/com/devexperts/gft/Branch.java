package com.devexperts.gft;

import com.devexperts.gft.utils.MavenPomXml;
import com.devexperts.gft.utils.MavenVersion;
import com.devexperts.gft.utils.scm.ScmException;
import com.devexperts.gft.utils.scm.ScmProvider;
import org.apache.maven.plugin.MojoExecutionException;
import org.jdom2.JDOMException;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Author: Andrew Logvinov
 * Date: 11/1/11
 * Time: 3:12 PM
 * <p/>
 * This class contains code that creates branch from a given tag.
 *
 * @aggregator
 * @description This goal creates branch from a given tag.
 * @goal branch
 * @requiresDirectInvocation true
 * @requiresOnline true
 */
public final class Branch extends AbstractDevexMojo {

    /**
     * Tag number to create branch from.
     *
     * @parameter property="tag"
     * @required
     */
    private String tag;

    @Override
    public void execute() throws MojoExecutionException {

        final String tagName = getTagPrefix() + tag;
        final String tagUrl = getScmUrl() + "tags/" + tagName;
        final String branchName = getBranchPrefix() + tag;
        final String branchUrl = getScmUrl() + "branches/" + branchName;
        final File branchLocation = new File("../" + branchName);
        final Scanner scanner = new Scanner(System.in);

        getLog().info("+++++++++++++++++++++++++++++++++++++++++");
        getLog().info("Initial input parameters are:");
        getLog().info("  - tag = " + tag);
        getLog().info("  - tagPath = " + tagUrl);
        if (isBundleCoreDependent())
            getLog().info("  - core.version = " + getCoreVersion());
        getLog().info("This will:");
        getLog().info("  1. Copy the " + tagName + " tag in repository to " + branchUrl);
        getLog().info("  2. Checkout this branch to parent directory");
        getLog().info("  3. Update all pom.xml files to '-SNAPSHOT' version");
        getLog().info("  4. Commit changes");
        getLog().info("+++++++++++++++++++++++++++++++++++++++++");
        getLog().info("Are you ready to proceed? (y,n)");

        final String userInput = scanner.next();
        if (!userInput.equalsIgnoreCase("y")) {
            throw new MojoExecutionException("User cancelled the operation");
        } else {
            getLog().info("+++++++++++++++++++++++++++++++++++++++++");
        }

        final ScmProvider scmProvider = getScmProvider();

        getLog().info("1. Performing svn copy...");
        final String cpMessage = "Branch " + branchName + " has been created";
        try {
            final String cpStatus = scmProvider.copy(tagUrl, branchUrl, cpMessage);
            getLog().info("   Status: " + cpStatus);
        } catch (ScmException e) {
            fail("", e);
        }

        getLog().info("2. Checking out branch...");
        try {
            final long coStatus = scmProvider.checkout(branchUrl, branchLocation);
            getLog().info("   Status: revision " + coStatus + " checked out");
        } catch (ScmException e) {
            fail("", e);
        }

        getLog().info("3. Updating version in branch main pom.xml to -SNAPSHOT version...");
        final String snapshotVersion = MavenVersion.fromString(tag).getNextBranchSnapshotVersion();
        getLog().info("Snapshot version: " + snapshotVersion);
        setExternalVersion(snapshotVersion, branchLocation);
        setCoreVersion(branchLocation);
        getLog().info("   Status: Ok");

        getLog().info("4. Committing changes...");
        try {
            final String ciStatus = scmProvider.commit(branchName, branchLocation);
            getLog().info("   Status: " + ciStatus);
        } catch (ScmException e) {
            fail("", e);
        }
    }

    private void setCoreVersion(File path) {
        final String failMessage = "Failed to set core.version to branch pom.xml";
        try {
            File pom = new File(path.getAbsolutePath() + "/pom.xml");
            MavenPomXml pomXml = new MavenPomXml(pom);
            if (pomXml.isPropertyPresent("core.version")) {
                // we should take core.version from tag and adapt it (f.e., 310-6 becomes 310-SNAPSHOT)
                String tagCoreVersion = pomXml.getPropertyValue("core.version");
                if (tagCoreVersion.contains("-")) {
                    String[] arr = tagCoreVersion.split("-");
                    tagCoreVersion = arr[0];
                    for (int i = 1; i < arr.length - 1; i++)
                        tagCoreVersion += ("-" + arr[i]);
                }
                MavenVersion mvn = MavenVersion.fromString(tagCoreVersion);
                pomXml.setPropertyValue("core.version", mvn.getCurrentSnapshotVersion());
                MavenPomXml.writeXmlDocument(pomXml.getXmlDocument(), pom);
            }
        } catch (JDOMException e) {
            getLog().error(failMessage);
        } catch (IOException e) {
            getLog().error(failMessage);
        }
    }
}