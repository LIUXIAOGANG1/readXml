/*
 * Copyright (c) 2005 National University of Ireland, Galway
 *
 * Licensed under MIT License
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated 
 * documentation files (the "Software"), to deal in the Software 
 * without restriction, including without limitation the rights 
 * to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons 
 * to whom the Software is furnished to do so, subject to the 
 * following conditions:
 *
 * The above copyright notice and this permission notice shall 
 * be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY 
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO 
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES 
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH 
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.sourceforge.powerswing.icons;

import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * @author Mick Kerrigan
 *
 * Created on 30-Mar-2005
 * Committed by $Author$
 *
 * $Source$,
 * @version $Revision$ $Date$
 */

public class DefaultIconRepository implements IconRepository {

    public static final String NEW_ICON = "DefaultIconRepository.NewIcon";
    public static final String OPEN_ICON = "DefaultIconRepository.OpenIcon";
    public static final String SAVE_ICON = "DefaultIconRepository.SaveIcon";
    public static final String SAVEAS_ICON = "DefaultIconRepository.SaveAsIcon";
    public static final String CLOSE_ICON = "DefaultIconRepository.CloseIcon";
    public static final String PREFERENCES_ICON = "DefaultIconRepository.PreferencesIcon";
    public static final String FILTER_ICON = "DefaultIconRepository.FilterIcon";
    public static final String EDIT_ICON = "DefaultIconRepository.EditIcon";
    public static final String DELETE_ICON = "DefaultIconRepository.DeleteIcon";
    public static final String BLANK_ICON = "DefaultIconRepository.BlankIcon";
    
    public static final String ARROW_UP_ICON = "DefaultIconRepository.ArrowUpIcon";
    public static final String ARROW_DOWN_ICON = "DefaultIconRepository.ArrowDownIcon";
    public static final String ARROW_LEFT_ICON = "DefaultIconRepository.ArrowLeftIcon";
    public static final String ARROW_RIGHT_ICON = "DefaultIconRepository.ArrowRightIcon";
    
    private HashMap <String, Icon> icons = new HashMap <String, Icon> ();
    
    public DefaultIconRepository(){
        register(NEW_ICON, loadIcon("new.png"));
        register(OPEN_ICON, loadIcon("open.png"));
        register(SAVE_ICON, loadIcon("save.png"));
        register(SAVEAS_ICON, loadIcon("saveas.png"));
        register(CLOSE_ICON, loadIcon("close.png"));
        register(PREFERENCES_ICON, loadIcon("preferences.gif"));
        register(FILTER_ICON, loadIcon("filter.gif"));
        register(EDIT_ICON, loadIcon("edit.png"));
        register(DELETE_ICON, loadIcon("delete.png"));
        register(BLANK_ICON, loadIcon("blank.png"));
        
        register(ARROW_UP_ICON, loadIcon("arrow_up.gif"));
        register(ARROW_DOWN_ICON, loadIcon("arrow_down.gif"));
        register(ARROW_LEFT_ICON, loadIcon("arrow_left.gif"));
        register(ARROW_RIGHT_ICON, loadIcon("arrow_right.gif"));
    }
    
    private Icon loadIcon(String theIconFilename) {
        return new ImageIcon(DefaultIconRepository.class.getResource(theIconFilename));
    }

    public void register(String theIconId, Icon theIcon) {
        icons.put(theIconId, theIcon);
    }

    public Icon getIcon(String theIconId) {
        return icons.get(theIconId);
    }

    public void deregister(String theIconId) {
        icons.remove(theIconId);
    }
}
