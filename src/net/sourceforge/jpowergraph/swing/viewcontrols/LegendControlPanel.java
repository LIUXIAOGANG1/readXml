package net.sourceforge.jpowergraph.swing.viewcontrols;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import net.sourceforge.jpowergraph.lens.LegendLens;
import net.sourceforge.jpowergraph.lens.Lens;
import net.sourceforge.jpowergraph.lens.LensListener;
import net.sourceforge.powerswing.panel.PPanel;

/**
 * @author Mick Kerrigan
 *
 * Created on 05-Aug-2005
 * Committed by $Author: morcen $
 *
 * $Source: /cvsroot/jpowergraph/swing/src/net/sourceforge/jpowergraph/swing/viewcontrols/LegendControlPanel.java,v $,
 * @version $Revision: 1.1 $ $Date: 2006/10/26 18:37:12 $
 */
public class LegendControlPanel extends JPanel {
    
    private JCheckBox showLegend;
    private LegendLens legendLens;
    
    public LegendControlPanel(LegendLens theLegendLens) {
        this(theLegendLens, true);
    }
    
    public LegendControlPanel(LegendLens theLegendLens, boolean showLegendValue) {
        super();
        legendLens = theLegendLens;
        showLegend = new JCheckBox("Show Legend", showLegendValue);
        showLegend.setBorder(BorderFactory.createEmptyBorder());
        showLegend.setEnabled(legendLens != null);
        setSelectedItemFromLens();
        
        this.setLayout(new BorderLayout());
        this.add(new PPanel(1, 1, 0, 0, new Object[]{
                "",     "0",
                "0",    showLegend
        }, 0, 0, 0, 0));
        this.addActionListeners();
    }

    private void addActionListeners() {
        if (legendLens != null && showLegend != null){
            showLegend.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (legendLens.isShowLegend() != showLegend.isSelected()){
                        legendLens.setShowLegend(showLegend.isSelected());
                    }
                }
            });
        }
        
        if (legendLens != null){
            legendLens.addLensListener(new LensListener() {
                public void lensUpdated(Lens lens) {
                    setSelectedItemFromLens();
                }
            });
        }
    }

    protected void setSelectedItemFromLens() {
        if (legendLens != null && showLegend != null){
            showLegend.setSelected(legendLens.isShowLegend());
        }
    }
}