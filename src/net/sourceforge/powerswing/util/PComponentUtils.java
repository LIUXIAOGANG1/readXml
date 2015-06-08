package net.sourceforge.powerswing.util;


import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyListener;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.FocusManager;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;

import net.sourceforge.powerswing.localization.PBundle;

/**
 * @author Niall O Cuilinn
 *
 * Created on Jun 13, 2003
 */
public class PComponentUtils {
    
    private static KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
    private static KeyStroke tab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0);
    private static KeyStroke shift_tab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.SHIFT_MASK);
    
    /**
     * Fixes the focus management on a table so that hitting tab / shift & tab
     * moves focus from the table to another component
     * @author Niall O Cuilinn
     * 
     * @param theTable
     */
    public static void fixTabbing(JTable theTable) 
    {   
        final JTable table = theTable;
        InputMap im = table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = table.getActionMap();
        im.put(enter, "");
        im.put(tab, "focusNextComponent");
        im.put(shift_tab, "focusPreviousComponent");
        am.put("focusNextComponent", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                FocusManager.getCurrentKeyboardFocusManager().focusNextComponent(table);
            }
        });
        am.put("focusPreviousComponent", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                FocusManager.getCurrentKeyboardFocusManager().focusPreviousComponent(table);
            }
        });
    }
    
    /**
     * Fixes the focus management on a text area so that hitting tab / shift & tab
     * moves focus from the text area to another component
     * @author Niall O Cuilinn
     * 
     * @param theTextArea
     */
    public static void fixTabbing(JTextArea theTextArea) 
    {   
        final JTextArea textArea = theTextArea;
        InputMap im = textArea.getInputMap();
        ActionMap am = textArea.getActionMap();
        im.put(tab, "focusNextComponent");
        im.put(shift_tab, "focusPreviousComponent");
        am.put("focusNextComponent", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                FocusManager.getCurrentKeyboardFocusManager().focusNextComponent(textArea);
            }
        });
        am.put("focusPreviousComponent", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                FocusManager.getCurrentKeyboardFocusManager().focusPreviousComponent(textArea);
            }
        });
    }

    /**
     * @param theJTextField
     */
    public static void addSelectionListener(JTextField theJTextField) {
        theJTextField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                JTextField source = (JTextField)e.getSource();
                source.setSelectionStart(0);
                source.setSelectionEnd(source.getText().length());
            }
        });  
    }

    /**
     * Checks a value against the range of a spinner. If out of range it sets it to the min or max where appropriate and informs the user
     * @author Niall O Cuilinn
     * 
     * @param theInt
     * @param min
     * @param max
     * @return int
     */
    public static int validSpinnerValue(int theInt, int min, int max, Component parent, PBundle messages, String property) {
        if(theInt < min) {
            theInt = min;
            String s = messages.format("messages.error.SpinnerValueTooSmall", new Object[]{property});
            JOptionPane.showMessageDialog(parent,s,messages.getString("Product.Title"),JOptionPane.WARNING_MESSAGE);
        }
        if(theInt > max) {
            theInt = max;
            String s = messages.format("messages.error.SpinnerValueTooLarge", new Object[]{property});
            JOptionPane.showMessageDialog(parent,s,messages.getString("Product.Title"),JOptionPane.WARNING_MESSAGE);
        }
        return theInt;
    }
    
    public static void updateJSpinner(JSpinner theJSpinner, int value, int minimum, int maximum, int stepSize){
        ((SpinnerNumberModel) theJSpinner.getModel()).setValue(new Integer(value));
        ((SpinnerNumberModel) theJSpinner.getModel()).setMinimum(new Integer(minimum));
        ((SpinnerNumberModel) theJSpinner.getModel()).setMaximum(new Integer(maximum));
        ((SpinnerNumberModel) theJSpinner.getModel()).setStepSize(new Integer(stepSize));
    }

    public static void clearJMenu(JMenu theJMenu) {
        for (int i = 0; i < theJMenu.getMenuComponentCount(); i++) {
            Component c = theJMenu.getMenuComponent(i);
            if (c != null && c instanceof JMenuItem){
	            ((JMenuItem) c).setAction(null);
            }
        }
        theJMenu.removeAll();
    }
    
    public static void clearJPopupMenu(JPopupMenu theJPopupMenu) {
        for (int i = 0; i < theJPopupMenu.getComponentCount(); i++) {
            Component c = theJPopupMenu.getComponent(i);
            if (c != null && c instanceof JMenuItem){
	            ((JMenuItem) c).setAction(null);
            }
        }
        theJPopupMenu.removeAll();
    }

    public static void clearMaps(JComponent theComponent) {
        for (int i = 0; i < theComponent.getActionMap().allKeys().length; i++) {
            Object key = theComponent.getActionMap().allKeys()[i];
            theComponent.getActionMap().remove(key);
        }
        for (int i = 0; i < theComponent.getInputMap().allKeys().length; i++) {
            KeyStroke key = theComponent.getInputMap().allKeys()[i];
            theComponent.getInputMap().remove(key);
        }
    }

    public static void removeAllListeners(JComponent theComponent) {
        ArrayList <EventListener> toRemove;
        
        //Remove ComponentListener
        toRemove = new ArrayList <EventListener> ();
        for (int i = 0; i < theComponent.getKeyListeners().length; i++) {
            toRemove.add(theComponent.getKeyListeners()[i]);
        }
        for (Iterator iter = toRemove.iterator(); iter.hasNext();) {
            theComponent.removeKeyListener((KeyListener) iter.next());
        }
        //Remove ContainerListener
        toRemove = new ArrayList <EventListener> ();
        for (int i = 0; i < theComponent.getContainerListeners().length; i++) {
            toRemove.add(theComponent.getContainerListeners()[i]);
        }
        for (Iterator iter = toRemove.iterator(); iter.hasNext();) {
            theComponent.removeContainerListener((ContainerListener) iter.next());
        }
        //Remove FocusListener
        toRemove = new ArrayList <EventListener> ();
        for (int i = 0; i < theComponent.getFocusListeners().length; i++) {
            toRemove.add(theComponent.getFocusListeners()[i]);
        }
        for (Iterator iter = toRemove.iterator(); iter.hasNext();) {
            theComponent.removeFocusListener((FocusListener) iter.next());
        }
        //Remove HierarchyBoundsListener
        toRemove = new ArrayList <EventListener> ();
        for (int i = 0; i < theComponent.getHierarchyBoundsListeners().length; i++) {
            toRemove.add(theComponent.getHierarchyBoundsListeners()[i]);
        }
        for (Iterator iter = toRemove.iterator(); iter.hasNext();) {
            theComponent.removeHierarchyBoundsListener((HierarchyBoundsListener) iter.next());
        }
        //Remove HierarchyListener
        toRemove = new ArrayList <EventListener> ();
        for (int i = 0; i < theComponent.getHierarchyListeners().length; i++) {
            toRemove.add(theComponent.getHierarchyListeners()[i]);
        }
        for (Iterator iter = toRemove.iterator(); iter.hasNext();) {
            theComponent.removeHierarchyListener((HierarchyListener) iter.next());
        }
        //Remove InputMethodListener
        toRemove = new ArrayList <EventListener> ();
        for (int i = 0; i < theComponent.getInputMethodListeners().length; i++) {
            toRemove.add(theComponent.getInputMethodListeners()[i]);
        }
        for (Iterator iter = toRemove.iterator(); iter.hasNext();) {
            theComponent.removeInputMethodListener((InputMethodListener) iter.next());
        }
        //Remove KeyListener
        toRemove = new ArrayList <EventListener> ();
        for (int i = 0; i < theComponent.getKeyListeners().length; i++) {
            toRemove.add(theComponent.getKeyListeners()[i]);
        }
        for (Iterator iter = toRemove.iterator(); iter.hasNext();) {
            theComponent.removeKeyListener((KeyListener) iter.next());
        }
        //Remove MouseListener
        toRemove = new ArrayList <EventListener> ();
        for (int i = 0; i < theComponent.getMouseListeners().length; i++) {
            toRemove.add(theComponent.getMouseListeners()[i]);
        }
        for (Iterator iter = toRemove.iterator(); iter.hasNext();) {
            theComponent.removeMouseListener((MouseListener) iter.next());
        }
        //Remove MouseMotionListener
        toRemove = new ArrayList <EventListener> ();
        for (int i = 0; i < theComponent.getMouseMotionListeners().length; i++) {
            toRemove.add(theComponent.getMouseMotionListeners()[i]);
        }
        for (Iterator iter = toRemove.iterator(); iter.hasNext();) {
            theComponent.removeMouseMotionListener((MouseMotionListener) iter.next());
        }
        //Remove MouseWheelListener
        toRemove = new ArrayList <EventListener> ();
        for (int i = 0; i < theComponent.getMouseWheelListeners().length; i++) {
            toRemove.add(theComponent.getMouseWheelListeners()[i]);
        }
        for (Iterator iter = toRemove.iterator(); iter.hasNext();) {
            theComponent.removeMouseWheelListener((MouseWheelListener) iter.next());
        }
    }
}