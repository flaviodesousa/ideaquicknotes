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

import com.jsrana.plugins.quicknotes.ui.QuickNotesPanel;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Quick Notes Panel
 *
 * @author Jitendra Rana
 */
public class QuickNotesManager {
    private HashMap<String, QuickNotesPanel> panelMap;
    private int index = 0;
    private static QuickNotesManager instance = new QuickNotesManager();
    public static boolean devmode = false;

    private boolean showLineNumbers = true;
    private Font notesFont = new Font( "Arial", Font.PLAIN, 12 );
    private int toolbarLocation = TOOLBARLOCATION_BOTTOM;

    public static final int TOOLBARLOCATION_BOTTOM = 0;
    public static final int TOOLBARLOCATION_TOP = 1;

    /**
     * Do not instantiate QuickNotesManager.
     */
    private QuickNotesManager() {
        panelMap = new HashMap<String, QuickNotesPanel>();
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
     * @param panel
     */
    public void addQuickNotesPanel( QuickNotesPanel panel ) {
        panelMap.put( panel.getId(), panel );
    }

    /**
     *
     */
    public void setNoteEditWarning() {
        HashMap<String, ArrayList<QuickNotesPanel>> map = new HashMap<String, ArrayList<QuickNotesPanel>>();
        for ( Object o : panelMap.keySet() ) {
            QuickNotesPanel panel = panelMap.get( o );
            String index = String.valueOf( panel.getSelectedNoteIndex() );
            if ( !map.containsKey( index ) ) {
                map.put( index, new ArrayList<QuickNotesPanel>() );
            }
            ( map.get( index ) ).add( panel );
        }

        for ( String key : map.keySet() ) {
            List<QuickNotesPanel> list = map.get( key );
            if ( list.size() > 1 ) {
                for ( QuickNotesPanel aList : list ) {
                    ( aList ).setWarning( true );
                }
            }
            else if ( list.size() == 1 ) {
                ( list.get( 0 ) ).setWarning( false );
            }
        }
        map.clear();
    }

    /**
     * @param panelid
     */
    public void syncQuickNotePanels( String panelid ) {
        if ( panelid != null ) {
            for ( String id : panelMap.keySet() ) {
                if ( id != null && !panelid.equals( id ) ) {
                    QuickNotesPanel qnp = panelMap.get( id );
                    int index = qnp.getSelectedNoteIndex();
                    if ( index == qnp.element.getChildren().size() ) {
                        index--;
                    }
                    qnp.selectNote( index, false );
                }
            }
        }
    }

    /**
     * Getter for property 'nextPanelID'.
     *
     * @return Value for property 'nextPanelID'.
     */
    public String getNextPanelID() {
        return "panel_" + index++;
    }

    /**
     * @param panel
     */
    public void clearLocks( QuickNotesPanel panel ) {
        panelMap.remove( panel.getId() );
        setNoteEditWarning();
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
                QuickNotesManager mgr = QuickNotesManager.getInstance();
                element.setAttribute( "showlinenumbers", mgr.isShowLineNumbers() ? "Y" : "N" );
                element.setAttribute( "toolbarlocation", String.valueOf( mgr.getToolbarLocation() ) );

                Font font = mgr.getNotesFont();
                element.setAttribute( "fontname", font.getFontName() );
                element.setAttribute( "fontsize", String.valueOf( font.getSize() ) );

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

    /**
     * @param panelid
     */
    public void syncNoteText( String panelid ) {
        QuickNotesPanel panel = panelMap.get( panelid );
        for ( Object o : panelMap.keySet() ) {
            String id = ( String ) o;
            if ( id != null && !panelid.equals( id ) ) {
                QuickNotesPanel qnp = panelMap.get( id );
                if ( qnp.getSelectedNoteIndex() == panel.getSelectedNoteIndex() ) {
                    qnp.setText( panel.getText() );
                }
            }
        }
    }

    public boolean isShowLineNumbers() {
        return showLineNumbers;
    }

    public void setShowLineNumbers( boolean showLineNumbers ) {
        this.showLineNumbers = showLineNumbers;
        for ( String id : panelMap.keySet() ) {
            if ( id != null ) {
                QuickNotesPanel qnp = panelMap.get( id );
                qnp.getTextArea().repaint();
            }
        }
    }

    public Font getNotesFont() {
        return notesFont;
    }

    public void setNotesFont( Font notesFont ) {
        this.notesFont = notesFont;
        for ( String id : panelMap.keySet() ) {
            if ( id != null ) {
                QuickNotesPanel qnp = panelMap.get( id );
                qnp.setNotesFont( notesFont );
            }
        }
    }

    public void setToolBarLocation( int location ) {
        toolbarLocation = location;
        for ( String id : panelMap.keySet() ) {
            if ( id != null ) {
                QuickNotesPanel qnp = panelMap.get( id );
                qnp.setToolbarLocation( location );
            }
        }
    }

    public int getToolbarLocation() {
        return toolbarLocation;
    }
}
