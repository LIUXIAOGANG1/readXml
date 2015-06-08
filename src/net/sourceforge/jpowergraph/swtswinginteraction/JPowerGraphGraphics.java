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

package net.sourceforge.jpowergraph.swtswinginteraction;

import net.sourceforge.jpowergraph.pane.JGraphPane;
import net.sourceforge.jpowergraph.swtswinginteraction.color.JPowerGraphColor;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphDimension;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphRectangle;


public interface JPowerGraphGraphics {

    public JPowerGraphColor getBackground();
    public JPowerGraphColor getForeground();
    public void setForeground(JPowerGraphColor theColor);
    public void setBackground(JPowerGraphColor theColor);
    
    public int getStringWidth(String theString);
    public int getAscent();
    public int getDescent();
    
    public void drawLine(int x1, int y1, int x2, int y2);
    //-Bingxue
    public void drawArc(int x, int y, int width, int height,int startAngle, int arcAngle);
            
    public void drawString(String theString, int x, int y, int numlines);
    public void drawRectangle(int x, int y, int width, int height);
    public void drawRoundRectangle(int x, int y, int width, int height, int arcWidth, int arcHeight);
    public void drawOval(int x, int y, int width, int height);
    public void drawPolygon(int[] theIntegers);
    
    public void fillRectangle(int x, int y, int width, int height);
    public void fillRoundRectangle(int x, int y, int width, int height, int arcWidth, int arcHeight);
    public void fillOval(int x, int y, int width, int height);
    public void fillPolygon(int[] theIntegers);
    
    //Store and Restore
    public void storeFont();
    public void setFontFromJGraphPane(JGraphPane graphPane);
    public void restoreFont();
    
    public JPowerGraphRectangle getClipping();
    public boolean getAntialias();
    public void setAntialias(boolean antialias);
    public JPowerGraphGraphics getSubJPowerGraphGraphics(JPowerGraphDimension theDimension);
    public void dispose();
    public boolean isDisposed();
    public void drawSubJPowerGraph(JPowerGraphGraphics subG, int i, int j);
    public int getLineWidth();
    public void setLineDashed(boolean b);
    public void setLineWidth(int lineWidth);
}
