package net.sourceforge.powerswing.handypanels;

import java.awt.BorderLayout;


import javax.swing.JPanel;

import net.sourceforge.powerswing.localization.PBundle;
import net.sourceforge.powerswing.panel.PPanel;

public class PAboutPanel extends JPanel {

	public PAboutPanel(PBundle theMessages){
	    this.setLayout(new BorderLayout());
		this.add(new PPanel(8, 1, 4, 3, new Object[] {
            "",  "0,1",
            "0", "~" + theMessages.getString("Product.Title"),
            "0", "",
            "0", "~" + theMessages.format("PAboutPanel.Version", new Object[]{theMessages.getString("Product.Version")}),
            "0", "",
            "0", "~" + theMessages.format("PAboutPanel.Copyright", new Object[]{theMessages.getString("Product.Copyright")}),
            "0", "",
            "0", "~" + theMessages.getString("PAboutPanel.Website"),
            "0", "", 
        }, 10, 0, 0, 0));
	}
}