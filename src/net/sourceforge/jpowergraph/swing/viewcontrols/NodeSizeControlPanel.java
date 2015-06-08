package net.sourceforge.jpowergraph.swing.viewcontrols;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import net.sourceforge.jpowergraph.lens.Lens;
import net.sourceforge.jpowergraph.lens.LensListener;
import net.sourceforge.jpowergraph.lens.NodeSizeLens;
import net.sourceforge.jpowergraph.painters.NodePainter;
import net.sourceforge.powerswing.panel.PPanel;

/**
 * @author Mick Kerrigan
 *
 * Created on 05-Aug-2005
 * Committed by $Author: morcen $
 *
 * $Source: /cvsroot/jpowergraph/swing/src/net/sourceforge/jpowergraph/swing/viewcontrols/NodeSizeControlPanel.java,v $,
 * @version $Revision: 1.1 $ $Date: 2006/10/26 18:37:12 $
 */
public class NodeSizeControlPanel extends JPanel {
    
    private JToggleButton smallNodes;
    private JToggleButton largeNodes;
    
    private NodeSizeLens nodeSizeLens;

    public NodeSizeControlPanel(NodeSizeLens theNodeSizeLens) {
        super();
        nodeSizeLens = theNodeSizeLens;
        
        ImageIcon small = new ImageIcon(CursorControlPanel.class.getResource("small.gif"));
        ImageIcon large = new ImageIcon(CursorControlPanel.class.getResource("large.gif"));
        
        largeNodes = new JToggleButton(large, true);
        largeNodes.setBorder(BorderFactory.createEmptyBorder(5, 7, 5, 7));
        largeNodes.setEnabled(nodeSizeLens != null);
        smallNodes = new JToggleButton(small, false);
        smallNodes.setBorder(BorderFactory.createEmptyBorder(5, 7, 5, 7));
        smallNodes.setEnabled(nodeSizeLens != null);
        setSelectedItemFromLens();
        
        this.setLayout(new BorderLayout());
        this.add(new PPanel(1, 2, 0, 2, new Object[]{
                "",     "0",        "0",
                "0",    largeNodes, smallNodes
        }));
        this.addActionListeners();
    }

    /**
     * @param theNodeSizeValue
     */
    private void select(Integer theNodeSizeValue) {
        smallNodes.setSelected(theNodeSizeValue == NodePainter.SMALL);
        largeNodes.setSelected(theNodeSizeValue == NodePainter.LARGE);
    }

    private void addActionListeners() {
        if (nodeSizeLens != null && smallNodes != null){
            smallNodes.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    int value = -1;
                    if (smallNodes.isSelected()){
                        value = NodePainter.SMALL;
                    }
                    else{
                        value = NodePainter.LARGE;
                    }
                    
                    if (nodeSizeLens.getNodeSize() != value){
                        nodeSizeLens.setNodeSize(value);
                    }
                }
            });
        }
        
        if (nodeSizeLens != null && largeNodes != null){
            largeNodes.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    int value = -1;
                    if (largeNodes.isSelected()){
                        value = NodePainter.LARGE;
                    }
                    else{
                        value = NodePainter.SMALL;
                    }
                    
                    if (nodeSizeLens.getNodeSize() != value){
                        nodeSizeLens.setNodeSize(value);
                    }
                }
            });
        }
        
        if (nodeSizeLens != null){
            nodeSizeLens.addLensListener(new LensListener() {
                public void lensUpdated(Lens lens) {
                    setSelectedItemFromLens();
                }
            });
        }
    }

    protected void setSelectedItemFromLens() {
        if (nodeSizeLens != null && largeNodes != null && smallNodes != null){
            select(nodeSizeLens.getNodeSize());
        }
    }
}
