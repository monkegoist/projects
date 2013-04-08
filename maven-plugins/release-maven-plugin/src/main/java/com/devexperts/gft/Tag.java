package com.devexperts.gft;

import com.devexperts.gft.utils.scm.ScmException;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;

/**
 * Author: Andrew Logvinov
 * Date: 10/1/11
 * Time: 8:43 PM
 * <p/>
 * This class contains code that makes tag in SCM system.
 *
 * @description This goal makes tag in SCM system.
 * @goal make-tag
 * @requiresOnline true
 */
public final class Tag extends AbstractDevexMojo {

    /**
     * Path to working copy.
     *
     * @parameter alias="bundle.basedir.path" property="bundle.basedir.path"
     * @required
     */
    private File workingCopyPath;

    @Override
    public void execute() throws MojoExecutionException {

        checkVersions();

        final String tagName = getTagPrefix() + getMavenVersion().getCurrentReleaseVersion();
        final String commitMessage = "Tag " + tagName + " created";
        final String tagPath = getScmUrl() + "tags/" + tagName;

        getLog().info("Path to working copy: " + workingCopyPath.getAbsolutePath());
        getLog().info("Tag to be created: " + tagName);
        getLog().info("Path to tag in repository: " + tagPath);
        getLog().info("Creating tag in repository...");

        try {
            String status = getScmProvider().copy(workingCopyPath, tagPath, commitMessage);
            getLog().info("Status: " + status);
        } catch (ScmException e) {
            final String message = "Failed to create tag in repository";
            fail(message, e);
        }
    }
}