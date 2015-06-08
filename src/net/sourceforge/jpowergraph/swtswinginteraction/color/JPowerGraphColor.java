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

package net.sourceforge.jpowergraph.swtswinginteraction.color;



public class JPowerGraphColor {
    
    public static final JPowerGraphColor BLUE = new JPowerGraphColor(0, 0, 255);
    public static final JPowerGraphColor DARK_BLUE = new JPowerGraphColor(0, 0, 128);
    public static final JPowerGraphColor RED = new JPowerGraphColor(255, 0, 0);
    public static final JPowerGraphColor DARK_RED = new JPowerGraphColor(128, 0, 0);
    public static final JPowerGraphColor MAGENTA = new JPowerGraphColor(255, 0, 255);
    public static final JPowerGraphColor DARK_MAGENTA = new JPowerGraphColor(128, 0, 128);
    public static final JPowerGraphColor PINK = new JPowerGraphColor(255, 175, 175);
    public static final JPowerGraphColor ORANGE = new JPowerGraphColor(255, 200, 0);
    public static final JPowerGraphColor GREEN = new JPowerGraphColor(0, 255, 0);
    public static final JPowerGraphColor DARK_GREEN = new JPowerGraphColor(0, 128, 0);
    public static final JPowerGraphColor CYAN  = new JPowerGraphColor(0, 255, 255);
    public static final JPowerGraphColor DARK_CYAN  = new JPowerGraphColor(0, 128, 128);
    public static final JPowerGraphColor YELLOW = new JPowerGraphColor(255, 255, 0);
    public static final JPowerGraphColor DARK_YELLOW = new JPowerGraphColor(128, 128, 0);
    public static final JPowerGraphColor BLACK = new JPowerGraphColor(0, 0, 0);
    public static final JPowerGraphColor WHITE = new JPowerGraphColor(255, 255, 255);
    public static final JPowerGraphColor LIGHT_GRAY = new JPowerGraphColor(192, 192, 192);
    public static final JPowerGraphColor GRAY = new JPowerGraphColor(128, 128, 128);
    public static final JPowerGraphColor DARK_GRAY = new JPowerGraphColor(64, 64, 64);
    
    public int red;
    public int green;
    public int blue;

    public JPowerGraphColor(int theRed, int theGreen, int theBlue) {
        this.red = theRed;
        this.green = theGreen;
        this.blue = theBlue;
    }

    public int getBlue() {
        return blue;
    }

    public int getGreen() {
        return green;
    }

    public int getRed() {
        return red;
    }
    
    public boolean equals (Object o){
        if (!(o instanceof JPowerGraphColor)){
            return false;
        }
        return ((JPowerGraphColor) o).getRed() == red && ((JPowerGraphColor) o).getGreen() == green && ((JPowerGraphColor) o).getBlue() == blue;
    }
    
    public int hashCode(){
        int result = 17;
        result = 37 * result + new Integer(red).hashCode();
        result = 37 * result + new Integer(green).hashCode();
        result = 37 * result + new Integer(blue).hashCode();
        return result;
    }
}
