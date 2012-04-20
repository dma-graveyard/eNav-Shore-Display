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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.beans.PropertyVetoException;
import java.beans.beancontext.BeanContextServicesSupport;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import dk.frv.enav.esd.ESD;
import dk.frv.enav.esd.settings.GuiSettings;
import dk.frv.enav.esd.settings.Workspace;

/**
 * The main frame containing map and panels
 */
public class MainFrame extends JFrame implements WindowListener {

	private static final String TITLE = "eNav Shore Display System ";

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(MainFrame.class);
	private int windowCount = 0;
	private Dimension size = new Dimension(1000, 700);
	private Point location;
	private JMenuWorkspaceBar topMenu;
	private boolean fullscreen = false;
	private int mouseMode = 1;
	private BeanContextServicesSupport beanHandler;
	
	
	private List<JMapFrame> mapWindows;
	private JMainDesktopPane desktop;
	private JScrollPane scrollPane;
	
	private boolean toolbarsLocked = false;
	private ToolBar toolbar = new ToolBar(this);
	private NotificationArea notificationArea = new NotificationArea(this);
	private NotificationCenter notificationCenter  = new NotificationCenter();
	private StatusArea statusArea = new StatusArea(this);


	public MainFrame() {
		super();
		initGUI();

	}

	public int getMouseMode() {
		return mouseMode;
	}

	public void setMouseMode(int mouseMode) {
		this.mouseMode = mouseMode;
	}

	private void initGUI() {

		beanHandler = ESD.getBeanHandler();
		// Get settings
		GuiSettings guiSettings = ESD.getSettings().getGuiSettings();

		Workspace workspace = ESD.getSettings().getWorkspace();

		setTitle(TITLE);

		// Set location and size
		if (guiSettings.isMaximized()) {
			setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
		} else {
			setLocation(guiSettings.getAppLocation());
		}
		if (guiSettings.isFullscreen()) {
			toggleFullScreen();
		} else {
			setSize(guiSettings.getAppDimensions());
		}

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setIconImage(getAppIcon());
		addWindowListener(this);

		desktop = new JMainDesktopPane(this);
		scrollPane = new JScrollPane();

		// pack();

		// desktop.setSize(1000, 700);
		// scrollPane.setSize(1000, 700);

		scrollPane.getViewport().add(desktop);
		// getContentPane().add(scrollPane);
		this.setContentPane(scrollPane);

		desktop.setBackground(Color.LIGHT_GRAY);

		mapWindows = new ArrayList<JMapFrame>();

		topMenu = new JMenuWorkspaceBar(this);
		this.setJMenuBar(topMenu);

		//Initiate the permanent window elements
		desktop.getManager().setStatusArea(statusArea);

		desktop.getManager().setNotificationArea(notificationArea);
		
		desktop.getManager().setToolbar(toolbar);

		desktop.getManager().setNotCenter(notificationCenter);
		
		desktop.add(statusArea, true);
		desktop.add(notificationCenter, true);
		desktop.add(toolbar, true);
		desktop.add(notificationArea, true);
		


		
		
		// dtp.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);

		// Add self to bean handler
		beanHandler.add(this);

		setWorkSpace(workspace);

	}

	public void toggleNotificationCenter() {
		notificationCenter.toggleVisibility();
	}
	
	public boolean isToolbarsLocked() {
		return toolbarsLocked;
	}


	public ToolBar getToolbar() {
		return toolbar;
	}
	
	public StatusArea getStatusArea() {
		return statusArea;
	}

	public NotificationArea getNotificationArea() {
		return notificationArea;
	}

	public void toggleBarsLock() {
		
		if (toolbarsLocked){
			toolbarsLocked = false;
		}else{
			toolbarsLocked = true;
		}
		
		toolbar.toggleLock();
		notificationArea.toggleLock();
		statusArea.toggleLock();
	}

	private static Image getAppIcon() {
		java.net.URL imgURL = ESD.class.getResource("/images/appicon.png");
		if (imgURL != null) {
			return new ImageIcon(imgURL).getImage();
		}
		LOG.error("Could not find app icon");
		return null;
	}

	public List<JMapFrame> getMapWindows() {
		return mapWindows;
	}

	public JMapFrame addMapWindow(boolean workspace, boolean locked, boolean alwaysInFront, Point2D center, float scale) {
		windowCount++;
		JMapFrame window = new JMapFrame(windowCount, this, center, scale);
		desktop.add(window, workspace);
		mapWindows.add(window);
		window.toFront();
		topMenu.addMap(window, locked, alwaysInFront);

		return window;
	}

	public JMapFrame addMapWindow() {
		windowCount++;
		JMapFrame window = new JMapFrame(windowCount, this);

		desktop.add(window);

		mapWindows.add(window);
		// window.toFront();

		topMenu.addMap(window, false, false);

		return window;
	}

	public void loadNewWorkspace(String parent, String filename) {
		Workspace workspace = ESD.getSettings().loadWorkspace(parent, filename);
		setWorkSpace(workspace);
	}

	public void setWorkSpace(Workspace workspace) {

		while (mapWindows.size() != 0) {
			try {
				mapWindows.get(0).setClosed(true);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}

		// Reset the workspace
		windowCount = 0;
		mapWindows = new ArrayList<JMapFrame>();

		if (workspace.isValidWorkspace()) {
			for (int i = 0; i < workspace.getName().size(); i++) {
				JMapFrame window = addMapWindow(true, workspace.isLocked().get(i), workspace.getAlwaysInFront().get(i),
						workspace.getCenter().get(i), workspace.getScale().get(i));
				window.setTitle(workspace.getName().get(i));
				topMenu.renameMapMenu(window);
				window.setSize(workspace.getSize().get(i));
				window.setLocation(workspace.getPosition().get(i));

				try {
					window.setMaximum(workspace.isMaximized().get(i));
				} catch (PropertyVetoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (workspace.isLocked().get(i)) {
					window.lockUnlockWindow();
				}

				if (workspace.getAlwaysInFront().get(i)) {
					window.alwaysFront();
				}

				// window.getChartPanel().getMap().setScale(0.001f);
				// window.getChartPanel().getMap().setCenter(workspace.getCenter().get(i));

			}

		}
	}

	public void removeMapWindow(JMapFrame window) {
		topMenu.removeMapMenu(window);
		mapWindows.remove(window);
	}

	public void renameMapWindow(JMapFrame window) {
		topMenu.renameMapMenu(window);
	}

	public JMainDesktopPane getDesktop() {
		return desktop;
	}

	public Dimension getMaxResolution() {
		int width = 0;
		int height = 0;

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();

		for (GraphicsDevice curGs : gs) {
			DisplayMode mode = curGs.getDisplayMode();
			width += mode.getWidth();

			// System.out.println("Width: " + width);

			if (height < mode.getHeight()) {
				height = mode.getHeight();
			}

		}
		return new Dimension(width, height);

	}

	public void toggleFullScreen() {

		// System.out.println(this.getLocationOnScreen());
		// System.out.println("fullscreen toggle");

		if (!fullscreen) {
			location = this.getLocation();
			System.out.println("Size is: " + size);
			
			
			
			this.setSize(getMaxResolution());
			// setLocationRelativeTo(null);
			this.setLocation(0, 0);
			// setExtendedState(JFrame.MAXIMIZED_BOTH);
			dispose();
			this.setUndecorated(true);
			setVisible(true);
			fullscreen = true;
		} else {
			// setExtendedState(JFrame.NORMAL);
			fullscreen = false;
			if (size.getHeight() != 0 && size.getWidth() != 0){
				size = Toolkit.getDefaultToolkit().getScreenSize();
//				size = new Dimension(1000, 700);	
			}
			this.setSize(size);
			this.setLocation(location);
			dispose();
			this.setUndecorated(false);
			setVisible(true);
		}
	}

	public void saveSettings() {
		// Save gui settings
		GuiSettings guiSettings = ESD.getSettings().getGuiSettings();
		guiSettings.setFullscreen(fullscreen);
		guiSettings.setMaximized((getExtendedState() & MAXIMIZED_BOTH) > 0);
		guiSettings.setAppLocation(getLocation());
		guiSettings.setAppDimensions(getSize());

		// Save map settings
		// chartPanel.saveSettings();

	}

	public void saveWorkSpace(String filename) {
		ESD.getSettings().saveCurrentWorkspace(mapWindows, filename);
	}

	@Override
	public void windowActivated(WindowEvent we) {
	}

	@Override
	public void windowClosed(WindowEvent we) {
	}

	@Override
	public void windowClosing(WindowEvent we) {

		// Close routine
		ESD.closeApp();
	}

	@Override
	public void windowDeactivated(WindowEvent we) {
	}

	@Override
	public void windowDeiconified(WindowEvent we) {
	}

	@Override
	public void windowIconified(WindowEvent we) {
	}

	@Override
	public void windowOpened(WindowEvent we) {
	}

}
