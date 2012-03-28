package dk.frv.enav.esd.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class JMenuWorkspaceBar extends JMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JMenu maps;
	private HashMap<Integer, JMenu> mapMenus;

	// private MainFrame mainFrame;

	public JMenuWorkspaceBar(final MainFrame mainFrame) {
		super();

		// this.mainFrame = mainFrame;
		// JMenuBar mb = new JMenuBar();
		// this.setJMenuBar(mb);
		mapMenus = new HashMap<Integer, JMenu>();

		JMenu fm = new JMenu("File");
		this.add(fm);

		JMenuItem toggleFullScreen = new JMenuItem("Toggle Fullscreen");
		fm.add(toggleFullScreen);

		JMenuItem mi = new JMenuItem("Exit");
		fm.add(mi);

		maps = new JMenu("Maps");
		this.add(maps);

		JMenuItem addMap = new JMenuItem("New Map Window");
		maps.add(addMap);

//		JMenuItem lockMaps = new JMenuItem("Lock/Unlock all map windows");
//		maps.add(lockMaps);

		maps.addSeparator();

		toggleFullScreen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.toggleFullScreen();
			}
		});

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

//		lockMaps.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				List<JMapFrame> mapWindows = mainFrame.getMapWindows();
//				for (int i = 0; i < mapWindows.size(); i++) {
//					mapWindows.get(i).lockUnlockWindow();
//				}
//			}
//		});

	}

	public void addMap(final JMapFrame window) {
		JMenu mapWindow = new JMenu(window.getTitle());

		JCheckBoxMenuItem toggleLock = new JCheckBoxMenuItem("Lock/Unlock");
		mapWindow.add(toggleLock);
		
		JMenuItem windowSettings = new JMenuItem("Settings");
		mapWindow.add(windowSettings);
		
		JMenuItem front = new JMenuItem("Bring to front");
		mapWindow.add(front);		
		
		mapMenus.put(window.getId(), mapWindow);

		maps.add(mapWindow);

		toggleLock.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				window.lockUnlockWindow();
			}
		});

	}

	public void removeMapMenu(final JMapFrame window) {
		JMenu menuItem = mapMenus.get(window.getId());
		maps.remove(menuItem);
	}

	public void renameMapMenu(final JMapFrame window) {
		JMenu menuItem = mapMenus.get(window.getId());

		int menuPosition = 0;
		for (int i = 0; i < maps.getItemCount(); i++) {

			if (maps.getItem(i) == menuItem) {
				menuPosition = i;
			}

		}

		maps.remove(menuItem);
		menuItem.setText(window.getTitle());
		maps.insert(menuItem, menuPosition);
	}

}
