package pipe.gui.widgets;

import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import pipe.dataLayer.TemCons;
import pipe.gui.CreateGui;


public class LPanel extends JPanel{
	private static ArrayList<TemCons> tclist = new ArrayList<TemCons>();
	JPanel panel = new JPanel();
	ArrayList <RPanel>  list = new ArrayList<RPanel>();
	public LPanel(){
		
	}
	public void setThings(ArrayList<TemCons> a){
		
		panel.setLayout(new GridLayout(0,2,10,20));
		for(int i = 0 ; i < a.size();i++){
			RPanel rpanel = new RPanel(2, i);
			panel.add(rpanel);
			list.add(rpanel);
			
		}
	}
	private class RPanel extends JPanel{
		JButton button = new JButton();
		JLabel label = new JLabel();
		JPanel panel = new JPanel();
		public RPanel(int path, int num){
			String file;
			if(path==0){
				file = "r.png";
			}
			else if(path == 1){
				file = "g.png";
			}
			else{
				file = "y.png";
			}
			Icon p =  new ImageIcon(Thread.currentThread().getContextClassLoader().
   	              getResource(CreateGui.imgPath + file));
			label = new JLabel(p);
			
			label.setOpaque(false);
			button.setText("TC"+num);
			
			panel.add(label);
			panel.add(button);
		}
	}
	
	
}

