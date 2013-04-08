package com.devexperts.gft;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;

/**
 * Author: Andrew Logvinov
 * Date: 9/25/11
 * Time: 5:07 PM
 * <p/>
 * This class contains code that copies deliverables to the specified path.
 *
 * @description This goal copies deliverables to the specified path.
 * @goal copy-deliverables
 * @requiresOnline true
 */
public final class Deliverables extends AbstractDevexMojo {

    /**
     * Directory to copy files to.
     *
     * @parameter alias="bundle.deliverables.path" property="bundle.deliverables.path"
     * @required
     */
    private String deliverablesPath;

    /**
     * List of files to copy:
     * <p/>
     * {@code
     * <files>
     * <file>dir1/dir2/file1</file>
     * <file>dir3/file2</file>
     * <file>../file3</file>
     * </files>}
     *
     * @parameter
     * @required
     */
    private File[] files;

    @Override
    public void execute() throws MojoExecutionException {

        checkVersions();

        getLog().info("Path to deliverables in pom.xml: " + deliverablesPath);

        final File path = new File(getSystemSpecificPath());
        getLog().info("System-specific path to deliverables: " + path.getAbsolutePath());

        for (File curFile : files) {
            try {
                getLog().info("Copying file " + curFile.getPath());
                FileUtils.copyFileToDirectory(curFile, path);
                getLog().info("Ok");
            } catch (IOException e) {
                final String message = "Failed to copy file " + curFile.getAbsolutePath();
                fail(message, e);
            }
        }
    }

    // --------------- Visible for testing only ---------------

    /**
     * @return String
     * @throws MojoExecutionException
     */
    String getSystemSpecificPath() throws MojoExecutionException {
        if (!deliverablesPath.startsWith("//jazz")) {
            final String message = "Incorrect path to deliverables, should be starting with //jazz";
            fail(message);
        }
        String os = System.getProperty("os.name");
        return (os.toLowerCase().contains("win")) ? deliverablesPath : deliverablesPath.replaceFirst("/jazz(\\.[a-z]+)*", "mnt");
    }

    void setDeliverablesPath(String newPath) {
        this.deliverablesPath = newPath;
    }
}