package dk.frv.enav.esd.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import dk.frv.enav.esd.event.ToolbarMoveMouseListener;
import dk.frv.enav.esd.msi.IMsiUpdateListener;
import dk.frv.enav.esd.msi.MsiHandler;

public class NotificationArea extends ComponentFrame implements IMsiUpdateListener {
	
	private static final long serialVersionUID = 1L;	
	private Boolean locked = false;
	private JLabel moveHandler;
	private JPanel masterPanel;
	private JPanel notificationPanel;
	private static int moveHandlerHeight = 18;
	private static int notificationHeight = 25;
	private static int notificationWidth = 125;
	private static int notificationPanelOffset = 4;
	//private ArrayList<JLabel> notifications = new ArrayList<JLabel>();
	private HashMap<String, JPanel> notifications = new HashMap<String, JPanel>();
	private HashMap<String, String> services = new HashMap<String, String>();
	private HashMap<String, Integer> unreadMessages = new HashMap<String, Integer>();
	private HashMap<String, JLabel> indicatorLabels = new HashMap<String, JLabel>();
	public int width;
	public int height;
	private MsiHandler msiHandler;
	
	Border paddingLeft = BorderFactory.createMatteBorder(0, 8, 0, 0, new Color(65, 65, 65));
	Border paddingBottom = BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(83, 83, 83));
	Border notificationPadding = BorderFactory.createCompoundBorder(paddingBottom, paddingLeft);
	Border notificationsIndicatorImportant = BorderFactory.createMatteBorder(0, 0, 0, 10, new Color(206, 120, 120));
	Border paddingLeftPressed = BorderFactory.createMatteBorder(0, 8, 0, 0, new Color(45, 45, 45));
	Border notificationPaddingPressed = BorderFactory.createCompoundBorder(paddingBottom, paddingLeftPressed);

	public NotificationArea(final MainFrame mainFrame) {
		
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
        moveHandler.setForeground(new Color(200, 200, 200));
        moveHandler.setOpaque(true);
        moveHandler.setBackground(Color.DARK_GRAY);
        moveHandler.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(30, 30, 30)));
        moveHandler.setFont(new Font("Arial", Font.BOLD, 9));
        moveHandler.setPreferredSize(new Dimension(notificationWidth, moveHandlerHeight));
        ToolbarMoveMouseListener mml = new ToolbarMoveMouseListener(this, mainFrame);
        moveHandler.addMouseListener(mml);
        moveHandler.addMouseMotionListener(mml);
		
		// Create the grid for the notifications
        notificationPanel = new JPanel();
		notificationPanel.setLayout(new GridLayout(0,1));
        //notificationPanel.setLayout(new GridBagLayout());
		notificationPanel.setBorder(BorderFactory.createEmptyBorder(5,5,0,5));
		notificationPanel.setBackground(new Color(83, 83, 83));
		
		
		
		// Setup notifications (add here for more notifications)
		// Notification: MSI
		//final JLabel msi = new JLabel("MSI");
		final JPanel msi = new JPanel();
		notifications.put("msi", msi);
		services.put("msi", "MSI");
		
		msi.addMouseListener(new MouseAdapter() {  
			public void mousePressed(MouseEvent e) {
				msi.setBorder(notificationPaddingPressed);
				msi.setBackground(new Color(45, 45, 45));
			}
			
		    public void mouseReleased(MouseEvent e) {  
		    	msi.setBorder(notificationPadding);
		    	msi.setBackground(new Color(65, 65, 65));
		    	mainFrame.toggleNotificationCenter();
		    }  
		});
		
		// Notification: AIS
		//final JLabel ais = new JLabel("AIS");
		final JPanel ais = new JPanel();
		notifications.put("ais", ais);
		//unreadMessages.put("ais", 23);
		services.put("ais", "AIS");
		
		ais.addMouseListener(new MouseAdapter() {  
			public void mousePressed(MouseEvent e) {
				ais.setBorder(notificationPaddingPressed);
				ais.setBackground(new Color(45, 45, 45));
			}
			
		    public void mouseReleased(MouseEvent e) {  
		    	ais.setBorder(notificationPadding);
		    	ais.setBackground(new Color(65, 65, 65));
		    	mainFrame.toggleNotificationCenter();
		    }  
		});
		
		

	    // Create the masterpanel for aligning
	    masterPanel = new JPanel(new BorderLayout());
	    masterPanel.add(moveHandler, BorderLayout.NORTH);
	    masterPanel.add(notificationPanel, BorderLayout.SOUTH);
	    masterPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, new Color(30, 30, 30), new Color(45, 45, 45)));
	    this.getContentPane().add(masterPanel);
	 
	    // And finally refresh the notification area
	    repaintNotificationArea();
	}
	
	public void addMessage(String service) throws InterruptedException {
		unreadMessages.put(service, unreadMessages.get(service)+1);
		newMessage(service);
	}
	
	public void newMessage(final String key) throws InterruptedException {
		final int blinks = 20;
		
		final Runnable doChangeIndicator = new Runnable() {
			JLabel unreadIndicator = indicatorLabels.get(key);
		    boolean changeColor = false;
		    public void run() {
			if (changeColor = !changeColor) {
				unreadIndicator.setBackground(new Color(165, 80, 80));
			} else {
				unreadIndicator.setBackground(new Color(206, 120, 120));
			}
		    }
		};

		Runnable doBlinkIndicator = new Runnable() {
		    public void run() {
		    	for (int i = 0; i < blinks; i++) {
				    try {
				    	EventQueue.invokeLater(doChangeIndicator);
				    	Thread.sleep(500);
				    } catch (InterruptedException e) {
				    	return;
				    }
				}
		    }
		};
		
		new Thread(doBlinkIndicator).start();
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
		
		// Clear panel before adding services
		notificationPanel.removeAll(); 
		notificationPanel.updateUI();
		
		// Lets start by adding all the notifications
		for(Iterator<Entry<String, JPanel>> i = notifications.entrySet().iterator();i.hasNext();) {
			
			Entry<String, JPanel> entry = i.next();
			
			// Get values for service
			String service = services.get(entry.getKey());
			Integer messageCount = unreadMessages.get(entry.getKey());
			
			if(messageCount == null)
					messageCount = 0;
			
			if(service == null)
				service = "";
			
			// Style the notification panel
			//JPanel servicePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
			JPanel servicePanel = entry.getValue();
			servicePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			//servicePanel.setLayout(FlowLayout.LEFT, 0, 0);
			servicePanel.setBackground(new Color(65, 65, 65));
			servicePanel.setBorder(notificationPadding);
			servicePanel.setPreferredSize(new Dimension(notificationWidth, notificationHeight));
			
			// Create labels for each value
			// The label
			JLabel notification = new JLabel(service);
			notification.setPreferredSize(new Dimension(76, notificationHeight));
			notification.setFont(new Font("Arial", Font.PLAIN, 11));
			notification.setForeground(new Color(237, 237, 237));
			servicePanel.add(notification);
			
			// Unread messages
			JLabel messages = new JLabel(messageCount.toString(), SwingConstants.RIGHT);
			messages.setPreferredSize(new Dimension(20, notificationHeight));
			messages.setFont(new Font("Arial", Font.PLAIN, 9));
			messages.setForeground(new Color(100, 100, 100));
			messages.setBorder(BorderFactory.createEmptyBorder(0,0,0,5));
			servicePanel.add(messages);
			
			// The unread indicator
			JLabel unreadIndicator = new JLabel();
			unreadIndicator.setPreferredSize(new Dimension(7, notificationHeight));
			
			if(messageCount > 0) {
				unreadIndicator.setBackground(new Color(206, 120, 120));
				unreadIndicator.setOpaque(true);
			}
			
			servicePanel.add(unreadIndicator);
			
			notificationPanel.add(servicePanel);
		
			// Make list of indicator labels to use when blinking
			indicatorLabels.put(entry.getKey(), unreadIndicator);
			
		}
		/*
		OLD
		for(Iterator<JLabel> i = notifications.iterator();i.hasNext();) {
			//notificationPanel.add(i.next());
		}
		*/
		
		// Then calculate the size of the notification area according to the number of notifications
		width = notificationWidth;
		int innerHeight = (notifications.size() * (notificationHeight + 5)) + 5; // 5 and 5 for padding
		height = innerHeight + notificationPanelOffset;
		
		if(!locked)
			height = height + moveHandlerHeight;
		
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
	
	@Override
	public void findAndInit(Object obj) {
		if (obj instanceof MsiHandler) {
			msiHandler = (MsiHandler)obj;
			msiHandler.addListener(this);
			
			unreadMessages.put("msi", msiHandler.getUnAcknowledgedMSI());
			repaintNotificationArea();
		}
	}

	@Override
	public void msiUpdate() {
		//msiHandler.getUnAcknowledgedMSI();
		try {
			newMessage("msi");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}