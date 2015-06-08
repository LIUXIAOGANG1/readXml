package net.sourceforge.jpowergraph.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import net.sourceforge.jpowergraph.lens.CursorLens;
import net.sourceforge.jpowergraph.lens.LegendLens;
import net.sourceforge.jpowergraph.lens.LensSet;
import net.sourceforge.jpowergraph.lens.NodeSizeLens;
import net.sourceforge.jpowergraph.lens.RotateLens;
import net.sourceforge.jpowergraph.lens.TooltipLens;
import net.sourceforge.jpowergraph.lens.ZoomLens;
import net.sourceforge.jpowergraph.swing.viewcontrols.CursorControlPanel;
import net.sourceforge.jpowergraph.swing.viewcontrols.LegendControlPanel;
import net.sourceforge.jpowergraph.swing.viewcontrols.NodeSizeControlPanel;
import net.sourceforge.jpowergraph.swing.viewcontrols.RotateControlPanel;
import net.sourceforge.jpowergraph.swing.viewcontrols.TooltipControlPanel;
import net.sourceforge.jpowergraph.swing.viewcontrols.ZoomControlPanel;
import net.sourceforge.powerswing.panel.PPanel;

/**
 * @author Mick Kerrigan
 *
 * Created on 05-Aug-2005
 * Committed by $Author: morcen $
 *
 * $Source: /cvsroot/jpowergraph/swing/src/net/sourceforge/jpowergraph/swing/SwingJGraphViewPane.java,v $,
 * @version $Revision: 1.1 $ $Date: 2006/10/26 18:37:13 $
 */
public class SwingJGraphViewPane extends JPanel{

    private JPanel startPanel;
    private JPanel endPanel;
    
    /**
     * 
     */
    public SwingJGraphViewPane(JComponent theJComponent, LensSet theLensSet, boolean allowContributions) {
        super();
        startPanel = new JPanel();
        endPanel = new JPanel();
        CursorControlPanel cursorControl = new CursorControlPanel((CursorLens) theLensSet.getFirstLensOfType(CursorLens.class));
        ZoomControlPanel zoomControl = new ZoomControlPanel((ZoomLens) theLensSet.getFirstLensOfType(ZoomLens.class));
        RotateControlPanel rotateControl = new RotateControlPanel((RotateLens) theLensSet.getFirstLensOfType(RotateLens.class));
        NodeSizeControlPanel nodeSizeControl = new NodeSizeControlPanel((NodeSizeLens) theLensSet.getFirstLensOfType(NodeSizeLens.class));
        LegendControlPanel legendControl = new LegendControlPanel((LegendLens) theLensSet.getFirstLensOfType(LegendLens.class));
        TooltipControlPanel tooltipControl = new TooltipControlPanel((TooltipLens) theLensSet.getFirstLensOfType(TooltipLens.class));
        
        this.setLayout(new BorderLayout());
        
        JPanel toolTipPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        toolTipPanel.add(legendControl);
        toolTipPanel.add(tooltipControl);
        
        
        PPanel top; 
        if (allowContributions){
            top= new PPanel(1, 8, 2, 4, new Object[]{
                    "",     "0",         "0",           "0", "0",   "0","0",       "0", "0",
                    "0",    startPanel , cursorControl, new JSeparator(JSeparator.VERTICAL), zoomControl,  rotateControl, new JSeparator(JSeparator.VERTICAL), nodeSizeControl, endPanel
            }, 0, 0, 0, 0);
        }
        else{
            top= new PPanel(1, 6, 2, 4, new Object[]{
                    "",     "0",           "0", "0",   "0","0",       "0",
                    "0",    cursorControl, new JSeparator(JSeparator.VERTICAL), zoomControl,  rotateControl, new JSeparator(JSeparator.VERTICAL), nodeSizeControl
            }, 0, 0, 0, 0);
        }
        
        this.add(new PPanel(2, 1, 0, 0, new Object[]{
                "",     "0,1",
                "0",    top,
                "0,1",  theJComponent,
        }));
    }

    public JPanel getContributePanel() {
        return startPanel;
    }
    
    public JPanel getEndContributeComposite() {
        return endPanel;
    }
}
