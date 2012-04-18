package dk.frv.enav.esd.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StatusArea extends JInternalFrame {
	
	private static final long serialVersionUID = 1L;	
	private Boolean locked = false;
	private JLabel moveHandler;
	private JPanel masterPanel;
	private JPanel statusPanel;
	private static int moveHandlerHeight = 12;
	private static int statusItemHeight = 15;
	private static int statusItemWidth = 70;
	private HashMap<String, String> statusItems = new HashMap<String, String>();
	public int width;
	public int height;

	public StatusArea(MainFrame mainFrame) {
		
		// Setup location
		this.setLocation((10+moveHandlerHeight), (80 + mainFrame.getToolbar().getHeight() + mainFrame.getNotificationArea().getHeight()));
		this.setVisible(true);
		this.setResizable(false);
		
		// Strip off window looks
		setRootPaneCheckingEnabled(false);
		((javax.swing.plaf.basic.BasicInternalFrameUI)this.getUI()).setNorthPane(null);
		this.setBorder(null);
		
        // Create the top movehandler (for dragging)
        moveHandler = new JLabel("Status", JLabel.CENTER);
        moveHandler.setForeground(Color.WHITE);
        moveHandler.setOpaque(true);
        moveHandler.setBackground(Color.DARK_GRAY);
        moveHandler.setPreferredSize(new Dimension(statusItemWidth, moveHandlerHeight));
        MoveMouseListener mml = new MoveMouseListener(this, mainFrame);
        moveHandler.addMouseListener(mml);
        moveHandler.addMouseMotionListener(mml);
		
		// Create the grid for the status items
        statusPanel = new JPanel();
        statusPanel.setLayout(new GridLayout(0,1));
        statusPanel.setBorder(BorderFactory.createLineBorder (Color.DARK_GRAY, 2));
		
		
		// Add status items here
		// Status: X coordinate
		statusItems.put("X", "X: 342.32");
		
		// Status: Y coordinate
		statusItems.put("Y", "Y: 34.234");
		
		// Status: Z coordinate
		statusItems.put("Z", "Z: 3.122");
				

	    // Create the masterpanel for aligning
	    masterPanel = new JPanel(new BorderLayout());
	    masterPanel.add(moveHandler, BorderLayout.NORTH);
	    masterPanel.add(statusPanel, BorderLayout.SOUTH);
	    this.getContentPane().add(masterPanel);
	 
	    // And finally refresh the status bar
	    repaintToolbar();
	}
	
	/*
	 * Function for locking/unlocking the status bar
	 * Author: Steffen D. Sommer
	 */
	public void toggleLock() {
		if(locked) {
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
	
	/*
	 * Function for refreshing the status bar after editing status items, size etc.
	 * Author: Steffen D. Sommer
	 */
	public void repaintToolbar() {
		
		// Lets start by adding all the notifications
		for(Iterator<Entry<String, String>> i = statusItems.entrySet().iterator();i.hasNext();) {
			statusPanel.add(new JLabel(i.next().getValue()));
		}
		
		// Then calculate the size of the status bar according to the number of status items
		width = statusItemWidth;
		int innerHeight = statusItems.size() * statusItemHeight;
		height = innerHeight;
		
		if(!locked)
			height = innerHeight + moveHandlerHeight;
		
		// And finally set the size and repaint it
		statusPanel.setSize(width, innerHeight);
		statusPanel.setPreferredSize(new Dimension(width, innerHeight));
		this.setSize(width, height);
		this.revalidate();
		this.repaint();
		
	}
	
	/*
	 * Function for getting the width of the status bar
	 * @return width Width of the status bar
	 */
	public int getWidth() {
		return width;
	}
	
	/*
	 * Function for getting the height of the status bar
	 * @return height Height of the status bar
	 */
	public int getHeight() {
		return height;
	}
	
	// TODO: Add methods for updating the hashmap containing status values
}
