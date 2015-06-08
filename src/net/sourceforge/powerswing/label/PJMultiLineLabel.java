package net.sourceforge.powerswing.label;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JTextArea;

public class PJMultiLineLabel extends JTextArea{

	public ArrayList <Component> associations = new ArrayList <Component> ();

	public PJMultiLineLabel(String theText){
        this(theText, 2, 1);
    }
	
    public PJMultiLineLabel(String theText, int theRows, int theColumns){
        super(theText, theRows, theColumns);
        
        JLabel template = new JLabel();
        
        this.setFont(template.getFont());
        this.setCaret(new PJMultiLineLabelCaret());
        this.setBackground(template.getBackground());
        this.setEnabled(false);
        this.setDisabledTextColor(template.getForeground());
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
			for (Iterator i = associations.iterator(); i.hasNext();) {
				Component comp = (Component) i.next();
				if (comp.isEnabled()){
					allDisabled = false;
				}
			}
			this.setEnabled(!allDisabled);
	    }
	}
}
