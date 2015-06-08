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

public interface JPowerGraphMouseListener {

    public void mouseDoubleClick(JPowerGraphMouseEvent e);

    public void mouseDown(JPowerGraphMouseEvent e);

    public void mouseUp(JPowerGraphMouseEvent e);
    
    public void mouseEnter(JPowerGraphMouseEvent e);

    public void mouseExit(JPowerGraphMouseEvent e);

    public void mouseHover(JPowerGraphMouseEvent e);
    
    public void mouseMove(JPowerGraphMouseEvent e);
}