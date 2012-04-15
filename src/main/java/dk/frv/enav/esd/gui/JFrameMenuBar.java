package dk.frv.enav.esd.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import dk.frv.enav.esd.ESD;

public class JFrameMenuBar extends JInternalFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// private final MainFrame mainFrame;

	public JFrameMenuBar(final MainFrame mainFrame) {
		super();
		// this.mainFrame = mainFrame;

		// DO NOT USE THIS - USING JMenuworkspaceBar for now!

		setRootPaneCheckingEnabled(false);
		javax.swing.plaf.InternalFrameUI ifu = this.getUI();
		((javax.swing.plaf.basic.BasicInternalFrameUI) ifu).setNorthPane(null);

		this.setLocation(0, 0);
		this.setSize(100, 17);
		this.setVisible(true);
		this.setBorder(null);

		JMenuBar mb = new JMenuBar();
		this.setJMenuBar(mb);
		JMenu fm = new JMenu("File");
		JMenu maps = new JMenu("Maps");
		mb.add(fm);
		mb.add(maps);

		JMenuItem mi = new JMenuItem("Exit");
		fm.add(mi);

		JMenuItem addMap = new JMenuItem("New Map Window");
		maps.add(addMap);

		JMenuItem lockMaps = new JMenuItem("Lock/Unlock all map windows");
		maps.add(lockMaps);

		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// System.exit(0);
//				ESD.closeApp();
				System.out.println("Exit");
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
