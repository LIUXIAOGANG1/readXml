package net.sourceforge.jpowergraph.swing.viewcontrols;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import net.sourceforge.jpowergraph.lens.Lens;
import net.sourceforge.jpowergraph.lens.LensListener;
import net.sourceforge.jpowergraph.lens.RotateLens;
import net.sourceforge.powerswing.panel.PPanel;

/**
 * @author Mick Kerrigan
 *
 * Created on 05-Aug-2005
 * Committed by $Author: morcen $
 *
 * $Source: /cvsroot/jpowergraph/swing/src/net/sourceforge/jpowergraph/swing/viewcontrols/RotateControlPanel.java,v $,
 * @version $Revision: 1.1 $ $Date: 2006/10/26 18:37:12 $
 */
public class RotateControlPanel extends JPanel {
    
    public static final Integer[] DEFAULT_ROTATE_ANGLES = new Integer[]{0, 90, 180, 270};
    
    private JComboBox rotateFactor;
    private RotateLens rotateLens;
    
    /**
     * 
     */
    public RotateControlPanel(RotateLens theRotateLens) {
        this(theRotateLens, DEFAULT_ROTATE_ANGLES, 0);
    }
    
    public RotateControlPanel(RotateLens theRotateLens, Integer[] rotateAngles, Integer rotateValue) {
        super();
        rotateLens = theRotateLens;
        rotateFactor = new JComboBox(rotateAngles);
        rotateFactor.setRenderer(new AngleListCellRenderer());
        rotateFactor.setEnabled(rotateLens != null);
        rotateFactor.setSelectedItem(rotateValue);
        setSelectedItemFromLens();
        
        this.setLayout(new BorderLayout());
        this.add(new PPanel(1, 2, 0, 0, new Object[]{
                "",     "0",        "0",
                "0",    "~Rotate: ",  rotateFactor
        }));
        this.addActionListeners();
    }

    private void addActionListeners() {
        if (rotateLens != null && rotateFactor != null){
            rotateFactor.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    int i = 360 - (Integer) rotateFactor.getSelectedItem();
                    if (rotateLens.getRotationAngle() != i){
                        rotateLens.setRotationAngle(i);
                    }
                }
            });
        }
        
        if (rotateLens != null){
            rotateLens.addLensListener(new LensListener() {
                public void lensUpdated(Lens lens) {
                    setSelectedItemFromLens();
                }
            });
        }
    }

    protected void setSelectedItemFromLens() {
        if (rotateLens != null && rotateFactor != null){
            int currentValue = (int) (360 - rotateLens.getRotationAngle());
            while (currentValue == 360){
                currentValue = 0;
            }
            rotateFactor.setSelectedItem(currentValue);
        }
    }
}
