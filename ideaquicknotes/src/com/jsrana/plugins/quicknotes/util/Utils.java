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
package com.jsrana.plugins.quicknotes.util;

import javax.swing.*;
import java.io.File;

public class Utils {
    public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String gif = "gif";
    public final static String tiff = "tiff";
    public final static String tif = "tif";
    public final static String png = "png";

    public final static ImageIcon ICON_NOTE = createImageIcon( "quicknotes32.png" );
    public final static ImageIcon ICON_LIST = createImageIcon( "list.png" );
    public final static ImageIcon ICON_LIST16 = createImageIcon( "list_16.png" );
    public final static ImageIcon ICON_LIST16_SELECTED = createImageIcon( "list_16_selected.png" );
    public final static ImageIcon ICON_FORWARD = createImageIcon( "forward.png" );
    public final static ImageIcon ICON_BACK = createImageIcon( "back.png" );
    public final static ImageIcon ICON_CUT = createImageIcon( "editcut.png" );
    public final static ImageIcon ICON_COPY = createImageIcon( "editcopy.png" );
    public final static ImageIcon ICON_PASTE = createImageIcon( "editpaste.png" );
    public final static ImageIcon ICON_DELETE = createImageIcon( "editdelete.png" );
    public final static ImageIcon ICON_WARNING = createImageIcon( "warning.png" );
    public final static ImageIcon ICON_EXEC = createImageIcon( "exec.png" );
    public final static ImageIcon ICON_ON = createImageIcon( "on.png" );
    public final static ImageIcon ICON_OFF = createImageIcon( "off.png" );

    /*
     * Get the extension of a file.
     */
    public static String getExtension( File f ) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf( '.' );

        if ( i > 0 && i < s.length() - 1 ) {
            ext = s.substring( i + 1 ).toLowerCase();
        }
        return ext;
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     */
    public static ImageIcon createImageIcon( String path ) {
        java.net.URL imgURL = Utils.class.getClassLoader().getResource( "resources/" + path );
        if ( imgURL != null ) {
            return new ImageIcon( imgURL );
        }
        else {
            System.err.println( "Couldn't find file: " + path );
            return null;
        }
    }
}