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

package net.sourceforge.jpowergraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;


public class GroupLegendItem implements LegendItem{

    private String description;
    private ArrayList <Class> groupClasses;
    
    private ArrayList <LegendItem> legendItems;
    private ArrayList <GroupLegendItem> groups;
    private boolean expanded = true;
    
    public GroupLegendItem() {
        this("");
    }
    
    public GroupLegendItem(String theDescription) {
        this.description = theDescription;
        this.groupClasses = new ArrayList <Class> ();
        this.legendItems = new ArrayList <LegendItem> ();
        this.groups = new ArrayList <GroupLegendItem> ();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String theDescription) {
        this.description = theDescription;
    }

    public void addGroupClass(Class theGroupClass) {
        this.groupClasses.add(theGroupClass);
    }
    
    public void removeGroupClass(Class theGroupClass) {
        this.groupClasses.remove(theGroupClass);
    }
    
    public void clearGroupClass() {
        this.groupClasses.clear();
    }
    
    public boolean containedInGroup(Class theClass){
        if (groupClasses.contains(theClass)){
            return true;
        }
        for (GroupLegendItem group : groups){
            if (group.containedInGroup(theClass)){
                return true;
            }
        }
        return false;
    }
    
    public void add(NodeLegendItem theNodeLegendItem) {
        for (GroupLegendItem group : groups){
            if (group.containedInGroup(theNodeLegendItem.getNodeClass())){
                group.add(theNodeLegendItem);
                return;
            }
        }
        if (!legendItems.contains(theNodeLegendItem)){
            this.legendItems.add(theNodeLegendItem);
        }
    }
    
    public void add(GroupLegendItem theGroupLegendItem) {
        this.groups.add(theGroupLegendItem);
        this.legendItems.add(theGroupLegendItem);
    }
    
    public ArrayList <LegendItem> getLegendItems(){
        ArrayList <LegendItem> result = new ArrayList <LegendItem> ();
        result.addAll(legendItems);
        
        Collections.sort(result, new Comparator <LegendItem> () {
            public int compare(LegendItem o1, LegendItem o2) {
                if (o1 instanceof GroupLegendItem && !(o2 instanceof GroupLegendItem)){
                    return -1;
                }
                else if (o2 instanceof GroupLegendItem && !(o1 instanceof GroupLegendItem)){
                    return 1;
                }
                else if (o1 instanceof GroupLegendItem && o2 instanceof GroupLegendItem){
                    Integer o1index = groups.indexOf(o1);
                    Integer o2index = groups.indexOf(o2);
                    return o1index.compareTo(o2index);
                }
                else if (o1 instanceof NodeLegendItem && o2 instanceof NodeLegendItem){
                    Integer o1index = groupClasses.indexOf(((NodeLegendItem) o1).getNodeClass());
                    Integer o2index = groupClasses.indexOf(((NodeLegendItem) o2).getNodeClass());
                    return o1index.compareTo(o2index);
                }
                return 0;
            }
        });
        
        return result;
    }

    public void clear() {
        for (Iterator <LegendItem> i = legendItems.iterator(); i.hasNext();) {
            LegendItem legendItem = i.next();
            if (legendItem instanceof NodeLegendItem){
                i.remove();
            }
            else if (legendItem instanceof GroupLegendItem){
                ((GroupLegendItem) legendItem).clear();
            }
        }
    }

    public boolean isExpanded() {
        return expanded;
    }
    
    public void setExpanded(boolean isExpanded){
        this.expanded  = isExpanded;        
    }
}