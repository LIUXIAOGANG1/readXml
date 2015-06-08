package net.sourceforge.powerswing.preferences.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import net.sourceforge.powerswing.PJButton;
import net.sourceforge.powerswing.focus.SpecifiedOrderFocusTraversalPolicy;
import net.sourceforge.powerswing.localization.PBundle;
import net.sourceforge.powerswing.panel.PPanel;
import net.sourceforge.powerswing.preferences.Preferences;
import net.sourceforge.powerswing.preferences.tree.PreferenceTree;
import net.sourceforge.powerswing.preferences.tree.PreferenceTreeNode;
import net.sourceforge.powerswing.util.PCentre;

/**
 * 
 * 
 * @author mkerrigan2
 */
public class PreferencesPanel extends JPanel {

    private JLabel label;
    private JPanel displayPanel;
    private PreferenceTree tree;
    private PJButton ok;
    private PJButton ca;
    
    private PBundle messages;
    private ActionListener finished;
    private ActionListener cancelled;
    private Container container;
    private PreferencePanelRegistry registry;
    
    public PreferencesPanel(PreferenceTreeNode theRoot, PreferenceTreeNode theCurrent, PreferencePanelRegistry theRegistry, PBundle theMessages){
        this.messages = theMessages;
        this.registry = theRegistry;
        
        ok = new PJButton("Button.Ok", false, messages);
        ca = new PJButton("Button.Cancel", false, messages);
        
        JPanel butPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        butPanel.add(ok);
        butPanel.add(ca);
        
        displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 0, 2));
        
        label = new JLabel(" ");
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        label.setBackground(Color.WHITE);
        label.setOpaque(true);
        label.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(), BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        
        tree = new PreferenceTree(theRoot);
        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                if (tree.getSelectedNode() != null){
                    PreferenceTreeNode n = tree.getSelectedNode();
                    PreferencePanel pp = registry.get(n.getId());
                    if (pp != null){
                        label.setText(n.getLocalizedTextIncludingParent());
                        label.invalidate();
                        label.repaint();
                        label.validate();
                        
                        displayPanel.removeAll();
                        displayPanel.add(pp);
                        displayPanel.invalidate();
                        displayPanel.repaint();
                        displayPanel.validate();
                        
                        ArrayList <Component> cs = new ArrayList <Component> ();
                        if (tree.isEnabled()){
                            cs.add(tree);
                        }
                        if (pp.getOrderedComponentList() != null){
                        cs.addAll(pp.getOrderedComponentList());
                        }
                        cs.add(ok);
                        cs.add(ca);
                        
                        if (container != null){
                            container.setFocusTraversalPolicy(new SpecifiedOrderFocusTraversalPolicy(cs, cs.get(0)));
                        }
                    }
                    else{
                        label.setText("Error");
                        label.invalidate();
                        label.repaint();
                        label.validate();
                        
                        displayPanel.removeAll();
                        displayPanel.invalidate();
                        displayPanel.repaint();
                        displayPanel.validate();
                        
                        ArrayList <Component> cs = new ArrayList <Component> ();
                        if (tree.isEnabled()){
                            cs.add(tree);
                        }
                        cs.add(ok);
                        cs.add(ca);
                        
                        if (container != null){
                            container.setFocusTraversalPolicy(new SpecifiedOrderFocusTraversalPolicy(cs, cs.get(0)));
                        }
                    }
                }
                else{
                    tree.setSelectionPath(e.getOldLeadSelectionPath());
                }
            }
        });
        tree.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                tree.invalidate();
                tree.repaint();
            }

            public void focusLost(FocusEvent e) {
                tree.invalidate();
                tree.repaint();
            }
        });
        if (theCurrent != null){
            setView(theCurrent.getId());
        }
        else{
            setView(tree.getModel().getRootNode().getChildAt(0).getId());
        }
        
        JScrollPane jsp = new JScrollPane(tree);
        jsp.setPreferredSize(new Dimension(150, 0));
        
        PPanel treePanel = new PPanel(1, 1, 0, 0, new Object[]{
                "", "0,1",
                "0,1", jsp
        });
        
        JPanel seperatorPanel = new JPanel(new BorderLayout());
        seperatorPanel.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.SOUTH);
        
        PPanel top = new PPanel(3, 2, 4, 4, new Object[]{
                "",    "0",       "0,1",
                "0",   treePanel, label,
                "0",   "^",       displayPanel,
                "0,1", "^",       seperatorPanel
        }, 5, 0, 5, 5);
        
        PPanel main = new PPanel (2, 1, 0, 0, new Object[]{
                "", "0,1",
                "0,1", top,
                "0", butPanel
        });
        
        this.addActionListeners();
        this.setLayout(new BorderLayout());
        this.add(main);
    }

    public JDialog getDialog(Container theContainer, Preferences thePreferences){
        final JDialog jd;
        if (theContainer instanceof JFrame){
            jd = new JDialog((JFrame) theContainer, messages.getString("PreferencePanel.Title"));
        }
        else{
            jd = new JDialog((JDialog) theContainer, messages.getString("PreferencePanel.Title"));
        }
        jd.getContentPane().setLayout(new BorderLayout());
        jd.getContentPane().add(this);
        this.addComponentListener(new ComponentAdapter() {
            public void componentHidden(ComponentEvent e) {
                jd.setVisible(false);
            }
        });
        jd.setSize(new Dimension(580, 495));
        jd.setResizable(false);
        jd.setModal(true);
        PCentre.centreRelativeToParent(jd, jd.getParent());
        
        for (Iterator <Integer> i = registry.getIds().iterator(); i.hasNext();) {
            try {
                PreferencePanel pp = registry.get(i.next());
                pp.init();
                pp.set(thePreferences);
                pp.setContainer(theContainer);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.setContainer(jd);
        addActionListeners();
        return jd;
    }
    
    public void setContainer(Container theContainer){
        this.container = theContainer;
        tree.selectNodeWithPanelId(tree.getSelectedNode().getId());
    }
    
    /**
     * 
     */
    private void addActionListeners() {
        KeyAdapter okEnterKey = new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    ok.doClick();
                }
            }
        };
        
        KeyAdapter caEnterKey = new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    ca.doClick();
                }
            }
        };
        
        KeyAdapter escKey = new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == 0x1B) {
                    ca.doClick();
                }
            }
        };

        ok.addKeyListener(okEnterKey);
        ok.addKeyListener(escKey);
        ca.addKeyListener(caEnterKey);
        ca.addKeyListener(escKey);
        KeyListener[] kls = tree.getKeyListeners();
        for (int i = 0; i < kls.length; i++) {
             tree.removeKeyListener(kls[i]);
        }
        tree.addKeyListener(okEnterKey);
        tree.addKeyListener(escKey);
        
        for (Iterator <Integer> i = registry.getIds().iterator(); i.hasNext();) {
            registry.get(i.next()).setKeyListeners(okEnterKey, escKey);
        }
        
        ok.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (isVisible()){
                    if (finished != null) {
                        finished.actionPerformed(null);
                    }
                    setVisible(false);
                }
            }
        });

        ca.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (isVisible()){
                    if (cancelled != null) {
                        cancelled.actionPerformed(null);
                    }
                    setVisible(false);
                }
            }
        });
    }
    
    /**
     * This method is used to set the ActionListener which will be called upon clicking
     * ok on the JFrame
     * 
     * @param theActionListener An ActionListener
     */
    public void setFinishListener(ActionListener theActionListener) {
        finished = theActionListener;
    }

    /**
     * This method is used to set the ActionListener which will be called upon clicking
     * cancel on the JFrame
     * 
     * @param theActionListener An ActionListener
     */
    public void setCancelListener(ActionListener theActionListener) {
        cancelled = theActionListener;
    }
    
    public void setView(int thePanelId){
        tree.selectNodeWithPanelId(thePanelId);
    }
    
    public void setCanChangeView(boolean canChangeView) {
        tree.setEnabled(canChangeView);
        repaint();
    }
}