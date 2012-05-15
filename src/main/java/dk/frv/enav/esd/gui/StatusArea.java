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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.beancontext.BeanContext;
import java.beans.beancontext.BeanContextChild;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.frv.enav.esd.event.IMapCoordListener;
import dk.frv.enav.esd.event.ToolbarMoveMouseListener;
import dk.frv.enav.ins.common.text.Formatter;

/**
 * Class for setting up the status area of the application
 * 
 * @author Steffen D. Sommer (steffendsommer@gmail.com)
 */
public class StatusArea extends JInternalFrame implements IMapCoordListener, BeanContextChild {

	private static final long serialVersionUID = 1L;
	private Boolean locked = false;
	private JLabel moveHandler;
	private JPanel masterPanel;
	private JPanel statusPanel;
	private JPanel highlightPanel;
	private static int moveHandlerHeight = 18;
	private static int statusItemHeight = 20;
	private static int statusItemWidth = 125;
	private static int statusPanelOffset = 4;
	private HashMap<String, JLabel> statusItems = new HashMap<String, JLabel>();
	private HashMap<String, JLabel> highlightItems = new HashMap<String, JLabel>();
	public int width;
	public int height;

	/**
	 * Constructor for setting up the status area
	 * 
	 * @param mainFrame
	 *            reference to the mainframe
	 */
	public StatusArea(MainFrame mainFrame) {

		// Setup location
		this.setLocation((10 + moveHandlerHeight), (80 + mainFrame.getToolbar().getHeight() + mainFrame
				.getNotificationArea().getHeight()));
		this.setVisible(true);
		this.setResizable(false);

		// Strip off window looks
		setRootPaneCheckingEnabled(false);
		((javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI()).setNorthPane(null);
		this.setBorder(null);

		// Create the top movehandler (for dragging)
		moveHandler = new JLabel("Status", JLabel.CENTER);
		moveHandler.setForeground(new Color(200, 200, 200));
		moveHandler.setOpaque(true);
		moveHandler.setBackground(Color.DARK_GRAY);
		moveHandler.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(30, 30, 30)));
		moveHandler.setFont(new Font("Arial", Font.BOLD, 9));
		moveHandler.setPreferredSize(new Dimension(statusItemWidth, moveHandlerHeight));
		ToolbarMoveMouseListener mml = new ToolbarMoveMouseListener(this, mainFrame);
		moveHandler.addMouseListener(mml);
		moveHandler.addMouseMotionListener(mml);

		// Create the grid for the status items
		statusPanel = new JPanel();
		statusPanel.setLayout(new GridLayout(0, 1));
		statusPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 0));
		statusPanel.setBackground(new Color(83, 83, 83));

		// Add status items here
		// Status: X coordinate
		statusItems.put("LAT", new JLabel(" LAT: N/A"));

		// Status: Y coordinate
		statusItems.put("LON", new JLabel(" LON: N/A"));

		// Create the grid for the highlighted ship info area
		highlightPanel = new JPanel();
		highlightPanel.setLayout(new GridLayout(0, 1));
		highlightPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 0));
		highlightPanel.setBackground(new Color(83, 83, 83));

		// Create the masterpanel for aligning
		masterPanel = new JPanel(new BorderLayout());
		masterPanel.add(moveHandler, BorderLayout.NORTH);
		masterPanel.add(statusPanel, BorderLayout.CENTER);
		masterPanel.add(highlightPanel, BorderLayout.SOUTH);
		masterPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, new Color(30, 30, 30), new Color(
				45, 45, 45)));
		this.getContentPane().add(masterPanel);

		// And finally refresh the status bar
		repaintToolbar();
	}

	/**
	 * Function for locking/unlocking the status bar
	 */
	public void toggleLock() {

		if (locked) {

			masterPanel.add(moveHandler, BorderLayout.NORTH);
			locked = false;
			repaintToolbar();

			// Align the status bar according to the height of the movehandler
			int newX = (int) (this.getLocation().getX());
			int newY = (int) (this.getLocation().getY());
			Point new_location = new Point(newX, (newY - moveHandlerHeight));
			this.setLocation(new_location);

		} else {

			masterPanel.remove(moveHandler);
			locked = true;
			repaintToolbar();

			// Align the status bar according to the height of the movehandler
			int newX = (int) (this.getLocation().getX());
			int newY = (int) (this.getLocation().getY());
			Point new_location = new Point(newX, (newY + moveHandlerHeight));
			this.setLocation(new_location);

		}
	}

	/**
	 * Function for refreshing the status area after editing status items
	 */
	public void repaintToolbar() {

		// Lets start by adding all the notifications
		for (Iterator<Entry<String, JLabel>> i = statusItems.entrySet().iterator(); i.hasNext();) {
			JLabel statusItem = i.next().getValue();
			statusItem.setFont(new Font("Arial", Font.PLAIN, 11));
			statusItem.setForeground(new Color(237, 237, 237));
			statusPanel.add(statusItem);
		}

		// Then add all the highlighted vessel info
		highlightPanel.removeAll();
		JLabel highlightTitle = new JLabel(" Highlighted Vessel");
		highlightTitle.setFont(new Font("Arial", Font.PLAIN, 11));
		highlightTitle.setForeground(new Color(30, 30, 30));
		if(highlightItems.size()>0)
			highlightPanel.add(highlightTitle);
		for (Iterator<Entry<String, JLabel>> i = highlightItems.entrySet().iterator(); i.hasNext();) {
			JLabel highlightItem = i.next().getValue();
			highlightItem.setFont(new Font("Arial", Font.PLAIN, 11));
			highlightItem.setForeground(new Color(237, 237, 237));
			highlightPanel.add(highlightItem);
		}

		// Then calculate the size of the status bar according to the number of
		// status items
		width = statusItemWidth;
		int innerHeight = statusItems.size() * statusItemHeight;
		// Expanding width highlight size.
		int innerHeight2 = highlightItems.size() * statusItemHeight;
		if(highlightItems.size()>0)
			innerHeight2 += statusItemHeight;
		
		height = innerHeight+innerHeight2;

		if (!locked)
			height = innerHeight + innerHeight2 + moveHandlerHeight;

		// And finally set the size and repaint it
		statusPanel.setSize(width, innerHeight - statusPanelOffset);
		statusPanel.setPreferredSize(new Dimension(width, innerHeight - statusPanelOffset));
		// Also for highlight panel
		highlightPanel.setSize(width, innerHeight2 - statusPanelOffset);
		highlightPanel.setPreferredSize(new Dimension(width, innerHeight2 - statusPanelOffset));
		this.setSize(width, height);
		this.revalidate();
		this.repaint();

	}

	/**
	 * Function for getting the width of the status bar
	 * 
	 * @return width width of the status bar
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Function for getting the height of the status bar
	 * 
	 * @return height height of the status bar
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Overriding function for setting the behavior when a coordinate is
	 * received
	 * 
	 * @param llp
	 *            point including lat and lon
	 */
	@Override
	public void receiveCoord(LatLonPoint llp) {

		statusItems.get("LAT").setText(" LAT  " + Formatter.latToPrintable(llp.getLatitude()));
		statusItems.get("LON").setText(" LON " + Formatter.lonToPrintable(llp.getLongitude()));

	}

	@Override
	public void addVetoableChangeListener(String arg0, VetoableChangeListener arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public BeanContext getBeanContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeVetoableChangeListener(String arg0, VetoableChangeListener arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setBeanContext(BeanContext arg0) throws PropertyVetoException {
		// TODO Auto-generated method stub
	}
	
	/**
	 * Update status area with highlighted vessel info
	 * @param MMSI The MMSI of the vessel
	 * @param name The name (if set) of the vessel, else N/A.
	 */
	public void receiveHighlight(long MMSI, String name) {
		if(highlightItems.containsKey("MMSI")){
			highlightItems.get("MMSI").setText(" MMSI  " + MMSI);
		} else {
			highlightItems.put("MMSI", new JLabel(" MMSI  " + MMSI));
		}
		
		if(highlightItems.containsKey("Name")){
			highlightItems.get("Name").setText(" Name  " + name);
		} else {
			highlightItems.put("Name", new JLabel(" Name  " + name));
		}
		repaintToolbar();
		System.out.println("Size of toolbar after addedH: "+highlightItems.size());
	}
	
	public void removeHighlight(){
		highlightItems.clear();
		repaintToolbar();
		System.out.println("Size of toolbar after removedH: "+highlightItems.size());
	}

}
