package dk.frv.enav.esd.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ToolBar extends JInternalFrame {
	
	private static final long serialVersionUID = 1L;	
	private Boolean locked = false;
	private JLabel moveHandler;
	private JPanel masterPanel;
	private JPanel buttonPanel;
	private static int moveHandlerHeight = 12;
	private static int toolItemSize = 35;
	private static int toolItemColumns = 2;
	private ArrayList<JButton> toolItems = new ArrayList<JButton>();
	public int width;
	public int height;

	public ToolBar(MainFrame mainFrame) {
		
		// Setup location
		this.setLocation((10+moveHandlerHeight), 10);
		this.setVisible(true);
		this.setResizable(false);
		
		// Strip off window looks
		setRootPaneCheckingEnabled(false);
		((javax.swing.plaf.basic.BasicInternalFrameUI)this.getUI()).setNorthPane(null);
		this.setBorder(null);
		
        // Create the top movehandler (for dragging)
        moveHandler = new JLabel("Toolbar", JLabel.CENTER);
        moveHandler.setForeground(Color.WHITE);
        moveHandler.setOpaque(true);
        moveHandler.setBackground(Color.DARK_GRAY);
        moveHandler.setPreferredSize(new Dimension((toolItemSize * toolItemColumns), moveHandlerHeight));
        MoveMouseListener mml = new MoveMouseListener(this, mainFrame);
        moveHandler.addMouseListener(mml);
        moveHandler.addMouseMotionListener(mml);
		
		// Create the grid for the toolitems
        buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(0,2));
		buttonPanel.setBorder(BorderFactory.createLineBorder (Color.DARK_GRAY, 2));
		
		
		// Setup toolitems (add here for more toolitems)
		// Tool: Zoom
		JButton zoom = new JButton(new ImageIcon("images/toolbar/zoom.png"));
		zoom.setToolTipText("Zoom in by clicking, hold shift for zoom out");
		zoom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
//				System.out.println("Zoom clicked");
			}
        }); 
		toolItems.add(zoom);
				

	    // Create the masterpanel for aligning
	    masterPanel = new JPanel(new BorderLayout());
	    masterPanel.add(moveHandler, BorderLayout.NORTH);
	    masterPanel.add(buttonPanel, BorderLayout.SOUTH);
	    this.getContentPane().add(masterPanel);
	 
	    // And finally refresh the toolbar
	    repaintToolbar();
	}
	
	/*
	 * Function for locking/unlocking the toolbar
	 * Author: Steffen D. Sommer
	 */
	public void toggleLock() {
		if(locked) {
			masterPanel.add(moveHandler, BorderLayout.NORTH);
			locked = false;
			repaintToolbar();
			
			// Align the toolbar according to the height of the movehandler
			int newX = (int) (this.getLocation().getX());
			int newY = (int) (this.getLocation().getY());
			Point new_location = new Point(newX, (newY - moveHandlerHeight));
			this.setLocation(new_location);

		} else {
			masterPanel.remove(moveHandler);
			locked = true;
			repaintToolbar();
			
			// Align the toolbar according to the height of the movehandler
			int newX = (int) (this.getLocation().getX());
			int newY = (int) (this.getLocation().getY());
			Point new_location = new Point(newX, (newY + moveHandlerHeight));
			this.setLocation(new_location);
		}
	}
	
	/*
	 * Function for refreshing the toolbar after editing toolitems, size etc.
	 * Author: Steffen D. Sommer
	 */
	public void repaintToolbar() {
		
		// Lets start by adding all the toolitems
		for(Iterator<JButton> i = toolItems.iterator();i.hasNext();) {
			buttonPanel.add(i.next());
		}
		
		// Then calculate the size of the toolbar according to the number of toolitems
		double temp = (double) toolItems.size() / (double) toolItemColumns;
		width = toolItemSize * toolItemColumns;
		int innerHeight = (int) (Math.ceil(temp) * toolItemSize);
		height = innerHeight;
		
		if(!locked)
			height = innerHeight + moveHandlerHeight;
		
		// And finally set the size and repaint it
		buttonPanel.setSize(width, innerHeight);
		buttonPanel.setPreferredSize(new Dimension(width, innerHeight));
		this.setSize(width, height);
		this.revalidate();
		this.repaint();
		
	}
	
	/*
	 * Function for getting the width of the toolbar
	 * @return width Width of the toolbar
	 */
	public int getWidth() {
		return width;
	}
	
	/*
	 * Function for getting the height of the toolbar
	 * @return height Height of the toolbar
	 */
	public int getHeight() {
		return height;
	}
}
