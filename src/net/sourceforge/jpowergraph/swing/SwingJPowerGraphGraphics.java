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

package net.sourceforge.jpowergraph.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.jpowergraph.pane.JGraphPane;
import net.sourceforge.jpowergraph.swtswinginteraction.JPowerGraphGraphics;
import net.sourceforge.jpowergraph.swtswinginteraction.color.JPowerGraphColor;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphDimension;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphRectangle;


public class SwingJPowerGraphGraphics implements JPowerGraphGraphics{

    private Graphics2D graphics;
    private FontMetrics fontMetrics;
    private Font fontbackup;
    private BufferedImage image;
    private Stroke DASH_STROKE;
    private Stroke NORMAL_STROKE;

    public SwingJPowerGraphGraphics(Graphics2D theGraphics) {
        this.graphics = theGraphics;
        this.NORMAL_STROKE = graphics.getStroke();
        this.DASH_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, new float[]{4}, 0.0f);
    }
    
    public SwingJPowerGraphGraphics(BufferedImage theImage) {
        this.image = theImage;
        this.graphics = image.createGraphics();
        this.NORMAL_STROKE = graphics.getStroke();
        this.DASH_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, new float[]{4}, 0.0f);
    }

    public JPowerGraphColor getBackground() {
        Color c = graphics.getColor();
        return new JPowerGraphColor(c.getRed(), c.getGreen(), c.getBlue());
    }
    
    public JPowerGraphColor getForeground() {
        Color c = graphics.getColor();
        return new JPowerGraphColor(c.getRed(), c.getGreen(), c.getBlue());
    }

    public void setForeground(JPowerGraphColor theColor) {
        graphics.setColor(new Color(theColor.getRed(), theColor.getGreen(), theColor.getBlue()));
    }

    public void setBackground(JPowerGraphColor theColor) {
        graphics.setColor(new Color(theColor.getRed(), theColor.getGreen(), theColor.getBlue()));
    }
    
    public int getStringWidth(String theString){
        if (fontMetrics == null){
            fontMetrics = graphics.getFontMetrics(graphics.getFont());
        }
        return fontMetrics.stringWidth(theString);
    }
    
    public int getAscent(){
        if (fontMetrics == null){
            fontMetrics = graphics.getFontMetrics(graphics.getFont());
        }
        return fontMetrics.getAscent() - 2;
    }
    
    public int getDescent(){
        if (fontMetrics == null){
            fontMetrics = graphics.getFontMetrics(graphics.getFont());
        }
        return fontMetrics.getDescent() - 1;
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        graphics.drawLine(x1, y1, x2, y2);
    }
    // bingxue
    public void drawArc (int x, int y, int width, int height,
            int startAngle, int arcAngle){
    	graphics.drawArc(x, y, width, height, startAngle, arcAngle);
    }
    
    public void drawString(String theString, int x, int y, int numlines){
        y += ((7 * numlines) + 5 - ((getAscent() - getDescent()) * (numlines - 1)));
        graphics.drawString(theString, x, y);
    }

    public void drawRectangle(int x, int y, int width, int height) {
        graphics.drawRect(x, y, width, height);
    }
    
    public void drawRoundRectangle(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        graphics.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }
    
    public void drawOval(int x, int y, int width, int height) {
        graphics.drawOval(x, y, width, height);
    }
    
    public void drawPolygon(int[] theIntegers) {
        List <Integer> pointsX = new ArrayList <Integer> ();
        List <Integer> pointsY = new ArrayList <Integer> ();
        for (int i = 0; i < theIntegers.length; i = i + 2){
            pointsX.add(theIntegers[i]);
            pointsY.add(theIntegers[i+1]);
        }
        
        int[] xs = new int[pointsX.size()];
        for (int i = 0; i < pointsX.size(); i++){
            xs[i] = pointsX.get(i);
        }
        int[] ys = new int[pointsY.size()];
        for (int i = 0; i < pointsY.size(); i++){
            ys[i] = pointsY.get(i);
        }
        graphics.drawPolygon(xs, ys, xs.length);
    }

    public void fillRectangle(int x, int y, int width, int height) {
        graphics.fillRect(x, y, width, height);
    }
    
    public void fillRoundRectangle(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        graphics.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }
    
    public void fillOval(int x, int y, int width, int height) {
        graphics.fillOval(x, y, width, height);
    }
    
    public void fillPolygon(int[] theIntegers) {
        List <Integer> pointsX = new ArrayList <Integer> ();
        List <Integer> pointsY = new ArrayList <Integer> ();
        for (int i = 0; i < theIntegers.length; i = i + 2){
            pointsX.add(theIntegers[i]);
            pointsY.add(theIntegers[i+1]);
        }
        
        int[] xs = new int[pointsX.size()];
        for (int i = 0; i < pointsX.size(); i++){
            xs[i] = pointsX.get(i);
        }
        int[] ys = new int[pointsY.size()];
        for (int i = 0; i < pointsY.size(); i++){
            ys[i] = pointsY.get(i);
        }
        graphics.fillPolygon(xs, ys, xs.length);
    }

    public void storeFont() {
        fontbackup = graphics.getFont();
    }

    public void setFontFromJGraphPane(JGraphPane graphPane) {
        graphics.setFont(((SwingJGraphPane) graphPane).getFont());
    }

    public void restoreFont() {
        if (fontbackup != null){
            graphics.setFont(fontbackup);
            fontbackup = null;
        }
    }

    public JPowerGraphRectangle getClipping() {
        Rectangle r = graphics.getClipBounds();
        return new JPowerGraphRectangle(r.x, r.y, r.width, r.height);
    }

    public boolean getAntialias() {
        return graphics.getRenderingHint(RenderingHints.KEY_ANTIALIASING).equals(RenderingHints.VALUE_ANTIALIAS_ON);
    }

    public void setAntialias(boolean antialias) {
        if (antialias) {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        }
        else{
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
        }
    }

    public JPowerGraphGraphics getSubJPowerGraphGraphics(JPowerGraphDimension theDimension) {
        return new SwingJPowerGraphGraphics(new BufferedImage(theDimension.width, theDimension.height, BufferedImage.TYPE_INT_RGB));
    }

    public void drawSubJPowerGraph(JPowerGraphGraphics theGraphics, int i, int j) {
        SwingJPowerGraphGraphics subG = (SwingJPowerGraphGraphics) theGraphics;
        graphics.drawImage(subG.getImage(), 0, 0, new ImageObserver() {
            public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                return true;
            }
        });
    }

    public BufferedImage getImage() {
        return image;
    }
    
    public void dispose() {
    }

    public boolean isDisposed() {
        return false;
    }
    
    public int getLineWidth() {
        return (int) ((BasicStroke) graphics.getStroke()).getLineWidth();
    }

    public void setLineWidth(int lineWidth) {
        BasicStroke st = (BasicStroke) graphics.getStroke();
        graphics.setStroke(new BasicStroke(lineWidth, st.getEndCap(), st.getLineJoin()));
    }

    public void setLineDashed(boolean dashedLine) {
        if (dashedLine){
            graphics.setStroke(DASH_STROKE);
        }
        else{
            graphics.setStroke(NORMAL_STROKE);
        }
    }
}
