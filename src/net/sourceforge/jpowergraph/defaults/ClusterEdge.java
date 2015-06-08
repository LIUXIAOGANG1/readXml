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

package net.sourceforge.jpowergraph.defaults;

import net.sourceforge.jpowergraph.ClusterNode;
import net.sourceforge.jpowergraph.Node;

public class ClusterEdge extends DefaultEdge {

    private boolean showNodesInCluster;

    public ClusterEdge(Node from, ClusterNode to, boolean theShowNodesInCluster) {
        super(from, to);
        this.showNodesInCluster = theShowNodesInCluster;
    }
    
    public double getLength() {
        if (!showNodesInCluster){
           return super.getLength();
        }
        ClusterNode toNode = (ClusterNode) getTo();
        
        int numInstances = toNode.size();
        int leftRightPad = 4;
        int hPadBetweenInstances = (int) (2 * getInstanceScale(numInstances));
        int numOuterCircles = countOuterCircles(numInstances);
        int numRows = (numOuterCircles * 2) + 1;
        int[] rowSizes = getRowSizes(numInstances, numRows);
        int numCols = Integer.MIN_VALUE;
        for (int i = 0; i < rowSizes.length; i++){
            numCols = Math.max(numCols, rowSizes[i]);
        }

        return Math.max(40, ((18 * numCols) + (hPadBetweenInstances * (numCols - 1)) + (leftRightPad * 2)) / 2);
    }
    
    private int[] getRowSizes(int numInstances, int numRows) {
        int[] result = new int[numRows];
        for (int i = 0; i < result.length; i++) {
            int rowIndex = ((numRows - 1) / 2) - i;
            if (rowIndex < 0) {
                rowIndex = rowIndex * -1;
            }
            result[i] = numRows - rowIndex;
        }

        int numCurrentNodes = 0;
        for (int i = 0; i < result.length; i++) {
            numCurrentNodes += result[i];
        }

        int numOverNodes = numCurrentNodes - numInstances;
        while (numOverNodes > 0) {
            while((result[result.length - 1] > 0 || result[0] > 0) && numOverNodes != 0){
                if (result[result.length - 1] > 0 && numOverNodes != 0){
                    result[result.length - 1] -= 1;
                    numOverNodes -= 1;
                }
                if (result[0] > 0 && numOverNodes != 0){
                    result[0] -= 1;
                    numOverNodes -= 1;
                }
            }
            
            for (int i = result.length - 2; i >= 0; i--) {
                if (numOverNodes != 0 && result[i] > 0) {
                    result[i] -= 1;
                    numOverNodes -= 1;
                }
            }
        }

        return result;
    }

    private int countOuterCircles(int numInstances) {
        int numOuterCircles = 0;

        int temp = numInstances - 1;
        int nextCircle = 1;
        while (temp > 0) {
            numOuterCircles++;
            temp -= nextCircle * 6;
            nextCircle++;
        }

        return numOuterCircles;
    }
    
    private double getInstanceScale(int numInstances) {
        double scale = 0.5;
        if (numInstances > 100){
            scale -= 0.1;
        }
        if (numInstances > 200){
            scale -= 0.1;
        }
        if (numInstances > 400){
            scale -= 0.1;
        }
        if (numInstances > 800){
            scale -= 0.1;
        }
        return scale;
    }
}
