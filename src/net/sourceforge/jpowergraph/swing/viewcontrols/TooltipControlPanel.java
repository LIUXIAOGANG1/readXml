package net.sourceforge.jpowergraph.swing.viewcontrols;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import net.sourceforge.jpowergraph.lens.Lens;
import net.sourceforge.jpowergraph.lens.LensListener;
import net.sourceforge.jpowergraph.lens.TooltipLens;
import net.sourceforge.powerswing.panel.PPanel;

/**
 * @author Mick Kerrigan
 *
 * Created on 05-Aug-2005
 * Committed by $Author: morcen $
 *
 * $Source: /cvsroot/jpowergraph/swing/src/net/sourceforge/jpowergraph/swing/viewcontrols/TooltipControlPanel.java,v $,
 * @version $Revision: 1.1 $ $Date: 2006/10/26 18:37:12 $
 */
public class TooltipControlPanel extends JPanel {
    
    private JCheckBox showToolTips;
    private TooltipLens tooltipLens;
    
    public TooltipControlPanel(TooltipLens theTooltipLens) {
        this(theTooltipLens, true);
    }
    
    public TooltipControlPanel(TooltipLens theTooltipLens, boolean showToolTipsValue) {
        super();
        tooltipLens = theTooltipLens;
        showToolTips = new JCheckBox("Show Tooltips", showToolTipsValue);
        showToolTips.setBorder(BorderFactory.createEmptyBorder());
        showToolTips.setEnabled(tooltipLens != null);
        setSelectedItemFromLens();
        
        this.setLayout(new BorderLayout());
        this.add(new PPanel(1, 1, 0, 0, new Object[]{
                "",     "0",
                "0",    showToolTips
        }, 0, 0, 0, 0));
        this.addActionListeners();
    }

    private void addActionListeners() {
        if (tooltipLens != null && showToolTips != null){
            showToolTips.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (tooltipLens.isShowToolTips() != showToolTips.isSelected()){
                        tooltipLens.setShowToolTips(showToolTips.isSelected());
                    }
                }
            });
        }
        
        if (tooltipLens != null){
            tooltipLens.addLensListener(new LensListener() {
                public void lensUpdated(Lens lens) {
                    setSelectedItemFromLens();
                }
            });
        }
    }

    protected void setSelectedItemFromLens() {
        if (tooltipLens != null && showToolTips != null){
            showToolTips.setSelected(tooltipLens.isShowToolTips());
        }
    }
}