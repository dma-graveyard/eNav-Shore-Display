package dk.frv.enav.esd.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class JMenuWorkspaceBar extends JMenuBar{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JMenuWorkspaceBar(final MainFrame mainFrame){
		super();

//		JMenuBar mb = new JMenuBar();
//		this.setJMenuBar(mb);
		JMenu fm = new JMenu("File");
		JMenu maps = new JMenu("Maps");
		this.add(fm);
		this.add(maps);
		
		JMenuItem mi = new JMenuItem("Exit");
		fm.add(mi);

		JMenuItem addMap = new JMenuItem("New Map Window");
		maps.add(addMap);
		
		JMenuItem lockMaps = new JMenuItem("Lock/Unlock all map windows");
		maps.add(lockMaps);
		
		
		mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		System.exit(0);
		}
		});
		
		addMap.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			mainFrame.addMapWindow();
		}
		});
		
		lockMaps.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			List<JMapFrame> mapWindows = mainFrame.getMapWindows();
			for (int i = 0; i < mapWindows.size(); i++) {
				mapWindows.get(i).lockUnlockWindow();
			}
		}
		});
		
	}
	

	


	
}
