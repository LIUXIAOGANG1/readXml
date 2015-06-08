package net.sourceforge.powerswing.label;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.JLabel;

public class PJLabel extends JLabel{
	
	public ArrayList <Component> associations = new ArrayList <Component> ();

	public PJLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
	}

	public PJLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
	}

	public PJLabel(String text) {
		super(text);
	}
	
	public PJLabel(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
	}

	public PJLabel(Icon image) {
		super(image);
	}

	public PJLabel() {
		super();
	}

	public void addAssociation(Component c){
		associations.add(c);
		c.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("enabled")){
					checkStates();
				}
			}
		});
	}
	
	public void addAssociations(ArrayList components){
		for (Iterator i = components.iterator(); i.hasNext();) {
			addAssociation((Component) i.next());
		}
	}
	
	public void checkStates() {
	    if (associations.size() > 0){
			boolean allDisabled = true;
			for (Iterator <Component> i = associations.iterator(); i.hasNext();) {
				Component comp = i.next();
				if (comp.isEnabled()){
					allDisabled = false;
				}
			}
			this.setEnabled(!allDisabled);
	    }
	}
}