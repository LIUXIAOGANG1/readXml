package net.sourceforge.jpowergraph.swing.viewcontrols;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import net.sourceforge.jpowergraph.lens.Lens;
import net.sourceforge.jpowergraph.lens.LensListener;
import net.sourceforge.jpowergraph.lens.ZoomLens;
import net.sourceforge.powerswing.panel.PPanel;

/**
 * @author Mick Kerrigan
 *
 * Created on 05-Aug-2005
 * Committed by $Author: morcen $
 *
 * $Source: /cvsroot/jpowergraph/swing/src/net/sourceforge/jpowergraph/swing/viewcontrols/ZoomControlPanel.java,v $,
 * @version $Revision: 1.1 $ $Date: 2006/10/26 18:37:12 $
 */
public class ZoomControlPanel extends JPanel {

    public static final Integer[] DEFAULT_ZOOM_LEVELS = new Integer[]{25, 50, 75, 100, 125, 150, 175, 200, 225, 250, 275, 300};
    
    private JComboBox zoomFactor;
    private ZoomLens zoomLens;
    
    /**
     * 
     */
    public ZoomControlPanel(ZoomLens theZoomLens) {
        this(theZoomLens, DEFAULT_ZOOM_LEVELS, 100);
    }
    
    public ZoomControlPanel(ZoomLens theZoomLens, Integer[] zoomLevels, Integer zoomValue) {
        super();
        zoomLens = theZoomLens;
        zoomFactor = new JComboBox(zoomLevels);
        zoomFactor.setRenderer(new PercentileListCellRenderer());
        zoomFactor.setEnabled(zoomLens != null);
        zoomFactor.setSelectedItem(zoomValue);
        setSelectedItemFromLens();
        
        this.setLayout(new BorderLayout());
        this.add(new PPanel(1, 2, 0, 0, new Object[]{
                "",     "0",        "0",
                "0",    "~Zoom: ",  zoomFactor
        }));
        this.addActionListeners();
    }

    private void addActionListeners() {
        if (zoomLens != null && zoomFactor != null){
            zoomFactor.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    int i = (Integer) zoomFactor.getSelectedItem();
                    double d = i / 100d;
                    if (zoomLens.getZoomFactor() != d){
                        zoomLens.setZoomFactor(d);
                    }
                }
            });
        }
        
        if (zoomLens != null){
            zoomLens.addLensListener(new LensListener() {
                public void lensUpdated(Lens lens) {
                    setSelectedItemFromLens();
                }
            });
        }
    }

    protected void setSelectedItemFromLens() {
        if (zoomLens != null && zoomFactor != null){
            zoomFactor.setSelectedItem((int) (zoomLens.getZoomFactor() * 100d));
        }
    }
}
