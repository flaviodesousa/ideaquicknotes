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
package com.jsrana.plugins.quicknotes;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerAdapter;
import com.intellij.openapi.util.DefaultJDOMExternalizer;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.peer.PeerFactory;
import com.intellij.ui.content.Content;
import com.jsrana.plugins.quicknotes.manager.QuickNotesManager;
import com.jsrana.plugins.quicknotes.ui.QuickNotesPanel;
import static com.jsrana.plugins.quicknotes.ui.QuickNotesPanel.addNewNote;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.InputSource;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Quick Notes is an IntelliJ IDEA Plugin that provides saving notes in IDEA itself
 *
 * @author Jitendra Rana
 */
public class QuickNotes
        implements ApplicationComponent, JDOMExternalizable {
    public String enotes = "";
    public int selectednoteindex = 0;
    private Element notesElement;

    /**
     * Constructor
     */
    public QuickNotes() {
    }


    /**
     * Unique name of this component. If there is another component with the same name or
     * name is null internal assertion will occur.
     *
     * @return the name of this component
     */
    @NonNls @NotNull public String getComponentName() {
        return "Quick Notes by Jitendra Rana";
    }

    /**
     * Component should do initialization and communication with another components in this method.
     */
    public void initComponent() {
        notesElement = readSettings();
        ProjectManager.getInstance().addProjectManagerListener( new ProjectManagerAdapter() {
            Key quicknoteskey = new Key( "quicknotesid" );

            public void projectOpened( final Project project ) {
                QuickNotesPanel quickNotesPanel = new QuickNotesPanel( notesElement );
                ToolWindowManager twm = ToolWindowManager.getInstance( project );
                ToolWindow toolWindow = twm.registerToolWindow( "Notes", true, ToolWindowAnchor.RIGHT );
                PeerFactory peerFactory = PeerFactory.getInstance();
                Content content = peerFactory.getContentFactory().createContent( quickNotesPanel.getRootComponent(), "", false );
                toolWindow.getContentManager().addContent( content );
                toolWindow.setIcon( new ImageIcon( getClass().getClassLoader().getResource( "resources/quicknotes.png" ) ) );
                project.putUserData( quicknoteskey, quickNotesPanel );
            }

            public void projectClosed( final Project project ) {
                // clear locks
                QuickNotesManager.getInstance().clearLocks( ( QuickNotesPanel ) project.getUserData( quicknoteskey ) );

                // save data
                if ( QuickNotesManager.saveSettings( notesElement ) ) {
                    enotes = "";
                }
                else {
                    enotes = new XMLOutputter().outputString( notesElement );
                }
            }
        } );
    }

    /**
     * Component should dispose system resources or perform another cleanup in this method.
     */
    public void disposeComponent() {
    }


    /**
     * {@inheritDoc}
     */
    public void readExternal( Element element )
            throws InvalidDataException {
        DefaultJDOMExternalizer.readExternal( this, element );
    }

    /**
     * {@inheritDoc}
     */
    public void writeExternal( Element element )
            throws WriteExternalException {
        DefaultJDOMExternalizer.writeExternal( this, element );
    }

    /**
     * This method reads the settings from file ideaquicknotes.xml in user's home directory.
     */
    private Element readSettings() {
        Element element = null;
        File settingsFile = QuickNotesManager.getSettingsFile();
        SAXBuilder builder = new SAXBuilder();
        try {
            if ( settingsFile != null ) {
                // settingsfile is not null, which indicates that reading of setting file is successful and data can be
                // managed in the settings file
                if ( settingsFile.length() == 0 ) {
                    if ( enotes != null && enotes.trim().length() > 0 ) {
                        element = builder.build( new InputSource( new StringReader( enotes ) ) ).getRootElement();
                    }
                }
                else {
                    FileInputStream fis = new FileInputStream( settingsFile );
                    element = builder.build( fis ).getRootElement();
                    fis.close();
                }
            }
            else if ( enotes != null && enotes.length() > 0 ) {
                element = builder.build( new InputSource( new StringReader( enotes ) ) ).getRootElement();
            }
        }
        catch ( Exception e ) {
            // ignore
        }

        if ( element == null ) {
            element = new Element( "notes" );
            element.setAttribute( "createdt", QuickNotesPanel.sdf.format( new Date() ) );
            element.setAttribute( "selectednoteindex", "0" );
            element.setAttribute( "showlinenumbers", "Y" );
        }

        List notes = element.getChildren();
        if ( notes.size() == 0 ) {
            element.addContent( addNewNote() );
        }
        else {
            for ( int i = 0; i < notes.size(); i++ ) {
                Element note = ( Element ) notes.get( i );
                if ( note.getAttributeValue( "title" ) == null ) {
                    note.setAttribute( "title", "New Note" );
                }
                if ( note.getAttributeValue( "createdt" ) == null ) {
                    note.setAttribute( "createdt", SimpleDateFormat.getInstance().format( new Date() ) );
                }
            }
        }
        return element;
    }
}
