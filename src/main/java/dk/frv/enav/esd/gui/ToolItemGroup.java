package dk.frv.enav.esd.gui;

import java.util.ArrayList;
import javax.swing.JLabel;

public class ToolItemGroup {
	
	private static final long serialVersionUID = 2L;
	
	private ArrayList<JLabel> toolItems = new ArrayList<JLabel>();
	private boolean singleEnable = false;
	
	
	
	public void addToolItem(JLabel toolItem) {
		this.toolItems.add(toolItem);
	}
	
	public void setToolItems(ArrayList<JLabel> toolItems) {
		this.toolItems = toolItems;
	}

	public ArrayList<JLabel> getToolItems() {
		return toolItems;
	}

	public void setSingleEnable(boolean singleEnable) {
		this.singleEnable = singleEnable;
	}

	public boolean isSingleEnable() {
		return singleEnable;
	}

}
