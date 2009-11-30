/**
 * Enter description here
 *
 * @author Jitendra Rana
 * @version $Author$
 *          $Source$
 *          $Revision$
 *          $Date$
 *          $State$
 *          $Id$
 */
package com.jsrana.plugins.quicknotes.manager;

public class Manager {
    private static Manager ourInstance = new Manager();

    public static Manager getInstance() {
        return ourInstance;
    }

    private Manager() {
    }
}
