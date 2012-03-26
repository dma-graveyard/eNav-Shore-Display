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
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;

import org.apache.log4j.Logger;

import com.bbn.openmap.MapHandler;

import dk.frv.enav.esd.ESD;
import dk.frv.enav.esd.settings.GuiSettings;

/**
 * The main frame containing map and panels
 */
public class MainFrame extends JFrame implements WindowListener {

	private static final String TITLE = "eNav Shore Display System ";

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(MainFrame.class);
	private int windowCount = 0;

	List<JMapFrame> mapWindows;

	public MainFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		MapHandler mapHandler = ESD.getMapHandler();
		// Get settings
		GuiSettings guiSettings = ESD.getSettings().getGuiSettings();
		setTitle(TITLE);

		// Set location and size
		if (guiSettings.isMaximized()) {
			setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
		} else {
			setLocation(guiSettings.getAppLocation());
			setSize(guiSettings.getAppDimensions());
		}
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setIconImage(getAppIcon());
		addWindowListener(this);

		JDesktopPane dtp = new JDesktopPane();
		this.setContentPane(dtp);
		dtp.setBackground(Color.LIGHT_GRAY);

		mapWindows = new ArrayList<JMapFrame>();

		JFrameMenuBar floatingMenu = new JFrameMenuBar(this);
		dtp.add(floatingMenu);

		// Add self to map map handler
		mapHandler.add(this);
		
		// HARDCODED: Initialize with 1 map window
		addMapWindow();

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

	public void addMapWindow(){
		System.out.println("Called");
		windowCount++;
		JMapFrame window = new JMapFrame(windowCount);
		this.add(window);
		mapWindows.add(window);
	}

	public void saveSettings() {
		// Save gui settings
		GuiSettings guiSettings = ESD.getSettings().getGuiSettings();
		guiSettings.setMaximized((getExtendedState() & MAXIMIZED_BOTH) > 0);
		guiSettings.setAppLocation(getLocation());
		guiSettings.setAppDimensions(getSize());
		// Save map settings
//		chartPanel.saveSettings();
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
