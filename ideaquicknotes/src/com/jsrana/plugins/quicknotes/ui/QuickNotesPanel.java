package com.jsrana.plugins.quicknotes.ui;

import com.jsrana.plugins.quicknotes.manager.QuickNotesManager;
import com.jsrana.plugins.quicknotes.renderer.NoteListRenderer;
import org.jdom.Element;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Quick Notes Panel
 *
 * @author Jitendra Rana
 */
public class QuickNotesPanel {
    private String id;
    private JPanel panel1;
    private JTextArea textArea1;
    private JLabel notestitle;
    private JLabel indexLabel;
    private JLabel addedon;
    private JLabel logo;
    private JPanel body;
    private JScrollPane noteScroller;
    private JToolBar toolbar;
    private JButton buttonAdd;
    private JButton buttonBack;
    private JButton buttonNext;
    private JButton buttonTrash;
    private JButton buttonSave;
    private JToggleButton buttonList;
    private JToggleButton buttonLine;
    private JLabel labelLocked;
    private JButton buttonRename;
    public Element element;
    private int selectedIndex;
    private Element selectedNote;
    private QuickNotesManager quickNotesManager;
    private JPopupMenu popupMenu;
    private Hashtable commands;

    public static final Border BORDER_BUTTON = BorderFactory.createEmptyBorder( 3, 3, 3, 3 );
    public static final Border BORDER_BUTTON_HOVER = BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( Color.gray ), BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) );
    private final ImageIcon icon_note = new ImageIcon( getClass().getClassLoader().getResource( "resources/quicknotes32.png" ) );
    private final ImageIcon icon_list = new ImageIcon( getClass().getClassLoader().getResource( "resources/list.png" ) );
    private final ImageIcon icon_list16 = new ImageIcon( getClass().getClassLoader().getResource( "resources/list_16.png" ) );
    private final ImageIcon icon_list16_selected = new ImageIcon( getClass().getClassLoader().getResource( "resources/list_16_selected.png" ) );
    private final ImageIcon icon_forward = new ImageIcon( getClass().getClassLoader().getResource( "resources/forward.png" ) );
    private final ImageIcon icon_forward_gray = new ImageIcon( getClass().getClassLoader().getResource( "resources/forward_gray.png" ) );
    private final ImageIcon icon_back = new ImageIcon( getClass().getClassLoader().getResource( "resources/back.png" ) );
    private final ImageIcon icon_back_gray = new ImageIcon( getClass().getClassLoader().getResource( "resources/back_gray.png" ) );
    private final ImageIcon icon_cut = new ImageIcon( getClass().getClassLoader().getResource( "resources/editcut.png" ) );
    private final ImageIcon icon_copy = new ImageIcon( getClass().getClassLoader().getResource( "resources/editcopy.png" ) );
    private final ImageIcon icon_paste = new ImageIcon( getClass().getClassLoader().getResource( "resources/editpaste.png" ) );
    private final ImageIcon icon_delete = new ImageIcon( getClass().getClassLoader().getResource( "resources/editdelete.png" ) );

    private static final Color EDITOR_COLOR_BACKGROUND = new Color( 254, 252, 178 );
    private static final Color EDITOR_COLOR_LINE = new Color( 234, 233, 164 );
    private static final Color EDITOR_COLOR_LINENUMBER = new Color( 189, 183, 107 );
    private static final Insets EDITOR_INSET = new Insets( 0, 25, 0, 0 );
    private static final Insets EDITOR_INSET_LINENUMBER = new Insets( 0, 35, 0, 0 );
    private boolean showLineNumbers = true;
    public static SimpleDateFormat sdf = new SimpleDateFormat( "EEE, d MMM yyyy h:mm a" );

    /**
     * @param element
     */
    public QuickNotesPanel( final Element element ) {
        quickNotesManager = QuickNotesManager.getInstance();
        id = quickNotesManager.getNextPanelID();
        this.element = element;

        buttonAdd.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        buttonAdd.addActionListener( new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                element.addContent( addNewNote() );
                selectNote( element.getChildren().size() - 1 );
            }
        } );

        buttonBack.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        buttonBack.addActionListener( new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                goBack();
            }
        } );

        buttonTrash.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        buttonTrash.addActionListener( new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                deleteNote();
            }
        } );

        buttonNext.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        buttonNext.addActionListener( new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                goNext();
            }
        } );

        buttonRename.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        buttonRename.addActionListener( new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                renameNote();
            }
        } );

        buttonSave.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        buttonSave.addActionListener( new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                saveNote();
            }
        } );

        buttonList.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        buttonList.addActionListener( new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                if ( buttonList.getIcon() == icon_list16_selected ) {
                    selectNote( getSelectedNoteIndex() );
                }
                else {
                    listAllNotes();
                }
            }
        } );

        buttonLine.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        buttonLine.addActionListener( new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                showLineNumbers = buttonLine.isSelected();
                textArea1.repaint();
                textArea1.setMargin( showLineNumbers ? EDITOR_INSET_LINENUMBER : EDITOR_INSET );
                element.setAttribute( "showlinenumbers", showLineNumbers ? "Y" : "N" );
            }
        } );


        textArea1.addKeyListener( new KeyAdapter() {
            @Override public void keyReleased( KeyEvent e ) {
                if ( e.getKeyCode() == 27 ) {
                    hidePopupMenu();
                }
                else {
                    hidePopupMenu();
                    getSelectedNote().setText( textArea1.getText() );
                }
            }
        } );

        textArea1.addMouseListener( new MouseAdapter() {
            @Override public void mousePressed( MouseEvent e ) {
                hidePopupMenu();
                if ( e.getButton() == 3 ) {
                    showPopupMenu();
                }
            }
        } );

        try {
            showLineNumbers = !"N".equals( element.getAttributeValue( "showlinenumbers" ) );
        }
        catch ( NumberFormatException e ) {
            showLineNumbers = true;
        }
        buttonLine.setSelected( showLineNumbers );
        textArea1.setMargin( showLineNumbers ? EDITOR_INSET_LINENUMBER : EDITOR_INSET );

        selectedIndex = 0;
        try {
            selectedIndex = Integer.parseInt( element.getAttributeValue( "selectednoteindex" ) );
        }
        catch ( NumberFormatException e ) {
            selectedIndex = 0;
        }
        selectNote( selectedIndex );
    }

    /**
     * Setter for property 'notes'.
     *
     * @param notes Value to set for property 'notes'.
     */
    public void setNotes( String notes ) {
        getSelectedNote().setText( notes );
    }

    /**
     * Setter for property 'selectedNoteIndex'.
     *
     * @param index Value to set for property 'selectedNoteIndex'.
     */
    private void setSelectedNoteIndex( int index ) {
        selectedIndex = index;
        element.setAttribute( "selectednoteindex", String.valueOf( index ) );
    }

    /**
     * Getter for property 'selectedNoteIndex'.
     *
     * @return Value for property 'selectedNoteIndex'.
     */
    private int getSelectedNoteIndex() {
        return selectedIndex;
    }

    /**
     * Getter for property 'selectedNote'.
     *
     * @return Value for property 'selectedNote'.
     */
    public Element getSelectedNote() {
        return selectedNote;
    }

    /**
     * @return
     */
    public boolean hasMoreNotes() {
        return getSelectedNoteIndex() < element.getChildren().size() - 1;
    }

    /**
     * Getter for property 'rootComponent'.
     *
     * @return Value for property 'rootComponent'.
     */
    public JComponent getRootComponent() {
        return panel1;
    }

    /**
     * Selects the previous Note
     */
    private void goBack() {
        if ( getSelectedNoteIndex() > 0 ) {
            selectNote( getSelectedNoteIndex() - 1 );
        }
    }

    /**
     * Selects the next Note
     */
    private void goNext() {
        if ( getSelectedNoteIndex() < element.getChildren().size() - 1 ) {
            selectNote( getSelectedNoteIndex() + 1 );
        }
    }

    /**
     * Selects a note
     *
     * @param index Index of the Note in Note collection
     */
    private void selectNote( int index ) {
        if ( textArea1.getParent() == null ) {
            buttonRename.setVisible( true );
            buttonAdd.setEnabled( true );
            buttonSave.setEnabled( true );
            logo.setIcon( icon_note );
            buttonList.setIcon( icon_list16 );
            noteScroller.getViewport().add( textArea1 );
        }

        if ( quickNotesManager.lockNote( id, index ) ) {
            textArea1.setEditable( true );
            labelLocked.setText( "" );
            labelLocked.setToolTipText( "" );
            buttonTrash.setEnabled( true );
            buttonRename.setVisible( true );
        }
        else {
            textArea1.setEditable( false );
            labelLocked.setText( "Locked" );
            labelLocked.setToolTipText( "This note has been locked by another instance of IDEA" );
            buttonTrash.setEnabled( false );
            buttonRename.setVisible( false );
        }

        if ( index >= 0 && index < element.getChildren().size() ) {
            setSelectedNoteIndex( index );
            selectedNote = ( Element ) element.getChildren().get( index );
            textArea1.setText( selectedNote.getText() );
            textArea1.setCaretPosition( 0 );
            notestitle.setText( selectedNote.getAttributeValue( "title" ) );
            addedon.setText( "(Added on " + selectedNote.getAttributeValue( "createdt" ) + ")" );
            indexLabel.setText( ( index + 1 ) + " of " + element.getChildren().size() );
            buttonBack.setEnabled( index > 0 );
            buttonNext.setEnabled( hasMoreNotes() );
            if ( buttonTrash.isEnabled() ) {
                buttonTrash.setEnabled( element.getChildren().size() > 1 );
            }
        }
        buttonList.setSelected( false );
    }

    /**
     * Deletes the current Note
     */
    private void deleteNote() {
        if ( element.getChildren().size() > 1 ) {
            if ( JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog( panel1, "Are you sure you want to delete this Note?",
                                                                          "Confirm Note Delete", JOptionPane.YES_NO_OPTION ) ) {
                if ( getSelectedNoteIndex() >= 0 && getSelectedNoteIndex() < element.getChildren().size() ) {
                    Element note = getSelectedNote();
                    element.removeContent( note );
                    if ( getSelectedNoteIndex() > 0 ) {
                        setSelectedNoteIndex( getSelectedNoteIndex() - 1 );
                    }
                    selectNote( getSelectedNoteIndex() );
                }
            }
        }
    }

    /**
     * Saves all Notes to a File
     */
    private void saveNote() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.showSaveDialog( panel1 );
        File file = fileChooser.getSelectedFile();
        if ( file != null ) {
            try {
                getSelectedNote().setText( textArea1.getText() );
                file.createNewFile();
                StringBuffer sb = new StringBuffer();
                FileWriter fileWriter = new FileWriter( file );
                java.util.List list = element.getChildren();
                for ( int i = 0; i < list.size(); i++ ) {
                    Element e = ( Element ) list.get( i );
                    sb.append( e.getAttributeValue( "title" ) );
                    sb.append( " (Added on " ).append( e.getAttributeValue( "createdt" ) ).append( ")" );
                    sb.append( "\n-------------------------------------------------------------------\n" );
                    sb.append( e.getText() );
                    sb.append( "\n\n" );
                }
                fileWriter.write( sb.toString() );
                fileWriter.flush();
                fileWriter.close();
                JOptionPane.showMessageDialog( panel1, "Notes have been saved successfully to file\n\n" + file.getPath() + "\n ",
                                               "File saved", JOptionPane.INFORMATION_MESSAGE );
            }
            catch ( IOException e ) {
                JOptionPane.showMessageDialog( panel1, "Unable to create file. Please make sure you have write access to the folder.",
                                               "File creation failure", JOptionPane.ERROR_MESSAGE );
            }
        }
    }

    /**
     * Renames the current Note
     */
    private void renameNote() {
        String notetitle = getSelectedNote().getAttributeValue( "title" );
        String title = JOptionPane.showInputDialog( panel1, "Please enter title for this Note", notetitle );
        if ( title != null && title.length() > 0 && !title.equals( notetitle ) ) {
            getSelectedNote().setAttribute( "title", title );
            notestitle.setText( title );
        }
    }

    /**
     * Lists all notes
     */
    private void listAllNotes() {
        java.util.List list = element.getChildren();
        Vector<Object> o = new Vector<Object>();
        int i = 0;
        while ( i < list.size() ) {
            o.add( list.get( i ) );
            i++;
        }
        final JList sList = new JList( o );
        sList.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        sList.setCellRenderer( new NoteListRenderer() );
        sList.addMouseMotionListener( new MouseAdapter() {
            @Override public void mouseMoved( MouseEvent e ) {
                sList.setSelectedIndex( sList.locationToIndex( e.getPoint() ) );
            }
        } );
        sList.addMouseListener( new MouseAdapter() {
            @Override public void mouseClicked( MouseEvent e ) {
                int index = sList.locationToIndex( e.getPoint() );
                selectNote( index );
            }
        } );
        noteScroller.getViewport().add( sList );

        notestitle.setText( "List of all Notes" );
        addedon.setText( "(Click on a Note to select it)" );
        buttonBack.setEnabled( false );
        buttonNext.setEnabled( false );
        buttonTrash.setEnabled( false );
        buttonAdd.setEnabled( false );
        buttonSave.setEnabled( false );
        buttonList.setIcon( icon_list16_selected );
        buttonRename.setVisible( false );
        logo.setIcon( icon_list );
    }

    /**
     * Adds a new Note Element to Root element
     *
     * @return Added Note Element
     */
    public static Element addNewNote() {
        Element note = new Element( "note" );
        note.setAttribute( "title", "New Note" );
        note.setAttribute( "createdt", sdf.format( new Date() ) );
        note.setText( "Enter your notes here..." );
        return note;
    }

    /**
     * Creates Custom UI Component for Text Area
     */
    private void createUIComponents() {
        textArea1 = new JTextArea() {
            {
                setOpaque( false );
            }

            public void paint( Graphics g ) {
                g.setColor( EDITOR_COLOR_BACKGROUND );
                g.fillRect( 0, 0, getWidth(), getHeight() );
                Rectangle clip = g.getClipBounds();
                FontMetrics fm = g.getFontMetrics( getFont() );
                Insets insets = getInsets();
                int fontHeignt = fm.getHeight();
                int y = fm.getAscent() + insets.top;
                int startLineNumber = ( ( clip.y + insets.top ) / fontHeignt ) + 1;
                if ( y < clip.y ) {
                    y = startLineNumber * fontHeignt - ( fontHeignt - fm.getAscent() );
                }
                int yend = y + clip.height + fontHeignt;
                while ( y < yend ) {
                    if ( showLineNumbers && startLineNumber <= getLineCount() ) {
                        g.setColor( EDITOR_COLOR_LINENUMBER );
                        g.drawString( startLineNumber++ + ".", 2, y );
                    }
                    g.setColor( EDITOR_COLOR_LINE );
                    g.drawLine( 0, y + 2, getWidth(), y + 2 );
                    y += fontHeignt;
                }
                g.setColor( EDITOR_COLOR_LINENUMBER );
                if ( showLineNumbers ) {
                    g.drawLine( 30, 0, 30, getHeight() );
                    g.drawLine( 32, 0, 32, getHeight() );
                }
                else {
                    g.drawLine( 20, 0, 20, getHeight() );
                    g.drawLine( 22, 0, 22, getHeight() );
                }
                super.paint( g );
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        QuickNotesPanel that = ( QuickNotesPanel ) o;
        return id.equals( that.id );
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Getter for property 'id'.
     *
     * @return Value for property 'id'.
     */
    public String getId() {
        return id;
    }

    /**
     *
     */
    public void hidePopupMenu() {
        if ( popupMenu != null ) {
            popupMenu.setVisible( false );
            popupMenu = null;
        }
    }

    /**
     * Shows the popup menu in Text Area
     */
    public void showPopupMenu() {
        hidePopupMenu();
        popupMenu = new JPopupMenu();

        JMenuItem cut = new JMenuItem();
        cut.setAction( findAction( DefaultEditorKit.cutAction ) );
        cut.setText( "Cut" );
        cut.setIcon( icon_cut );
        cut.addMouseListener( new MouseAdapter() {
            @Override public void mouseClicked( MouseEvent e ) {
                hidePopupMenu();
                getSelectedNote().setText( textArea1.getText() );
            }
        } );

        JMenuItem copy = new JMenuItem();
        copy.setAction( findAction( DefaultEditorKit.copyAction ) );
        copy.setText( "Copy" );
        copy.setIcon( icon_copy );
        copy.addMouseListener( new MouseAdapter() {
            @Override public void mouseClicked( MouseEvent e ) {
                hidePopupMenu();
            }
        } );

        JMenuItem paste = new JMenuItem();
        paste.setAction( findAction( DefaultEditorKit.pasteAction ) );
        paste.setText( "Paste" );
        paste.setIcon( icon_paste );
        paste.addMouseListener( new MouseAdapter() {
            @Override public void mouseClicked( MouseEvent e ) {
                hidePopupMenu();
                getSelectedNote().setText( textArea1.getText() );
            }
        } );

        JMenuItem next = new JMenuItem( "Next Note", icon_forward );
        next.setDisabledIcon( icon_forward_gray );
        final boolean nextFlag = hasMoreNotes();
        next.addMouseListener( new MouseAdapter() {
            @Override public void mouseClicked( MouseEvent e ) {
                hidePopupMenu();
                if ( nextFlag ) {
                    goNext();
                }
            }
        } );
        next.setEnabled( nextFlag );

        JMenuItem back = new JMenuItem( "Previous Note", icon_back );
        back.setDisabledIcon( icon_back_gray );
        back.addMouseListener( new MouseAdapter() {
            @Override public void mouseClicked( MouseEvent e ) {
                hidePopupMenu();
                if ( getSelectedNoteIndex() > 0 ) {
                    goBack();
                }
            }
        } );
        back.setEnabled( getSelectedNoteIndex() > 0 );

        JMenuItem delete = new JMenuItem( "Delete Note", icon_delete );
        delete.addMouseListener( new MouseAdapter() {
            @Override public void mouseClicked( MouseEvent e ) {
                hidePopupMenu();
                deleteNote();
            }
        } );

        popupMenu.add( cut );
        popupMenu.add( copy );
        popupMenu.add( paste );
        popupMenu.addSeparator();
        popupMenu.add( next );
        popupMenu.add( back );
        popupMenu.addSeparator();
        popupMenu.add( delete );

        for ( int i=0; i<popupMenu.getComponentCount(); i++ ) {
            Object o = popupMenu.getComponent( i );
            if ( o instanceof JMenuItem ) {
                JMenuItem item = (JMenuItem) o;
                item.setHorizontalAlignment( JMenuItem.LEFT );
                item.setMargin( new Insets( 2, 2, 2, 2 ) );
                item.setBackground( Color.WHITE );
                item.setIconTextGap( 5 );

                if ( item.isEnabled() ) {
                    item.addMouseListener( new MouseAdapter() {
                        @Override public void mouseEntered( MouseEvent e ) {
                            ( ( JMenuItem ) e.getSource() ).setBackground( Color.LIGHT_GRAY );
                        }

                        @Override public void mouseExited( MouseEvent e ) {
                            ( ( JMenuItem ) e.getSource() ).setBackground( Color.WHITE );
                        }
                    } );
                }
                else {
                    item.setForeground( Color.GRAY );
                }
            }
        }
        popupMenu.pack();
        popupMenu.setLocation( MouseInfo.getPointerInfo().getLocation() );
        popupMenu.setVisible( true );
    }

    /**
     * @param key
     * @return
     */
    private Action findAction( String key ) {
        if ( commands == null ) {
            commands = new Hashtable();
            Action actions[] = textArea1.getActions();
            for ( int i = 0; i < actions.length; i++ ) {
                Action action = actions[i];
                commands.put( action.getValue( Action.NAME ), action );
            }
        }
        return ( Action ) commands.get( key );
    }
}
