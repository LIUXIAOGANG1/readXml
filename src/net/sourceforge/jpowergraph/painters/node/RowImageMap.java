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

package net.sourceforge.jpowergraph.painters.node;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.jpowergraph.swtswinginteraction.JPowerGraphGraphics;
import net.sourceforge.jpowergraph.swtswinginteraction.color.JPowerGraphColor;


public class RowImageMap {

    private Map <Double, Map <Key, JPowerGraphGraphics>> scaleRowMap = new HashMap <Double, Map <Key, JPowerGraphGraphics>> ();
    private boolean disposed = false;

    public void register(Double theScale, Integer theRowSize, JPowerGraphGraphics theGraphics, JPowerGraphColor theBackgroundColor, JPowerGraphColor theNodeColor){
        Map <Key, JPowerGraphGraphics> row = null;
        if (scaleRowMap.containsKey(theScale)){
            row = scaleRowMap.get(theScale);
        }
        else{
            row = new HashMap <Key, JPowerGraphGraphics> ();
            scaleRowMap.put(theScale, row);
        }
        
        row.put(new Key(theRowSize, theBackgroundColor, theNodeColor), theGraphics);
    }
    
    public JPowerGraphGraphics retrieve(Double theScale, Integer theRowSize, JPowerGraphColor theBackgroundColor, JPowerGraphColor theNodeColor){
        Map <Key, JPowerGraphGraphics> row = row = scaleRowMap.get(theScale);
        if(row == null){
            return null;
        }
        return row.get(new Key(theRowSize, theBackgroundColor, theNodeColor));
    }
    
    public void dispose(){
        for (Double scale : scaleRowMap.keySet()){
            Map <Key, JPowerGraphGraphics> row = row = scaleRowMap.get(scale);
            for (Key p : row.keySet()){
                JPowerGraphGraphics g = row.get(p);
                g.dispose();
            }
        }
        scaleRowMap.clear();
        disposed = true;
    }
    
    public boolean isDisposed(){
        return disposed;
    }
    
    public class Key{
        private Integer integer;
        private JPowerGraphColor background;
        private JPowerGraphColor nodeColor;
        
        public Key (Integer theInteger, JPowerGraphColor theBackground, JPowerGraphColor theNodeColor){
            this.integer = theInteger;
            this.background = theBackground;
            this.nodeColor = theNodeColor;
        }
        
        public boolean equals (Object o){
            if (!(o instanceof Key)){
                return false;
            }
            return ((Key) o).integer.equals(integer) && ((Key) o).background.equals(background) && ((Key) o).nodeColor.equals(nodeColor);
        }
        
        public int hashCode(){
            int result = 17;
            result = 37 * result + integer.hashCode();
            result = 37 * result + background.hashCode();
            result = 37 * result + nodeColor.hashCode();
            return result;
        }
    }
}