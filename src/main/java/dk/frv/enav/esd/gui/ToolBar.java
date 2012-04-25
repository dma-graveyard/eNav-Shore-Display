package dk.frv.enav.esd.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import dk.frv.enav.esd.event.ToolbarMoveMouseListener;

public class ToolBar extends JInternalFrame {
	
	private static final long serialVersionUID = 1L;	
	private Boolean locked = false;
	private JLabel moveHandler;
	private JPanel masterPanel;
	private JPanel buttonPanel;
	private static int moveHandlerHeight = 18;
	private static int toolItemSize = 35;
	private static int toolItemColumns = 2;
	private static int buttonPanelOffset = 4;
	private ArrayList<JLabel> toolItems = new ArrayList<JLabel>();
	public int width;
	public int height;
	private static int iconWidth = 16;
	private static int iconHeight = 16;
	private Border toolPaddingBorder = BorderFactory.createMatteBorder(0, 0, 3, 3, new Color(83, 83, 83));
	private Border toolInnerEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, new Color(37, 37, 37), new Color(52, 52, 52));
	

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
        moveHandler.setForeground(new Color(200, 200, 200));
        moveHandler.setOpaque(true);
        moveHandler.setBackground(Color.DARK_GRAY);
        moveHandler.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(30, 30, 30)));
        moveHandler.setFont(new Font("Arial", Font.BOLD, 9));
        moveHandler.setPreferredSize(new Dimension((toolItemSize * toolItemColumns), moveHandlerHeight));
        ToolbarMoveMouseListener mml = new ToolbarMoveMouseListener(this, mainFrame);
        moveHandler.addMouseListener(mml);
        moveHandler.addMouseMotionListener(mml);
		
		// Create the grid for the toolitems
        buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(0,2));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(3,3,0,0));
		buttonPanel.setBackground(new Color(83, 83, 83));
		
		
		
		// Setup toolitems (add here for more toolitems)
		// Tool: Select TODO
		final JLabel select = new JLabel(toolbarIcon("images/toolbar/select.png"));
		select.addMouseListener(new MouseAdapter() {  
		    public void mouseReleased(MouseEvent e) {  
		    	setActiveToolItem(select);
		    	
				for (int i = 0; i < mainFrame.getMapWindows().size(); i++) {
					mainFrame.getMapWindows().get(i).getChartPanel().setMouseMode(2);
				}
				mainFrame.setMouseMode(2);
		    }  
		});
		select.setBorder(toolPaddingBorder);
		toolItems.add(select);
		
		// Tool: Drag
		final JLabel drag = new JLabel(toolbarIcon("images/toolbar/drag.png"));
		drag.addMouseListener(new MouseAdapter() {  
		    public void mouseReleased(MouseEvent e) {  
		    	setActiveToolItem(drag);
		    	
				for (int i = 0; i < mainFrame.getMapWindows().size(); i++) {
					mainFrame.getMapWindows().get(i).getChartPanel().setMouseMode(1);
				}
				mainFrame.setMouseMode(1);
		    }  
		});
		drag.setBorder(toolPaddingBorder);
		toolItems.add(drag);
		
		// Tool: Zoom
		final JLabel zoom = new JLabel(toolbarIcon("images/toolbar/zoom.png"));
		zoom.addMouseListener(new MouseAdapter() {  
		    public void mouseReleased(MouseEvent e) {  
		    	setActiveToolItem(zoom);
		    	
				for (int i = 0; i < mainFrame.getMapWindows().size(); i++) {
					mainFrame.getMapWindows().get(i).getChartPanel().setMouseMode(0);
				}
				mainFrame.setMouseMode(0);
		    }  
		});  
		zoom.setBorder(toolPaddingBorder);
		toolItems.add(zoom);
		
		

	    // Create the masterpanel for aligning
	    masterPanel = new JPanel(new BorderLayout());
	    masterPanel.add(moveHandler, BorderLayout.NORTH);
	    masterPanel.add(buttonPanel, BorderLayout.SOUTH);
	    masterPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, new Color(30, 30, 30), new Color(45, 45, 45)));
	    this.getContentPane().add(masterPanel);
	    
	    // And finally refresh the toolbar
	    repaintToolbar();
	    
		// Set default active tool item
	    int mouseMode = mainFrame.getMouseMode();
	    if (mouseMode == 0){
	    	setActiveToolItem(zoom);
	    }
	    if (mouseMode == 1){
	    	setActiveToolItem(drag);
	    }
	    if (mouseMode == 2){
	    	setActiveToolItem(select);	
	    }
		
	}
	
	/*
	 * Function for setting the active tool item in the toolbar
	 * Author: Steffen D. Sommer
	 */
	public void setActiveToolItem(JLabel tool) {
		// Inactive all tools
		for(int i=0;i<toolItems.size();i++) {
			toolItems.get(i).setBorder(toolPaddingBorder);
			toolItems.get(i).setOpaque(false);
		}
		
		// Set active tool
        tool.setBackground(new Color(55, 55, 55));
        tool.setBorder(BorderFactory.createCompoundBorder(toolPaddingBorder, toolInnerEtchedBorder));
        tool.setOpaque(true);
	}
	
	/*
	 * Function for resizing the icons for the toolbar
	 * Author: Steffen D. Sommer
	 */
	public ImageIcon toolbarIcon(String imgpath) {
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
		for(int i=0;i<toolItems.size();i++) {
			buttonPanel.add(toolItems.get(i));
		}
		
		// Then calculate the size of the toolbar according to the number of toolitems
		double temp = (double) toolItems.size() / (double) toolItemColumns;
		width = toolItemSize * toolItemColumns;
		int innerHeight = (int) (Math.ceil(temp) * toolItemSize);
		height = innerHeight;
		
		if(!locked)
			height = innerHeight + moveHandlerHeight;
				
		// And finally set the size and repaint it
		buttonPanel.setSize(width, innerHeight - buttonPanelOffset);
		buttonPanel.setPreferredSize(new Dimension(width, innerHeight - buttonPanelOffset));
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
