/**
 * Copyright 2010 Jitendra Rana, jsrana@gmail.com
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
package com.jsrana.plugins.quicknotes.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * 
 */
public class LicenseDialog
        extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextArea textArea1;

    public LicenseDialog() {
        setContentPane( contentPane );
        setTitle( "License" );
        setModal( true );
        getRootPane().setDefaultButton( buttonOK );

        buttonOK.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {onOK();}
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

        textArea1.setText( "Copyright 2010 Jitendra Rana, jsrana@gmail.com\n" +
                           " \n" +
                           "Licensed under the Apache License, Version 2.0 (the \"License\")\n" +
                           " you may not use this file except in compliance with the License.\n" +
                           " You may obtain a copy of the License at\n" +
                           " \n" +
                           "      http://www.apache.org/licenses/LICENSE-2.0\n" +
                           " \n" +
                           " Unless required by applicable law or agreed to in writing, software\n" +
                           " distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                           " WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                           " See the License for the specific language governing permissions and\n" +
                           " limitations under the License.\n" );
        textArea1.setMargin( new Insets( 2, 2, 2, 2 ) );
        textArea1.setBackground( Color.WHITE );
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        dispose();
    }
}
