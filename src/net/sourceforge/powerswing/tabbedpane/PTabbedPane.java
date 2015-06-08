package net.sourceforge.powerswing.tabbedpane;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.plaf.metal.MetalTabbedPaneUI;

import net.sourceforge.powerswing.tabbedpane.listener.PTabbedPaneMouseEvent;
import net.sourceforge.powerswing.tabbedpane.listener.PTabbedPaneMouseListener;
import net.sourceforge.powerswing.tabbedpane.listener.PTabbedPaneMouseTabIconListener;
import net.sourceforge.powerswing.tabbedpane.listener.PTabbedPaneMouseTabListener;
import net.sourceforge.powerswing.tabbedpane.ui.BasicPTabbedPaneReverseIconUI;
import net.sourceforge.powerswing.tabbedpane.ui.CloseIconUI;
import net.sourceforge.powerswing.tabbedpane.ui.MetalPTabbedPaneReverseIconUI;
import net.sourceforge.powerswing.tabbedpane.ui.MotifPTabbedPaneReverseIconUI;
import net.sourceforge.powerswing.tabbedpane.ui.WindowsPTabbedPaneReverseIconUI;

import com.sun.java.swing.plaf.motif.MotifTabbedPaneUI;
import com.sun.java.swing.plaf.windows.WindowsTabbedPaneUI;

public class PTabbedPane extends JTabbedPane {

    private HashMap <PTabbedPaneMouseListener, MouseListener> map1 = new HashMap <PTabbedPaneMouseListener, MouseListener>();
    private HashMap <PTabbedPaneMouseTabListener, MouseMotionListener> map2 = new HashMap <PTabbedPaneMouseTabListener, MouseMotionListener>();
    private HashMap <PTabbedPaneMouseTabIconListener, MouseMotionListener> map3 = new HashMap <PTabbedPaneMouseTabIconListener, MouseMotionListener>();
    
    private boolean showXs = false;
    
    public PTabbedPane(boolean showXs) {
        this(TOP, WRAP_TAB_LAYOUT, showXs);
    }

    public PTabbedPane(int tabPlacement, boolean showXs) {
        this(tabPlacement, WRAP_TAB_LAYOUT, showXs);
    }

    public PTabbedPane(int tabPlacement, int tabLayoutPolicy, boolean showXs) {
        super(tabPlacement, tabLayoutPolicy);
        
        this.showXs = showXs;
        
        if (this.getUI() instanceof WindowsTabbedPaneUI){
            this.setUI(new WindowsPTabbedPaneReverseIconUI());
        }
        else if (this.getUI() instanceof MetalTabbedPaneUI){
            this.setUI(new MetalPTabbedPaneReverseIconUI());
        }
        else if (this.getUI() instanceof MotifTabbedPaneUI){
            this.setUI(new MotifPTabbedPaneReverseIconUI());
        }
        else {
            this.setUI(new BasicPTabbedPaneReverseIconUI());
        }
        
        if (showXs){
            addPTabbedPaneMouseTabIconListener(new PTabbedPaneMouseTabIconListener() {
                public void mouseEnteredTabIcon(PTabbedPaneMouseEvent e) {
                    setIconAt(e.getTabIndex(), getCloseIconUI().getCloseIconHighlighted());
                }

                public void mouseExitedTabIcon(PTabbedPaneMouseEvent e) {
                    if (e.getTabIndex() < getTabCount()){
                        setIconAt(e.getTabIndex(), getCloseIconUI().getCloseIconNotHighlighted());
                    }
                }
            });
        }
    }

    public void addTab(String title, Component component) {
        super.addTab(title, component);
        if (showXs){
            super.setIconAt(getTabCount() - 1, getCloseIconUI().getCloseIconNotHighlighted());
        }
    }
    public void addTab(String title, Icon icon, Component component, String tip) {
        if (showXs){
            throw new UnsupportedOperationException();
        }
        super.addTab(title, icon, component, tip);
    }
    public void addTab(String title, Icon icon, Component component) {
        if (showXs){
            throw new UnsupportedOperationException();
        }
        super.addTab(title, icon, component);
    }
    public void addPTabbedPaneMouseListener(final PTabbedPaneMouseListener thePTabbedPaneMouseListener){
        MouseListener ml = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int index = getTabIndex(e);
                boolean inIcon = inIcon(e, index);
                thePTabbedPaneMouseListener.mouseClicked(new PTabbedPaneMouseEvent(e, index, inIcon));
            }

            public void mousePressed(MouseEvent e) {
                int index = getTabIndex(e);
                boolean inIcon = inIcon(e, index);
                thePTabbedPaneMouseListener.mousePressed(new PTabbedPaneMouseEvent(e, index, inIcon));
            }

            public void mouseReleased(MouseEvent e) {
                int index = getTabIndex(e);
                boolean inIcon = inIcon(e, index);
                thePTabbedPaneMouseListener.mouseReleased(new PTabbedPaneMouseEvent(e, index, inIcon));
            }
        };
        map1.put(thePTabbedPaneMouseListener, ml);
        this.addMouseListener(ml);
    }
    
    public void removePTabbedPaneMouseListener(final PTabbedPaneMouseListener thePTabbedPaneMouseListener){
        MouseListener ml = map1.get(thePTabbedPaneMouseListener);
        map1.remove(thePTabbedPaneMouseListener);
        this.removeMouseListener(ml);
    }
    
    public PTabbedPaneMouseListener[] getPTabbedPaneMouseListeners(){
        return map1.keySet().toArray(new PTabbedPaneMouseListener[]{});
    }
    
    public void addPTabbedPaneMouseTabListener(final PTabbedPaneMouseTabListener thePTabbedPaneMouseTabListener){
        MouseMotionListener mml = new MouseMotionAdapter() {
            private int lastTabIndex = -1;
            private boolean lastInIcon = false;
            
            public void mouseMoved(MouseEvent e) {
                doWork(e);
            }
            
            public void mouseDragged(MouseEvent e) {
                doWork(e);
            }
            
            public void doWork(MouseEvent e){
                int index = getTabIndex(e);
                boolean inIcon = inIcon(e, index);
                if (index != lastTabIndex){
                    if (lastTabIndex != -1){
                        thePTabbedPaneMouseTabListener.mouseExitedTab(new PTabbedPaneMouseEvent(e, lastTabIndex, lastInIcon));
                    }
                    if (index != -1){
                        thePTabbedPaneMouseTabListener.mouseEnteredTab(new PTabbedPaneMouseEvent(e, index, inIcon));
                    }
                }
                lastTabIndex = index;
                lastInIcon = inIcon;
            }
            
        };
        map2.put(thePTabbedPaneMouseTabListener, mml);
        this.addMouseMotionListener(mml);
    }
    
    public void removePTabbedPaneMouseTabListener(final PTabbedPaneMouseTabListener thePTabbedPaneMouseTabListener){
        MouseMotionListener mml = map2.get(thePTabbedPaneMouseTabListener);
        map2.remove(thePTabbedPaneMouseTabListener);
        this.removeMouseMotionListener(mml);
    }
    
    public void addPTabbedPaneMouseTabIconListener(final PTabbedPaneMouseTabIconListener thePTabbedPaneMouseTabIconListener){
        MouseMotionListener mml = new MouseMotionAdapter() {
            private int lastTabIndex = -1;
            private boolean lastInIcon = false;
            
            public void mouseMoved(MouseEvent e) {
                doWork(e);
            }
            
            public void mouseDragged(MouseEvent e) {
                doWork(e);
            }
            
            public void doWork(MouseEvent e){
                int index = getTabIndex(e);
                boolean inIcon = inIcon(e, index);
                if (index != lastTabIndex || inIcon != lastInIcon){
                    if (lastInIcon){
                        thePTabbedPaneMouseTabIconListener.mouseExitedTabIcon(new PTabbedPaneMouseEvent(e, lastTabIndex, lastInIcon));
                    }
                    if (inIcon){
                        thePTabbedPaneMouseTabIconListener.mouseEnteredTabIcon(new PTabbedPaneMouseEvent(e, index, inIcon));
                    }
                }
                lastTabIndex = index;
                lastInIcon = inIcon;
            }
        };
        map3.put(thePTabbedPaneMouseTabIconListener, mml);
        this.addMouseMotionListener(mml);
    }
    
    public PTabbedPaneMouseTabListener[] getPTabbedPaneMouseTabListeners(){
        return map2.keySet().toArray(new PTabbedPaneMouseTabListener[]{});
    }
    
    public void removePTabbedPaneMouseTabIconListener(final PTabbedPaneMouseTabIconListener thePTabbedPaneMouseTabIconListener){
        MouseMotionListener mml = map3.get(thePTabbedPaneMouseTabIconListener);
        map3.remove(thePTabbedPaneMouseTabIconListener);
        this.removeMouseMotionListener(mml);
    }
    
    public PTabbedPaneMouseTabIconListener[] getPTabbedPaneMouseTabIconListeners(){
        return map3.keySet().toArray(new PTabbedPaneMouseTabIconListener[]{});
    }

    private int getTabIndex(MouseEvent e) {
        int index = -1;
        for (int i = 0; i < getTabCount(); i++) {
            Rectangle r = getUI().getTabBounds(this, i);
            if (r.contains(e.getX(), e.getY())){
                index = i;
                break;
            }
        }
        return index;
    }
    
    protected boolean inIcon(MouseEvent e, int index) {
        if (index == -1){
            return false;
        }
        return getCloseIconUI().getIconBounds(index).contains(e.getX(), e.getY());
    }
    
    public CloseIconUI getCloseIconUI(){
        return (CloseIconUI) super.getUI();
    }
}