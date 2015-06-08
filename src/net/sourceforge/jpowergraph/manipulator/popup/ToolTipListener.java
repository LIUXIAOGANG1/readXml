package net.sourceforge.jpowergraph.manipulator.popup;

import net.sourceforge.jpowergraph.Node;

public interface ToolTipListener <E, F>{

    public boolean addNodeToolTipItems(Node theNode, E theGUIWidget, F backgroundColor);
    public void removeNodeToolTipItems(Node theNode, E theGUIWidget);
}
