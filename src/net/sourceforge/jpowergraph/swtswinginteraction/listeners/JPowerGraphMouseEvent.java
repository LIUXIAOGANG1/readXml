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

package net.sourceforge.jpowergraph.swtswinginteraction.listeners;

import java.util.ArrayList;

import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphPoint;


public class JPowerGraphMouseEvent {

    public static final int LEFT = 0;
    public static final int CENTRE = 1;
    public static final int RIGHT = 2;
    
    public static final int NO_MODIFIER = -1;
    public static final int SHIFT_MODIFIER = 0;
    public static final int CTRL_MODIFIER = 1;
    
    private JPowerGraphPoint point;
    private int button;
    private ArrayList <Integer> modifiers;
    
    public JPowerGraphMouseEvent(JPowerGraphPoint thePoint, int theButton, ArrayList <Integer> theModifiers) {
        this.point = thePoint;
        this.button = theButton;
        this.modifiers = theModifiers;
    }

    public JPowerGraphPoint getPoint() {
        return point;
    }
    
    public int getButton(){
        return button;
    }

    public ArrayList<Integer> getModifierList() {
        return modifiers;
    }
}
