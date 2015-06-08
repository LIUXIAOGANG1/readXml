package net.sourceforge.powerswing;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JCheckBox;
import javax.swing.JToggleButton;

/**
 * @author Niall Ó Cuilinn
 */

public class PMultiStateJCheckBox extends JCheckBox
{
    private int state;
    private boolean intable;
    /**
     * Constructor
     * @param theString a label
     */
    public PMultiStateJCheckBox(String aString, int theState, boolean theInTable)
    {
        super(aString);
        state = theState;
        intable = theInTable;
        setModel(new MultiStateToggleButtonModel());
    }
    
    /**
     * Paints box grey for null position
     * @param g Graphics
     */
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        if (getSelected() == null) {
            if (!intable){
                g.setColor(Color.gray);
                g.fillRect(5,6,10,10);    
            }
            else{
                g.setColor(Color.lightGray);
                g.fillRect(6,3,9,9);
            }
        }
    }
    
    /**
     * returns selected
     * @return Boolean
     */
    public Boolean getSelected()
    {
        return threeStateModel().getSelected();
    }
    
    /**
     * sets selected
     * @param b a Boolean
     */
    public void setSelected(Boolean b)
    {
        threeStateModel().setSelected(b);
        this.validate();
        this.repaint();
    }
    
    /**
     * returns a ThreeStateToggleButtonModel
     * @return ThreeStateToggleButtonModel
     */
    public MultiStateToggleButtonModel threeStateModel()
    {
        return (MultiStateToggleButtonModel) model;
    }

    public void setState(int theState) {
        state = theState;
        this.validate();
        this.repaint();
    }

    class MultiStateToggleButtonModel extends JToggleButton.ToggleButtonModel
    {
        Boolean selected;
        
        /**
         * returns selected
         * @return Boolean
         */    
        public Boolean getSelected()
        {
            return selected;
        }
        
        /**
         * sets selected
         * @param b a Boolean
         */        
        public void setSelected(Boolean b)
        {
            selected = b;
            super.setSelected(b == null ? false : b.booleanValue());
        }
        
        /**
         * sets selected true,false or null depending on present state plus boolean condition
         * @param b a boolean
         */    
        public void setSelected(boolean b)
        {
            if(state == 3) {
                System.out.println("setboolean "+b);
                if (selected == null) {
                    selected = new Boolean(false);
                    super.setSelected(false);
                }
    
                else if (!selected.booleanValue() && b) {
                    selected = new Boolean(true);
                    super.setSelected(true);    
                }
                
                else if (selected.booleanValue() && !b) {
                    selected = null;
                    super.setSelected(false);
                }
            }
            else {
                selected = new Boolean(b);
                super.setSelected(b);
            }
        }
    }

}
