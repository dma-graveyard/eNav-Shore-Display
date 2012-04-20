package dk.frv.enav.esd.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class NotificationArea extends JInternalFrame {
	
	private static final long serialVersionUID = 1L;	
	private Boolean locked = false;
	private JLabel moveHandler;
	private JPanel masterPanel;
	private JPanel notificationPanel;
	private static int moveHandlerHeight = 12;
	private static int notificationHeight = 35;
	private static int notificationWidth = 70;
	private ArrayList<JButton> notifications = new ArrayList<JButton>();
	public int width;
	public int height;

	public NotificationArea(MainFrame mainFrame) {
		
		// Setup location
		this.setLocation((10+moveHandlerHeight), (40 + mainFrame.getToolbar().getHeight()));
		this.setSize(100, 400);
		this.setVisible(true);
		this.setResizable(false);
		
		// Strip off window looks
		setRootPaneCheckingEnabled(false);
		((javax.swing.plaf.basic.BasicInternalFrameUI)this.getUI()).setNorthPane(null);
		this.setBorder(null);
		
        // Create the top movehandler (for dragging)
        moveHandler = new JLabel("Notifications", JLabel.CENTER);
        moveHandler.setForeground(Color.WHITE);
        moveHandler.setOpaque(true);
        moveHandler.setBackground(Color.DARK_GRAY);
        moveHandler.setPreferredSize(new Dimension(notificationWidth, moveHandlerHeight));
        MoveMouseListener mml = new MoveMouseListener(this, mainFrame);
        moveHandler.addMouseListener(mml);
        moveHandler.addMouseMotionListener(mml);
		
		// Create the grid for the notifications
        notificationPanel = new JPanel();
		notificationPanel.setLayout(new GridLayout(0,1));
		notificationPanel.setBorder(BorderFactory.createLineBorder (Color.DARK_GRAY, 2));
		
		
		// Setup notifications (add here for more notifications)
		// Notification: MSI
		JButton msi = new JButton("MSI");
		msi.setToolTipText("Messages from MSI");
		msi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
//				System.out.println("MSI clicked");
			}
        }); 
		notifications.add(msi);
		
		// Notification: AIS
		JButton ais = new JButton("AIS");
		ais.setToolTipText("Messages from AIS");
		ais.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
//				System.out.println("AIS clicked");
			}
        }); 
		notifications.add(ais);
				

	    // Create the masterpanel for aligning
	    masterPanel = new JPanel(new BorderLayout());
	    masterPanel.add(moveHandler, BorderLayout.NORTH);
	    masterPanel.add(notificationPanel, BorderLayout.SOUTH);
	    this.getContentPane().add(masterPanel);
	 
	    // And finally refresh the notification area
	    repaintNotificationArea();
	    
	}
	
	/*
	 * Function for locking/unlocking the notification area
	 * Author: Steffen D. Sommer
	 */
	public void toggleLock() {
		if(locked) {
			masterPanel.add(moveHandler, BorderLayout.NORTH);
			locked = false;
			repaintNotificationArea();
			
			// Align the notification area according to the height of the movehandler
			int newX = (int) (this.getLocation().getX());
			int newY = (int) (this.getLocation().getY());
			Point new_location = new Point(newX, (newY - moveHandlerHeight));
			this.setLocation(new_location);

		} else {
			masterPanel.remove(moveHandler);
			locked = true;
			repaintNotificationArea();
			
			// Align the notification area according to the height of the movehandler
			int newX = (int) (this.getLocation().getX());
			int newY = (int) (this.getLocation().getY());
			Point new_location = new Point(newX, (newY + moveHandlerHeight));
			this.setLocation(new_location);
		}
	}
	
	/*
	 * Function for refreshing the notification area after editing notifications, size etc.
	 * Author: Steffen D. Sommer
	 */
	public void repaintNotificationArea() {
		
		// Lets start by adding all the notifications
		for(Iterator<JButton> i = notifications.iterator();i.hasNext();) {
			//JLayeredPane lpane = new JLayeredPane();
			//lpane.setBackground(Color.BLACK);
			//notificationPanel.add(lpane);
			//lpane.setBounds(0, 0, 50, 50);
			//lpane.setPreferredSize(new Dimension(50, 50));
			//lpane.setBounds(20, 10, 10, 10);
			
			//lpane.add(i.next());
			notificationPanel.add(i.next());
			
			//JLabel l = new JLabel("13", new OvalIcon(15,15, Color.BLACK), SwingConstants.CENTER);
		    //l.setHorizontalTextPosition(SwingConstants.CENTER);
			//l.setForeground(Color.WHITE);
			//l.setOpaque(false);
			//l.setBounds(75, 12, 20, 20);
			
			//lpane.add(i.next(), new Integer(0), 0);
			//lpane.add(l, new Integer(1), 0);
			
			//notificationPanel.add(lpane);
		}
		
		// Then calculate the size of the notification area according to the number of notifications
		width = notificationWidth;
		int innerHeight = notifications.size() * notificationHeight;
		height = innerHeight;
		
		if(!locked)
			height = innerHeight + moveHandlerHeight;
		
		// And finally set the size and repaint it
		notificationPanel.setSize(width, innerHeight);
		notificationPanel.setPreferredSize(new Dimension(width, innerHeight));
		this.setSize(width, height);
		this.revalidate();
		this.repaint();
		
	}
	
	/*
	 * Function for getting the width of the notification area
	 * @return width Width of the notification areas
	 */
	public int getWidth() {
		return width;
	}
	
	/*
	 * Function for getting the height of the notification area
	 * @return height Height of the notification area
	 */
	public int getHeight() {
		return height;
	}
}

class OvalIcon implements Icon {

	  private int width, height;
	  private Color col;

	  public OvalIcon(int w, int h, Color c) {
	    width = w;
	    height = h;
	    col = c;
	  }

	  public void paintIcon(Component c, Graphics g, int x, int y) {
	    g.drawOval(x, y, width, height);
	    g.setColor(col);
	    g.fillOval(x, y, width, height);
	  }

	  public int getIconWidth() {
	    return width;
	  }

	  public int getIconHeight() {
	    return height;
	  }
}

