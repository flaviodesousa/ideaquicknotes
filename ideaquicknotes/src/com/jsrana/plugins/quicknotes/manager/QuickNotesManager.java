/**
 * Copyright 2009 Jitendra Rana, jsrana@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jsrana.plugins.quicknotes.manager;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Quick Notes Panel
 *
 * @author Jitendra Rana
 */
public class QuickNotesManager {
    private int lastLockedNoteIndex;
    private HashMap lockedNoteMap;
    private int index = 0;
    private static QuickNotesManager instance = new QuickNotesManager();
    public static boolean devmode = false;

    /**
     * Do not instantiate QuickNotesManager.
     */
    private QuickNotesManager() {
        lockedNoteMap = new HashMap();
    }

    /**
     * Getter for property 'instance'.
     *
     * @return Value for property 'instance'.
     */
    public static QuickNotesManager getInstance() {
        return instance;
    }

    /**
     * @param id
     * @param index
     * @return
     */
    public boolean lockNote( String id,
                             int index ) {
        if ( lockedNoteMap.containsKey( id ) ) {
            lockedNoteMap.remove( id );
        }
        if ( lockedNoteMap.containsValue( index ) ) {
            return false;
        }
        lockedNoteMap.put( id, index );
        lastLockedNoteIndex = index;
        return true;
    }

    /**
     * Getter for property 'lastLockedNoteIndex'.
     *
     * @return Value for property 'lastLockedNoteIndex'.
     */
    public int getLastLockedNoteIndex() {
        return lastLockedNoteIndex;
    }

    /**
     * Getter for property 'nextPanelID'.
     *
     * @return Value for property 'nextPanelID'.
     */
    public String getNextPanelID() {
        return "panel_" + index++;
    }

    public void clearLocks( String quicknoteid ) {
        lockedNoteMap.remove( quicknoteid );
    }

    /**
     * Returns the settings file. Creates the setting folder and file if not found.
     *
     * @return File
     */
    public static File getSettingsFile() {
        File settingsFile = null;
        String userHome = System.getProperty( "user.home" );
        if ( userHome != null ) {
            File home = new File( userHome );
            File settingsDirectory = new File( home, ".ideaquicknotes" );
            try {
                if ( !settingsDirectory.exists() ) {
                    if ( settingsDirectory.mkdir() ) {
                        settingsFile = new File( settingsDirectory, devmode ? "ideaquicknotes_dev.xml" : "ideaquicknotes.xml" );
                        if ( !settingsFile.exists() ) {
                            settingsFile.createNewFile();
                        }
                    }
                }
                else {
                    settingsFile = new File( settingsDirectory, devmode ? "ideaquicknotes_dev.xml" : "ideaquicknotes.xml" );
                    if ( !settingsFile.exists() ) {
                        settingsFile.createNewFile();
                    }
                }
            }
            catch ( IOException e ) {
                settingsFile = null;
            }
        }
        return settingsFile;
    }

    /**
     * Save the settings to ideaquicknotes.xml
     */
    public static boolean saveSettings( Element element ) {
        // Get an instane of XMLOutputter
        XMLOutputter outputter = new XMLOutputter();
        File settingsFile = getSettingsFile();
        if ( settingsFile != null ) {
            try {
                FileOutputStream fos = new FileOutputStream( settingsFile );
                outputter.setFormat( Format.getPrettyFormat() ); // make it Pretty!!!
                outputter.output( element, fos );
                fos.flush();
                fos.close();
            }
            catch ( IOException e ) {
                return false;
            }
        }
        return true;
    }
}
