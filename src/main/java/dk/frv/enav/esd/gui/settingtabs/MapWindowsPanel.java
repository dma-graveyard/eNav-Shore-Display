package dk.frv.enav.esd.gui.settingtabs;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

import dk.frv.enav.esd.gui.MainFrame;
import dk.frv.enav.esd.settings.Settings;

public class MapWindowsPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MainFrame mainFrame;
	private Settings settings;
	private JLabel lblTheresCurrently;
	private JLabel lblTheCurrentWorkspace;
	
	public MapWindowsPanel(MainFrame mainFrame, Settings settings){
		super();
		
		this.mainFrame = mainFrame;
		this.settings = settings;
		
		
		setBackground(GuiStyler.backgroundColor);
		setBounds(10, 11, 493, 600);
		setLayout(null);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(GuiStyler.backgroundColor);
		panel_1.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1, new Color(70, 70, 70)), "Map Windows", TitledBorder.LEADING, TitledBorder.TOP, GuiStyler.defaultFont, GuiStyler.textColor));
		panel_1.setBounds(10, 11, 473, 283);
		
		add(panel_1);
		panel_1.setLayout(null);
		
		lblTheresCurrently = new JLabel("There's currently x active Map Windows");
		GuiStyler.styleText(lblTheresCurrently);
		lblTheresCurrently.setBounds(10, 33, 352, 14);
		panel_1.add(lblTheresCurrently);
		
		lblTheCurrentWorkspace = new JLabel("The current workspace is: name");
		GuiStyler.styleText(lblTheCurrentWorkspace);
		lblTheCurrentWorkspace.setBounds(10, 54, 317, 14);
		panel_1.add(lblTheCurrentWorkspace);
		
		JLabel lblClickOnThe = new JLabel("Click on the Map Tabs to change settings for the individual map");
		GuiStyler.styleText(lblClickOnThe);
		lblClickOnThe.setBounds(10, 133, 387, 87);
		panel_1.add(lblClickOnThe);
	}
	
	public void loadSettings(){
		lblTheresCurrently.setText("There's currently " +  mainFrame.getMapWindows().size() + " active Map Windows");
		lblTheCurrentWorkspace.setText(("The current workspace is " + settings.getGuiSettings().getWorkspace()));
	}
}
