/*
 * Copyright (C) 2006 Digital Enterprise Research Insitute (DERI) Innsbruck
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.sourceforge.jpowergraph.manipulator.popup;

import net.sourceforge.jpowergraph.lens.TooltipLens;
import net.sourceforge.jpowergraph.manipulator.AbstractManipulator;
import net.sourceforge.jpowergraph.pane.JGraphPane;
import net.sourceforge.jpowergraph.swtswinginteraction.listeners.JPowerGraphMouseEvent;


public class PopupManipulator extends AbstractManipulator {

    public static final String NAME = "PopupManipulator";
    
    private TooltipLens tooltipLens;
    
    public PopupManipulator(JGraphPane theGraphPane, TooltipLens theTooltipLens) {
        setGraphPane(theGraphPane);
        this.tooltipLens = theTooltipLens;
    }
    
    public String getName() {
        return NAME;
    }
    
    public void mouseDown(JPowerGraphMouseEvent e) {
        getGraphPane().getPopupDisplayer().closeToolTipIfNeeded(getGraphPane());
        doRightClickPopup(e);
    }
    
    public void mouseUp(JPowerGraphMouseEvent e) {
        doRightClickPopup(e);
    }
    
    public void mouseExit(JPowerGraphMouseEvent e) {
        getGraphPane().getPopupDisplayer().closeToolTipIfNeeded(getGraphPane());
    }
    
    public void mouseMove(JPowerGraphMouseEvent e){
        if (getGraphPane().getPopupDisplayer().isToolTipShowing()){
            getGraphPane().getPopupDisplayer().doToolTipPopup(getGraphPane(), e);
        }
    }
    
    public void mouseHover(JPowerGraphMouseEvent e) {
        if (showToolTips() && getGraphPane().isEnabled() && !getGraphPane().getPopupDisplayer().isRightClickShowing()){
            getGraphPane().getPopupDisplayer().doToolTipPopup(getGraphPane(), e);
        }
    }
    
    private void doRightClickPopup(JPowerGraphMouseEvent e){
        if (getGraphPane().isEnabled() && e.getButton() == JPowerGraphMouseEvent.RIGHT) {
            getGraphPane().getPopupDisplayer().doRightClickPopup(getGraphPane(), e);
        }
        else{
            getGraphPane().getPopupDisplayer().closeRightClickIfNeeded(getGraphPane());
        }
    }

    private boolean showToolTips() {
        return tooltipLens == null || tooltipLens.isShowToolTips();
    }
}
