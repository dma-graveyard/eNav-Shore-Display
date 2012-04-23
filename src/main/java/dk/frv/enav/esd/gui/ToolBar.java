package dk.frv.enav.esd.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
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

import dk.frv.enav.esd.event.ToolbarMoveMouseListener;

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
	private static int iconWidth = 16;
	private static int iconHeight = 16;

	public ToolBar(final MainFrame mainFrame) {
		
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
        ToolbarMoveMouseListener mml = new ToolbarMoveMouseListener(this, mainFrame);
        moveHandler.addMouseListener(mml);
        moveHandler.addMouseMotionListener(mml);
		
		// Create the grid for the toolitems
        buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(0,2));
		buttonPanel.setBorder(BorderFactory.createLineBorder (Color.DARK_GRAY, 2));
		
		
		// Setup toolitems (add here for more toolitems)
		// Tool: Zoom
		final JButton zoom = new JButton(toolbarIcon("images/toolbar/zoom.png")); //toolbarIcon("images/toolbar/zoom.png")
		final JButton drag = new JButton(toolbarIcon("images/toolbar/drag.png"));
		drag.setSelected(true); // Enabled per default
		
		//zoom.setHorizontalAlignment(SwingConstants.CENTER);
		//zoom.setVerticalAlignment(SwingConstants.CENTER);
		//drag.setHorizontalAlignment(SwingConstants.CENTER);
		//drag.setAlignmentX(SwingConstants.CENTER);
		
		zoom.setToolTipText("Zoom in by clicking, hold shift for zoom out");
		zoom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for (int i = 0; i < mainFrame.getMapWindows().size(); i++) {
					mainFrame.getMapWindows().get(i).getChartPanel().setMouseMode(0);
					mainFrame.setMouseMode(0);
				}
				zoom.setSelected(true);
				drag.setSelected(false);
			}
        });
		toolItems.add(zoom);
		
		drag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for (int i = 0; i < mainFrame.getMapWindows().size(); i++) {
					mainFrame.getMapWindows().get(i).getChartPanel().setMouseMode(1);
					mainFrame.setMouseMode(1);
				}
				drag.setSelected(true);
				zoom.setSelected(false);
			}
        }); 
		toolItems.add(drag);
				

	    // Create the masterpanel for aligning
	    masterPanel = new JPanel(new BorderLayout());
	    masterPanel.add(moveHandler, BorderLayout.NORTH);
	    masterPanel.add(buttonPanel, BorderLayout.SOUTH);
	    this.getContentPane().add(masterPanel);
	 
	    // And finally refresh the toolbar
	    repaintToolbar();
	}
	
	/*
	 * Function for resizing the icons for the toolbar
	 * Author: Steffen D. Sommer
	 */
	public ImageIcon toolbarIcon(String imgpath) {
		/*
		ImageIcon icon = new ImageIcon(imgpath);
		Image img = icon.getImage();
		BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
		g.drawImage(img, 0, 0, iconWidth, iconHeight, null);
		ImageIcon newIcon = new ImageIcon(bi);
		
		return newIcon;
		*/
		ImageIcon icon = new ImageIcon(imgpath);
		Image img = icon.getImage();  
		Image newimg = img.getScaledInstance(iconWidth, iconHeight,  java.awt.Image.SCALE_DEFAULT);  
		return new ImageIcon(newimg); 
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
