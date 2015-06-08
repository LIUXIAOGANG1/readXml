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

package net.sourceforge.jpowergraph.lens;

import java.awt.geom.Point2D;

import net.sourceforge.jpowergraph.pane.CursorChanger;
import net.sourceforge.jpowergraph.pane.JGraphPane;


public class CursorLens extends AbstractLens {

    private boolean isArrow = false;
    private boolean isCross = false;
    private boolean isHand = false;
    private boolean isStop = false;

    public CursorLens() {
        setArrow(true);
    }
    
    public boolean isArrow() {
        if (isHand || isStop){
            return false;
        }
        return isArrow;
    }
    
    public boolean isCross() {
        if (isHand || isStop){
            return false;
        }
        return isCross;
    }
    
    public boolean isHand() {
        return isHand;
    }
    
    public boolean isStop() {
        return isStop;
    }

    public void setArrow(boolean theBoolean) {
        this.isArrow = theBoolean;
        this.isCross = !theBoolean;
        fireLensUpdated();
    }
    
    public void setCross(boolean theBoolean) {
        this.isCross = theBoolean;
        this.isArrow = !theBoolean;
        fireLensUpdated();
    }
    
    public void setHand(boolean theBoolean) {
        this.isHand = theBoolean;
        if (isHand){
            this.isStop = false;
        }
        fireLensUpdated();
    }

    public void setStop(boolean theBoolean) {
        this.isStop = theBoolean;
        if (isStop){
            this.isHand = false;
        }
        fireLensUpdated();
    }
    
    public void applyLens(JGraphPane theJGraphPane, Point2D point) {
        if (isArrow()){
            theJGraphPane.getCursorChanger().setCursor(theJGraphPane, CursorChanger.ARROW);
        }
        else if (isCross()){
            theJGraphPane.getCursorChanger().setCursor(theJGraphPane, CursorChanger.CROSS);
        }
        else if (isHand()){
            theJGraphPane.getCursorChanger().setCursor(theJGraphPane, CursorChanger.HAND);
        }
        else if (isStop()){
            theJGraphPane.getCursorChanger().setCursor(theJGraphPane, CursorChanger.STOP);
        }
    }
    
    public void undoLens(JGraphPane theJGraphPane, Point2D point) {
        if (isArrow()){
            theJGraphPane.getCursorChanger().setCursor(theJGraphPane, CursorChanger.ARROW);
        }
        else if (isCross()){
            theJGraphPane.getCursorChanger().setCursor(theJGraphPane, CursorChanger.CROSS);
        }
        else if (isHand()){
            theJGraphPane.getCursorChanger().setCursor(theJGraphPane, CursorChanger.HAND);
        }
        else if (isStop()){
            theJGraphPane.getCursorChanger().setCursor(theJGraphPane, CursorChanger.STOP);
        }
    }
}
