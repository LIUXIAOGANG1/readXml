package net.sourceforge.jpowergraph.swing;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;

import net.sourceforge.jpowergraph.Graph;
import net.sourceforge.jpowergraph.GraphListener;
import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.lens.Lens;
import net.sourceforge.jpowergraph.lens.LensListener;
import net.sourceforge.jpowergraph.lens.LensSet;
import net.sourceforge.jpowergraph.lens.TranslateLens;

/**
 * A scroll pane for the graph.
 */
public class SwingJGraphScrollPane extends JPanel {
    /** The graph pane being visualized. */
    protected SwingJGraphPane m_graphPane;
    /** The horizontal scroll bar. */
    protected JScrollBar m_horizontalScrollBar;
    /** The vertical scroll bar. */
    protected JScrollBar m_verticalScrollBar;
    /** The total area of the graph in graph coordinates. */
    protected Rectangle2D m_graphArea;
    /** The translation lens. */
    protected TranslateLens m_translateLens;
    /** Set to <code>true</code> if scroll bars are being programmatically updated. */
    protected boolean m_updatingScrollBars;

    /**
     * Creates an instance of this class.
     *
     * @param graphPane                     the graph pane
     * @param translateLens                 the lens for translation
     */
    public SwingJGraphScrollPane(SwingJGraphPane graphPane, LensSet theLensSet) {
        super(new GridBagLayout());
        m_graphPane=graphPane;
        m_translateLens = (TranslateLens) theLensSet.getFirstLensOfType(TranslateLens.class);
        m_graphArea=new Rectangle2D.Double();
        m_horizontalScrollBar=new JScrollBar(JScrollBar.HORIZONTAL);
        m_horizontalScrollBar.addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if (!m_updatingScrollBars)
                    scrollBy(e.getValue(),0);
            }
        });
        m_verticalScrollBar=new JScrollBar(JScrollBar.VERTICAL);
        m_verticalScrollBar.addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if (!m_updatingScrollBars)
                    scrollBy(0,e.getValue());
            }
        });
        
        m_graphPane.getLens().addLensListener(new LensListener() {
            public void lensUpdated(Lens lens) {
                updateScrollBars();
            }
        });
        
        m_graphPane.getGraph().addGraphListener(new GraphHandler());
        
        m_graphPane.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (!m_updatingScrollBars){
                    if ((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK)!=0){
                        scrollBy(e.getWheelRotation(), 0);
                    }
                    else{
                        scrollBy(0,e.getWheelRotation());
                    }
                }
            }
        });
        
        GridBagConstraints gbc=new GridBagConstraints(0,0,1,1,1.0f,1.0f,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0);
        add(new GraphScroller(m_graphPane),gbc);
        gbc.gridx=1;
        gbc.weightx=0.0f;
        gbc.fill=GridBagConstraints.VERTICAL;
        add(m_verticalScrollBar,gbc);
        gbc.gridx=0;
        gbc.gridy=1;
        gbc.weightx=1.0f;
        gbc.weighty=0.0f;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        add(m_horizontalScrollBar,gbc);
        setGraphArea(-500.0,-500.0,1000.0,1000.0);
    }
    /**
     * Scrolls the graph by specified amount.
     *
     * @param deltaX                        the amount to scroll in the X direction
     * @param deltaY                        the amount to scroll in the Y direction
     */
    public void scrollBy(int deltaX,int deltaY) {
        int newValueX=m_horizontalScrollBar.getValue()+deltaX;
        int newValueY=m_verticalScrollBar.getValue()+deltaY;
        if (newValueX<m_horizontalScrollBar.getMinimum())
            deltaX=m_horizontalScrollBar.getMinimum()-m_horizontalScrollBar.getValue();
        if (newValueX>m_horizontalScrollBar.getMaximum()-m_horizontalScrollBar.getVisibleAmount())
            deltaX=m_horizontalScrollBar.getMaximum()-m_horizontalScrollBar.getVisibleAmount()-m_horizontalScrollBar.getValue();
        if (newValueY<m_verticalScrollBar.getMinimum())
            deltaY=m_verticalScrollBar.getMinimum()-m_verticalScrollBar.getValue();
        if (newValueY>m_verticalScrollBar.getMaximum()-m_verticalScrollBar.getVisibleAmount())
            deltaY=m_verticalScrollBar.getMaximum()-m_verticalScrollBar.getVisibleAmount()-m_verticalScrollBar.getValue();
        Point2D point=new Point2D.Double(deltaX,deltaY);
        m_graphPane.getLens().undoLens(m_graphPane, point);
        m_translateLens.setTranslate(-point.getX(),-point.getY());
        for (int i=m_graphPane.getComponentCount()-1;i>=0;i--) {
            Component component=m_graphPane.getComponent(i);
            component.setLocation(component.getX()-deltaX,component.getY()-deltaY);
        }
    }
    /**
     * Sets the area of the graph.
     *
     * @param left                          the left boundary
     * @param top                           the top boundary
     * @param width                         the width of the area
     * @param height                        the height of the area
     */
    public void setGraphArea(double left,double top,double width,double height) {
        m_graphArea.setFrame(left,top,width,height);
        updateScrollBars();
    }
    /**
     * Updates the position and size of the scroll bars.
     */
    protected void updateScrollBars() {
        m_updatingScrollBars=true;
        Point2D[] points=new Point2D.Double[4];
        points[0]=new Point2D.Double(m_graphArea.getMinX(),m_graphArea.getMinY());
        m_graphPane.getLens().applyLens(m_graphPane, points[0]);
        points[1]=new Point2D.Double(m_graphArea.getMinX(),m_graphArea.getMaxY());
        m_graphPane.getLens().applyLens(m_graphPane, points[1]);
        points[2]=new Point2D.Double(m_graphArea.getMaxX(),m_graphArea.getMaxY());
        m_graphPane.getLens().applyLens(m_graphPane, points[2]);
        points[3]=new Point2D.Double(m_graphArea.getMaxX(),m_graphArea.getMinY());
        m_graphPane.getLens().applyLens(m_graphPane, points[3]);
        int minX=Integer.MAX_VALUE;
        int maxX=Integer.MIN_VALUE;
        int minY=Integer.MAX_VALUE;
        int maxY=Integer.MIN_VALUE;
        for (int i=0;i<4;i++) {
            if (points[i].getX()<minX)
                minX=(int)points[i].getX();
            if (points[i].getX()>maxX)
                maxX=(int)points[i].getX();
            if (points[i].getY()<minY)
                minY=(int)points[i].getY();
            if (points[i].getY()>maxY)
                maxY=(int)points[i].getY();
        }
        int width=m_graphPane.getWidth();
        int height=m_graphPane.getHeight();
        m_horizontalScrollBar.setMinimum(minX+width/2);
        m_horizontalScrollBar.setMaximum(maxX+width/2);
        m_horizontalScrollBar.setVisibleAmount(width);
        m_horizontalScrollBar.setBlockIncrement(width-10);
        m_horizontalScrollBar.setUnitIncrement(width/5);
        m_horizontalScrollBar.setValue(0);
        m_verticalScrollBar.setMinimum(minY+height/2);
        m_verticalScrollBar.setMaximum(maxY+height/2);
        m_verticalScrollBar.setVisibleAmount(height);
        m_verticalScrollBar.setBlockIncrement(height-10);
        m_verticalScrollBar.setUnitIncrement(height/5);
        m_verticalScrollBar.setValue(0);
        m_updatingScrollBars=false;
    }

    /**
     * The pane containing the graph pane. This class exists to catch the scrollRectToVisible method calls.
     */
    protected class GraphScroller extends JPanel implements ComponentListener {
        public GraphScroller(SwingJGraphPane graphPane) {
            super(new BorderLayout());
            add(graphPane,BorderLayout.CENTER);
            graphPane.addComponentListener(this);
        }
        public void scrollRectToVisible(Rectangle contentRect) {
            int deltaX=positionAdjustment(getWidth(),contentRect.width,contentRect.x);
            int deltaY=positionAdjustment(getHeight(),contentRect.height,contentRect.y);
            if (deltaX!=0 || deltaY!=0)
                scrollBy(deltaX,deltaY);
        }
        /*
         * This method is used by the scrollToRect method to determine the
         * proper direction and amount to move by. The integer variables are named
         * width, but this method is applicable to height also. The code assumes that
         * parentWidth/childWidth are positive and childAt can be negative. This method is
         * copied from JViewport.
         */
        protected int positionAdjustment(int parentWidth,int childWidth,int childAt) {
            if (childAt>=0 && childWidth+childAt<=parentWidth)
                return 0;
            if (childAt<=0 && childWidth+childAt>=parentWidth)
                return 0;
            if (childAt>0 && childWidth<=parentWidth)
                return childAt-parentWidth+childWidth;
            if (childAt>=0 && childWidth>=parentWidth)
                return childAt;
            if (childAt<=0 && childWidth<=parentWidth)
                return childAt;
            if (childAt<0 && childWidth>=parentWidth)
                return childAt-parentWidth+childWidth;
            return 0;
        }
        public void componentResized(ComponentEvent e) {
            updateScrollBars();
        }
        public void componentMoved(ComponentEvent e) {
        }
        public void componentShown(ComponentEvent e) {
        }
        public void componentHidden(ComponentEvent e) {
        }
    }

    /**
     * The handler for the graph events.
     */
    protected class GraphHandler implements GraphListener {
        public void graphLayoutUpdated(final Graph graph) {
            if (SwingUtilities.isEventDispatchThread())
                updateGraphBoundsMainThread(graph);
            else
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        updateGraphBoundsMainThread(graph);
                    }
                });
        }
        public void graphUpdated(Graph graph) {
        }
        public void graphContentsChanged(final Graph graph) {
            if (SwingUtilities.isEventDispatchThread())
                graphContentsChangedMainThread(graph);
            else
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        graphContentsChangedMainThread(graph);
                    }
                });
        }
        public void elementsAdded(Graph graph,Collection nodes,Collection edges) {
        }
        public void elementsRemoved(Graph graph,Collection nodes,Collection edges) {
        }
        protected void graphContentsChangedMainThread(Graph graph) {
            m_translateLens.setTranslate(0,0);
            setGraphArea(-500.0,-500.0,1000.0,1000.0);
            updateGraphBoundsMainThread(graph);
        }
        protected void updateGraphBoundsMainThread(Graph graph) {
            double minX=Double.MAX_VALUE;
            double maxX=Double.MIN_VALUE;
            double minY=Double.MAX_VALUE;
            double maxY=Double.MIN_VALUE;
            synchronized (graph) {
                Iterator iterator=graph.getVisibleNodes().iterator();
                while (iterator.hasNext()) {
                    Node node=(Node)iterator.next();
                    if (node.getX()<minX)
                        minX=node.getX();
                    if (node.getX()>maxX)
                        maxX=node.getX();
                    if (node.getY()<minY)
                        minY=node.getY();
                    if (node.getY()>maxY)
                        maxY=node.getY();
                }
            }
            minX-=100.0;
            maxX+=100.0;
            minY-=100.0;
            maxY+=100.0;
            if (minX<m_graphArea.getMinX() || maxX>m_graphArea.getMaxX() || minY<m_graphArea.getMinY() || maxY>m_graphArea.getMaxY()) {
                minX=Math.min(minX,m_graphArea.getMinX());
                maxX=Math.max(maxX,m_graphArea.getMaxX());
                minY=Math.min(minY,m_graphArea.getMinY());
                maxY=Math.max(maxY,m_graphArea.getMaxY());
                setGraphArea(minX,minY,maxX-minX,maxY-minY);
            }
        }
    }
}
