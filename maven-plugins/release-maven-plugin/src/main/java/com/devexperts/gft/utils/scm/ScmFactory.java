package com.devexperts.gft.utils.scm;

/**
 * Author: Andrew Logvinov
 * Date: 10/1/11
 * Time: 2:54 PM
 */
public abstract class ScmFactory {

    public static ScmProvider getSvnImpl() {
        return new SvnImpl();
    }
}