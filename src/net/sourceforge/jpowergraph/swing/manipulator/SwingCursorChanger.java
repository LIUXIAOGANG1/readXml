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

package net.sourceforge.jpowergraph.swing.manipulator;

import java.awt.Cursor;
import java.awt.dnd.DragSource;

import net.sourceforge.jpowergraph.pane.CursorChanger;
import net.sourceforge.jpowergraph.pane.JGraphPane;
import net.sourceforge.jpowergraph.swing.SwingJGraphPane;

public class SwingCursorChanger implements CursorChanger {

    // Defaults
    private static Cursor arrow;
    private static Cursor cross;

    // Overrides
    public static Cursor hand;
    public static Cursor stop;

    public SwingCursorChanger() {
        arrow = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        cross = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
        hand = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        stop = DragSource.DefaultMoveNoDrop;
    }

    public void setCursor(JGraphPane theJGraphPane, int theCursorType) {
        SwingJGraphPane graphPane = (SwingJGraphPane) theJGraphPane;
        if (theCursorType == CursorChanger.ARROW){
            graphPane.setCursor(arrow);
        }
        else if (theCursorType == CursorChanger.CROSS){
            graphPane.setCursor(cross);
        }
        else if (theCursorType == CursorChanger.HAND){
            graphPane.setCursor(hand);
        }
        else if (theCursorType == CursorChanger.STOP){
            graphPane.setCursor(stop);
        }
    }

}
