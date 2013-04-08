package com.devexperts.gft.utils.scm;

import java.io.File;

/**
 * Author: Andrew Logvinov
 * Date: 10/1/11
 * Time: 2:50 PM
 */
public interface ScmProvider {

    /**
     * @param url
     * @param dstPath
     * @throws ScmException
     * @return
     */
    public long checkout(String url, File dstPath) throws ScmException;

    /**
     * @param message
     * @param workingCopy
     * @throws ScmException
     * @return
     */
    public String commit(String message, File workingCopy) throws ScmException;

    /**
     * @param workingCopy
     * @param repositoryPath
     * @param message
     * @throws ScmException
     * @return
     */
    public String copy(File workingCopy, String repositoryPath, String message) throws ScmException;

    /**
     * @param srcUrl
     * @param dstUrl
     * @param message
     * @throws ScmException
     * @return
     */
    public String copy(String srcUrl, String dstUrl, String message) throws ScmException;
}