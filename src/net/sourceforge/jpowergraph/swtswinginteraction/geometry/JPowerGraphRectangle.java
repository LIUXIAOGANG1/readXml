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

package net.sourceforge.jpowergraph.swtswinginteraction.geometry;


public class JPowerGraphRectangle {

    public int x;

    public int y;

    public int width;

    public int height;

    public JPowerGraphRectangle(int theX, int theY, int theWidth, int theHeight) {
        this.x = theX;
        this.y = theY;
        this.width = theWidth;
        this.height = theHeight;
    }
    
    public int getX(){
        return x;
    }
    
    public int getY(){
        return y;
    }
    
    public int getWidth(){
        return width;
    }
    
    public int getHeight(){
        return height;
    }

    public boolean contains(JPowerGraphPoint thePoint){
        return contains(thePoint.x, thePoint.y);
    }
    
    public boolean contains(int X, int Y) {
        int w = this.width;
        int h = this.height;
        if ((w | h) < 0) {
            // At least one of the dimensions is negative...
            return false;
        }
        // Note: if either dimension is zero, tests below must return false...
        int x = this.x;
        int y = this.y;
        if (X < x || Y < y) {
            return false;
        }
        w += x;
        h += y;
        // overflow || intersect
        return ((w < x || w > X) && (h < y || h > Y));
    }
    
    public boolean intersects (JPowerGraphRectangle rect) {
        if (rect == null){
            return false;
        }
        return rect == this || intersects (rect.x, rect.y, rect.width, rect.height);
    }
    
    public boolean intersects (int x, int y, int width, int height) {
        return (x < this.x + this.width) && (y < this.y + this.height) && (x + width > this.x) && (y + height > this.y);
    }
}
