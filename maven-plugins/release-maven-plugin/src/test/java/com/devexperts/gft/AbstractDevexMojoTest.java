package com.devexperts.gft;

import com.devexperts.gft.utils.scm.SvnImpl;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractDevexMojoTest {

    @Test(expected = MojoExecutionException.class)
    public void testCheckMojoConfiguration_BundleVersionSnapshot() throws MojoExecutionException {
        AbstractDevexMojo mojo = mock(AbstractDevexMojo.class);
        when(mojo.getProjectVersion()).thenReturn("123-SNAPSHOT");
        doCallRealMethod().when(mojo).checkVersions();
        doCallRealMethod().when(mojo).fail(any(String.class));
        when(mojo.getLog()).thenReturn(mock(Log.class));
        mojo.checkVersions();
    }

    @Test(expected = MojoExecutionException.class)
    public void testCheckMojoConfiguration_CoreVersionSnapshot() throws MojoExecutionException {
        AbstractDevexMojo mojo = mock(AbstractDevexMojo.class);
        when(mojo.isBundleCoreDependent()).thenReturn(true);
        when(mojo.getProjectVersion()).thenReturn("123-12-1");
        when(mojo.getCoreVersion()).thenReturn("303-SNAPSHOT");
        doCallRealMethod().when(mojo).checkVersions();
        doCallRealMethod().when(mojo).fail(any(String.class));
        when(mojo.getLog()).thenReturn(mock(Log.class));
        mojo.checkVersions();
    }

    @Test
    public void testGetBundleScmUrl_ValidUrl() throws MojoExecutionException {
        TestMojo obj = new TestMojo();
        obj.setScmUrl("scm:svn:https://localhost/trunk");
        assertEquals("https://localhost/trunk/", obj.getScmUrl());
    }

    @Test
    public void testGetScmProvider_ValidProvider() throws MojoExecutionException {
        TestMojo obj = new TestMojo();
        obj.setScmUrl("scm:svn:https://localhost/trunk");
        assertTrue(obj.getScmProvider() instanceof SvnImpl);
    }

    @Test(expected = MojoExecutionException.class)
    public void testGetScmProvider_InvalidProvider() throws MojoExecutionException {
        TestMojo obj = new TestMojo();
        obj.setScmUrl("scm:cvs:https://localhost/trunk");
        obj.getScmProvider();
    }

    private static class TestMojo extends AbstractDevexMojo {
        @Override
        public void execute() {
        }
    }
}
