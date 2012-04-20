package dk.frv.enav.esd.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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

import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.frv.enav.esd.event.IMapCoordListener;
import dk.frv.enav.ins.common.text.Formatter;

public class StatusArea extends JInternalFrame implements IMapCoordListener, BeanContextChild {
	
	private static final long serialVersionUID = 1L;	
	private Boolean locked = false;
	private JLabel moveHandler;
	private JPanel masterPanel;
	private JPanel statusPanel;
	private static int moveHandlerHeight = 12;
	private static int statusItemHeight = 15;
	private static int statusItemWidth = 90;
	private HashMap<String, JLabel> statusItems = new HashMap<String, JLabel>();
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
		statusItems.put("LAT", new JLabel(" LAT: N/A"));
        
		// Status: Y coordinate
		statusItems.put("LON", new JLabel(" LON: N/A"));

		

		
		// Status: Z coordinate
//		statusItems.put("Z", new JLabel("Z: 3.122"));
				

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
		for(Iterator<Entry<String, JLabel>> i = statusItems.entrySet().iterator();i.hasNext();) {
			statusPanel.add(i.next().getValue());
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

	@Override
	public void recieveCoord(LatLonPoint llp) {
		statusItems.get("LAT").setText(" LAT  " + Formatter.latToPrintable(llp.getLatitude()));
		statusItems.get("LON").setText(" LON " + Formatter.latToPrintable(llp.getLongitude()));

		
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
	
	// TODO: Add methods for updating the hashmap containing status values
}
