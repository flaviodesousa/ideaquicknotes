package com.jsrana.plugins.quicknotes.ui;

import com.jsrana.plugins.quicknotes.manager.QuickNotesManager;
import com.jsrana.plugins.quicknotes.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class OptionsDialog
        extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JComboBox comboBoxFont;
    private JComboBox comboBoxFontSize;
    private JComboBox comboBoxLocation;
    private JLabel showLineNumberLabel;
    private JButton urlButton;
    private JButton licenseButton;
    private JLabel labelWebsite;
    private JLabel labelSource;

    protected String fontSizes[] = {"8", "10", "11", "12", "14", "16", "18", "20", "24"};
    private boolean showLineNumber;

    public OptionsDialog() {
        super();
        setContentPane( contentPane );
        setModal( true );
        getRootPane().setDefaultButton( buttonOK );
        setTitle( "Quick Notes Options" );

        buttonOK.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                onOK();
            }
        } );

        setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
        addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                onCancel();
            }
        } );

        contentPane.registerKeyboardAction( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );

        final QuickNotesManager manager = QuickNotesManager.getInstance();
        String currentFontName = manager.getNotesFont().getFontName();
        String currentFontSize = String.valueOf( manager.getNotesFont().getSize() );
        String[] fontList = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for ( String aFontList : fontList ) {
            comboBoxFont.addItem( aFontList );
        }
        comboBoxFont.setSelectedItem( currentFontName );

        for ( String fontSize : fontSizes ) {
            comboBoxFontSize.addItem( fontSize );
        }
        comboBoxFontSize.setSelectedItem( currentFontSize );

        comboBoxFont.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                manager.setNotesFont( new Font( String.valueOf( comboBoxFont.getSelectedItem() ), Font.PLAIN, Integer.parseInt( String.valueOf( comboBoxFontSize.getSelectedItem() ) ) ) );
            }
        } );
        comboBoxFontSize.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                manager.setNotesFont( new Font( String.valueOf( comboBoxFont.getSelectedItem() ), Font.PLAIN, Integer.parseInt( String.valueOf( comboBoxFontSize.getSelectedItem() ) ) ) );
            }
        } );

        comboBoxLocation.addItem( "Top" );
        comboBoxLocation.addItem( "Bottom" );
        comboBoxLocation.setSelectedItem( manager.getToolbarLocation() == QuickNotesManager.TOOLBARLOCATION_BOTTOM ? "Bottom" : "Top" );
        comboBoxLocation.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                manager.setToolBarLocation( "Top".equals( comboBoxLocation.getSelectedItem() ) ? QuickNotesManager.TOOLBARLOCATION_TOP : QuickNotesManager.TOOLBARLOCATION_BOTTOM );
            }
        } );

        showLineNumber = manager.isShowLineNumbers();
        showLineNumberLabel.setIcon( showLineNumber ? Utils.ICON_ON : Utils.ICON_OFF );
        showLineNumberLabel.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        showLineNumberLabel.addMouseListener( new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                showLineNumber = !showLineNumber;
                manager.setShowLineNumbers( showLineNumber );
                showLineNumberLabel.setIcon( showLineNumber ? Utils.ICON_ON : Utils.ICON_OFF );
            }
        } );

        urlButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Utils.openURL( "http://plugins.intellij.net/plugin/?id=4456" );
            }
        });

        licenseButton.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        licenseButton.addActionListener( new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                LicenseDialog dialog = new LicenseDialog();
                dialog.setLocationRelativeTo( null );
                dialog.pack();
                dialog.setVisible( true );
            }
        } );

        labelWebsite.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        labelWebsite.addMouseListener( new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                Utils.openURL( "http://www.jsrana.com/home/idea/quicknotes" );
            }
        });

        labelSource.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        labelSource.addMouseListener( new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                Utils.openURL( "https://code.google.com/p/ideaquicknotes/" );
            }
        });
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        dispose();
    }
}
