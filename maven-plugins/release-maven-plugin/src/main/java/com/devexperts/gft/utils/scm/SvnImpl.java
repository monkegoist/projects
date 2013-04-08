package com.devexperts.gft.utils.scm;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCopySource;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.io.File;

/**
 * Author: Andrew Logvinov
 * Date: 10/1/11
 * Time: 2:51 PM
 */
public class SvnImpl implements ScmProvider {

    private final SVNClientManager manager = SVNClientManager.newInstance();

    @Override
    public long checkout(String url, File dstPath) throws ScmException {
        try {
            return manager.getUpdateClient().doCheckout(
                    // SVNURL url - source URL
                    SVNURL.parseURIDecoded(url),
                    // File dstPath - destination path
                    dstPath,
                    // SVNRevision pegRevision - revision number of the URL
                    SVNRevision.HEAD,
                    // SVNRevision revision - the desired revision of the working copy
                    SVNRevision.HEAD,
                    // SVNDepth depth - tree depth
                    SVNDepth.INFINITY,
                    // boolean allowUnversionedObstructions
                    false);
        } catch (SVNException e) {
            throw new ScmException(e);
        }
    }

    @Override
    public String commit(String message, File workingCopy) throws ScmException {
        try {
            return manager.getCommitClient().doCommit(
                    // File - path to commit
                    new File[]{workingCopy},
                    // boolean - keepLocks
                    false,
                    // String - commitMessage
                    message,
                    // SVNProperties - custom revision properties
                    null,
                    // String[] - changelist names array
                    null,
                    // boolean - keepChangeList
                    false,
                    // boolean - force a non-recursive commit
                    false,
                    // SVNDepth - tree depth to process
                    SVNDepth.INFINITY).toString();
        } catch (SVNException e) {
            throw new ScmException(e);
        }
    }

    @Override
    public String copy(File workingCopy, String repositoryPath, String message) throws ScmException {
        SVNCopySource copySource = new SVNCopySource(
                SVNRevision.WORKING,
                SVNRevision.WORKING,
                workingCopy);
        try {
            return manager.getCopyClient().doCopy(
                    // SVNCopySource[] - sources to copy (only working copy in our case)
                    new SVNCopySource[]{copySource},
                    // SVNURL - destination URL
                    SVNURL.parseURIDecoded(repositoryPath),
                    // boolean - isMove (delete, then add with history)
                    false,
                    // boolean - makeParents (creates any non-existent parent directories)
                    true,
                    // boolean - failWhenDstExists (SVNException is thrown)
                    true,
                    // String - commitMessage
                    message,
                    // SVNProperties - custom revision properties
                    null).toString();
        } catch (SVNException e) {
            throw new ScmException(e);
        }
    }

    @Override
    public String copy(String srcUrl, String dstUrl, String message) throws ScmException {
        try {
            SVNCopySource copySource = new SVNCopySource(
                    SVNRevision.HEAD,
                    SVNRevision.HEAD,
                    SVNURL.parseURIDecoded(srcUrl));
            return manager.getCopyClient().doCopy(
                    // SVNCopySource[] - source to copy
                    new SVNCopySource[]{copySource},
                    // SVNURL - destination URL
                    SVNURL.parseURIDecoded(dstUrl),
                    // boolean - isMove (delete, then add with history)
                    false,
                    // boolean - makeParents (creates any non-existent parent directories)
                    true,
                    // boolean - failWhenDstExists (SVNException is thrown)
                    true,
                    // String - commitMessage
                    message,
                    // SVNProperties - custom revision properties
                    null).toString();
        } catch (SVNException e) {
            throw new ScmException(e);
        }
    }
}