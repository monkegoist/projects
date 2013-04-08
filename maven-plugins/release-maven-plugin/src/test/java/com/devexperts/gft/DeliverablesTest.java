package com.devexperts.gft;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DeliverablesTest {

    private final Deliverables obj = new Deliverables();

    @Test(expected = MojoExecutionException.class)
    public void testAbsolutelyInvalidPath() throws MojoExecutionException {
        obj.setDeliverablesPath("C:\\Windows\\system32");
        obj.getSystemSpecificPath();
    }

    @Test
    public void testGetSystemSpecificPath_WinValid() throws MojoExecutionException {
        System.setProperty("os.name", "Windows 7");
        obj.setDeliverablesPath("//jazz/GFT/Deliverables/srv");
        assertEquals("//jazz/GFT/Deliverables/srv", obj.getSystemSpecificPath());
    }

    @Test
    public void testGetSystemSpecificPath_UnixValid() throws MojoExecutionException {
        System.setProperty("os.name", "Linux x64");
        obj.setDeliverablesPath("//jazz.in.devexperts.com/GFT/Deliverables/srv");
        assertEquals("/mnt/GFT/Deliverables/srv", obj.getSystemSpecificPath());
    }
}