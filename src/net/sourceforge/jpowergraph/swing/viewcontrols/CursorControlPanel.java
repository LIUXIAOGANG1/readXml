package net.sourceforge.jpowergraph.swing.viewcontrols;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import net.sourceforge.jpowergraph.lens.CursorLens;
import net.sourceforge.jpowergraph.lens.Lens;
import net.sourceforge.jpowergraph.lens.LensListener;
import net.sourceforge.powerswing.panel.PPanel;

/**
 * @author Mick Kerrigan
 *
 * Created on 05-Aug-2005
 * Committed by $Author: morcen $
 *
 * $Source: /cvsroot/jpowergraph/swing/src/net/sourceforge/jpowergraph/swing/viewcontrols/CursorControlPanel.java,v $,
 * @version $Revision: 1.1 $ $Date: 2006/10/26 18:37:12 $
 */
public class CursorControlPanel extends JPanel {

    private JToggleButton moveGraph;
    private JToggleButton createEdge;
    
    private CursorLens draggingLens;
    
    /**
     * 
     */
    public CursorControlPanel(CursorLens theDraggingLens) {
        this(theDraggingLens, true);
    }
    
    public CursorControlPanel(CursorLens theDraggingLens, boolean theMoveGraph) {
        super();
        draggingLens = theDraggingLens;
        
        ImageIcon arrow = new ImageIcon(CursorControlPanel.class.getResource("arrow.gif"));
        ImageIcon cross = new ImageIcon(CursorControlPanel.class.getResource("cross.gif"));
        
        moveGraph = new JToggleButton(arrow, theMoveGraph);
        moveGraph.setBorder(BorderFactory.createEmptyBorder(5, 7, 5, 7));
        moveGraph.setEnabled(draggingLens != null);
        createEdge = new JToggleButton(cross, !theMoveGraph);
        createEdge.setBorder(BorderFactory.createEmptyBorder(6, 8, 4, 6));
        createEdge.setEnabled(draggingLens != null);
        setSelectedItemFromLens();
        
        this.setLayout(new BorderLayout());
        this.add(new PPanel(1, 2, 0, 2, new Object[]{
                "",     "0",        "0",
                "0",    moveGraph,  createEdge
        }));
        this.addActionListeners();
    }

    private void addActionListeners() {
        if (draggingLens != null && moveGraph != null){
            moveGraph.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    boolean b = moveGraph.isSelected();
                    if (draggingLens.isArrow() != b){
                        draggingLens.setArrow(b);
                    }
                }
            });
        }
        
        if (draggingLens != null && createEdge != null){
            createEdge.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    boolean b = createEdge.isSelected();
                    if (draggingLens.isCross() != b){
                        draggingLens.setCross(b);
                    }
                }
            });
        }
        
        if (draggingLens != null){
            draggingLens.addLensListener(new LensListener() {
                public void lensUpdated(Lens lens) {
                    setSelectedItemFromLens();
                }
            });
        }
    }

    protected void setSelectedItemFromLens() {
        if (draggingLens != null && moveGraph != null){
            moveGraph.setSelected(draggingLens.isArrow());
        }
        if (draggingLens != null && createEdge != null){
            createEdge.setSelected(draggingLens.isCross());
        }
    }
}
