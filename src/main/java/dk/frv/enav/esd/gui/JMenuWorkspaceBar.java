/*
 * Copyright 2012 Danish Maritime Authority. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 *   2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Safety Administration ``AS IS'' 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of Danish Maritime Authority.
 * 
 */
package dk.frv.enav.esd.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import dk.frv.enav.esd.ESD;
import dk.frv.enav.esd.gui.fileselection.WorkspaceFileFilter;

/**
 * Toolbar used in the mainframe
 * @author David A. Camre (davidcamre@gmail.com
 *
 */
public class JMenuWorkspaceBar extends JMenuBar {

	private static final long serialVersionUID = 1L;
	private JMenu maps;
	private HashMap<Integer, JMenu> mapMenus;
	private MainFrame mainFrame;
	private JMainDesktopPane desktop;
	// private MainFrame mainFrame;

	/**
	 * Constructor
	 * @param mainFrame
	 */
	public JMenuWorkspaceBar(final MainFrame mainFrame) {
		super();

		this.mainFrame = mainFrame;
		this.desktop = mainFrame.getDesktop();
		
		// this.mainFrame = mainFrame;
		// JMenuBar mb = new JMenuBar();
		// this.setJMenuBar(mb);
		mapMenus = new HashMap<Integer, JMenu>();

		//File menu
		
		JMenu fm = new JMenu("File");
		this.add(fm);

		JMenuItem toggleFullScreen = new JMenuItem("Toggle Fullscreen");
		fm.add(toggleFullScreen);

		JMenuItem preferences = new JMenuItem("Preferences");
		fm.add(preferences);
		
		JMenuItem mi = new JMenuItem("Exit");
		fm.add(mi);
		

		//Maps menu
		
		maps = new JMenu("Maps");
		this.add(maps);

		
		
		JMenuItem addMap = new JMenuItem("New Map Window");
		maps.add(addMap);
		
		JMenuItem cascade = new JMenuItem("Sort by Cascade");
		maps.add(cascade);
		
		JMenuItem tile = new JMenuItem("Sort by Tile");
		maps.add(tile);		
		maps.addSeparator();
		
		//Workspace
		
		JMenu workspace = new JMenu("Workspace");
		this.add(workspace);
		
		JMenuItem lockAll = new JMenuItem("Lock all windows");
		workspace.add(lockAll);
		
		JMenuItem unlockAll = new JMenuItem("Unlock all windows");
		workspace.add(unlockAll);
		

		//Notifications
		
		JMenu notifications = new JMenu("Notifications");
		this.add(notifications);
		
		JMenuItem notCenter = new JMenuItem("Notification Center");
		notifications.add(notCenter);

		workspace.addSeparator();
		
		JMenuItem loadWorkspace = new JMenuItem("Load workspace");
		workspace.add(loadWorkspace);

		JMenuItem saveWorkspace = new JMenuItem("Save workspace");
		workspace.add(saveWorkspace);
				
		//Action listeners
		
		loadWorkspace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					selectWorkspace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		
		saveWorkspace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					saveWorkspace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		//Action listeners
		
		toggleFullScreen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.toggleFullScreen();
			}
		});
		
		notCenter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.toggleNotificationCenter();
			}
		});

		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ESD.closeApp();
			}
		});
		
		preferences.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.getSettingsWindow().toggleVisibility();
			}
		});


		addMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.addMapWindow();
			}
		});
		
	    cascade.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent ae) {
	          desktop.cascadeFrames();
	        }
	      });
	    tile.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent ae) {
	        desktop.tileFrames();
	        }
	      });
	    
	    
	    lockAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//if(!mainFrame.isToolbarsLocked()){
				//	mainFrame.toggleBarsLock();
				//}
				
				lockAll();
			}
		});
		
	    unlockAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				unLockAll();
				//if(mainFrame.isToolbarsLocked()){
				//	mainFrame.toggleBarsLock();
				//}
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
	
	/**
	 * Added mapWindow to the toolbar
	 * @param window to be added
	 * @param locked locked setting
	 * @param alwaysInFront setting
	 */
	public void addMap(final JMapFrame window, boolean locked, boolean alwaysInFront) {
		JMenu mapWindow = new JMenu(window.getTitle());

		JCheckBoxMenuItem toggleLock = new JCheckBoxMenuItem("Lock/Unlock");
		mapWindow.add(toggleLock);
		
		JMenuItem windowSettings = new JMenuItem("Settings");
		mapWindow.add(windowSettings);
		windowSettings.setEnabled(false);
		
		JCheckBoxMenuItem alwaysFront = new JCheckBoxMenuItem("Always on top");
		mapWindow.add(alwaysFront);	
		
		JMenuItem front = new JMenuItem("Bring to front");
		mapWindow.add(front);		
		
		JMenuItem rename = new JMenuItem("Rename");
		mapWindow.add(rename);		
		
		mapMenus.put(window.getId(), mapWindow);

		maps.add(mapWindow);
		
		alwaysFront.setSelected(alwaysInFront);
		
		toggleLock.setSelected(locked);

		toggleLock.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				window.lockUnlockWindow();
			}
		});
		
		rename.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				window.rename();
			}
		});
		
		front.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				window.toFront();
			}
		});
		
		alwaysFront.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				window.alwaysFront();
			}
		});		

	}
	
	/**
	 * Set all windows to have a locked status
	 */
	public void lockAll(){
		List<JMapFrame> mapWindows = mainFrame.getMapWindows();
		for (int i = 0; i < mapWindows.size(); i++) {
			
			if (!mapWindows.get(i).isLocked()){
				mapWindows.get(i).lockUnlockWindow();	
			}
		}
		
		
	    Iterator<Entry<Integer, JMenu>> it = mapMenus.entrySet().iterator();
	    while (it.hasNext()) {
	    	((JCheckBoxMenuItem) it.next().getValue().getItem(0)).setSelected(true);
	    }
	}

	/**
	 * Remove a map from the toolbar
	 * @param window
	 */
	public void removeMapMenu(final JMapFrame window) {
		JMenu menuItem = mapMenus.get(window.getId());
		maps.remove(menuItem);
	}
	
	public void lockMapMenu(final JMapFrame window, boolean locked) {
		JMenu menuItem = mapMenus.get(window.getId());

		menuItem.getItem(0).setSelected(locked);
	}
	
	public void onTopMapMenu(final JMapFrame window, boolean locked) {
		JMenu menuItem = mapMenus.get(window.getId());

		menuItem.getItem(2).setSelected(locked);
	}


	/**
	 * Rename a mapwindow in the toolbar
	 * @param window
	 */
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

	/**
	 * Function used to save a workspace
	 * @throws IOException
	 */
	public void saveWorkspace() throws IOException{
		final JFileChooser fc = new JFileChooser(System.getProperty("user.dir") + "\\workspaces");
        fc.setFileFilter(new WorkspaceFileFilter());	
        
        int returnVal = fc.showSaveDialog(mainFrame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String filename = file.getName();
            if(!filename.endsWith(".workspace")){
//            	System.out.println("Appending .workspace");
            	filename = filename + ".workspace";
            }
            mainFrame.saveWorkSpace(filename);
        }
	}

	
	/**
	 * Load a workspace from a file
	 * @throws IOException
	 */
	public void selectWorkspace() throws IOException{
		final JFileChooser fc = new JFileChooser(System.getProperty("user.dir") + "\\workspaces");
        fc.setFileFilter(new WorkspaceFileFilter());	
        
        int returnVal = fc.showOpenDialog(mainFrame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            mainFrame.loadNewWorkspace(file.getParent(), file.getName());
        }
	}
	
	/**
	 * unlock all windows
	 */
	public void unLockAll(){
		List<JMapFrame> mapWindows = mainFrame.getMapWindows();
		for (int i = 0; i < mapWindows.size(); i++) {
			
			if (mapWindows.get(i).isLocked()){
				mapWindows.get(i).lockUnlockWindow();	
			}
		}
		
	    Iterator<Entry<Integer, JMenu>> it = mapMenus.entrySet().iterator();
	    while (it.hasNext()) {
//	    	JMenu menu = it.next().getValue();
//	    	menu.getItem(0);
	    	((JCheckBoxMenuItem) it.next().getValue().getItem(0)).setSelected(false);
//	    	locked.setSelected(true);
//	        Map.Entry pairs = (Map.Entry)it.next();
//	        pairs
//	        
//	        System.out.println(pairs.getKey() + " = " + pairs.getValue());
//	        it.remove(); // avoids a ConcurrentModificationException
	    }
		

	}
}
