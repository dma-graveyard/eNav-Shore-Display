package dk.frv.enav.esd.gui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import dk.frv.enav.esd.event.ToolbarMoveMouseListener;
import dk.frv.enav.esd.gui.msi.MsiTableModel;
import dk.frv.enav.esd.gui.route.RouteExchangeTableModel;
import dk.frv.enav.esd.gui.utils.ComponentFrame;
import dk.frv.enav.esd.msi.IMsiUpdateListener;
import dk.frv.enav.esd.msi.MsiHandler;
import dk.frv.enav.esd.msi.MsiHandler.MsiMessageExtended;
import dk.frv.enav.esd.service.ais.AisServices;

public class NotificationCenter extends ComponentFrame implements ListSelectionListener, ActionListener,
		IMsiUpdateListener {

	private static final long serialVersionUID = 1L;

	Border paddingLeft = BorderFactory.createMatteBorder(0, 8, 0, 0, new Color(65, 65, 65));
	Border paddingBottom = BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(83, 83, 83));
	Border notificationPadding = BorderFactory.createCompoundBorder(paddingBottom, paddingLeft);
	Border notificationsIndicatorImportant = BorderFactory.createMatteBorder(0, 0, 0, 10, new Color(206, 120, 120));
	Border paddingLeftPressed = BorderFactory.createMatteBorder(0, 8, 0, 0, new Color(45, 45, 45));
	Border notificationPaddingPressed = BorderFactory.createCompoundBorder(paddingBottom, paddingLeftPressed);

	private MainFrame mainFrame;
	private HashMap<String, Integer> unreadMessages = new HashMap<String, Integer>();
	private HashMap<String, JLabel> unreadMessagesLabels = new HashMap<String, JLabel>();
	private HashMap<String, JLabel> indicatorLabels = new HashMap<String, JLabel>();

	private static int notificationHeight = 25;
	private static int notificationWidth = 125;
	private JTable msiTable;

	private MsiHandler msiHandler;
	private MsiTableModel msiTableModel;

	private AisServices aisService;


	private JPanel msiPanelLeft;

	private JPanel routePanelLeft;

	private Color leftButtonColor = Color.DARK_GRAY;
	private Color leftButtonColorClicked = new Color(45, 45, 45);
	private Color backgroundColor = new Color(83, 83, 83);

	private JLabel moveHandler;
	private JPanel masterPanel;
	private static int moveHandlerHeight = 18;
	private JPanel topBar;
	private JPanel notificationContentPanel;

	private MSINotificationPanel msiPanel;
	

	public NotificationCenter() {

		super("NotificationCenter", false, true, false, false);

		// Strip off window looks
		setRootPaneCheckingEnabled(false);
		((javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI()).setNorthPane(null);
		this.setBorder(null);

		// Map tools
		topBar = new JPanel(new GridLayout(1, 3));
		topBar.setPreferredSize(new Dimension(500, moveHandlerHeight));
		topBar.setOpaque(true);
		topBar.setBackground(Color.DARK_GRAY);
		topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(30, 30, 30)));

		// Placeholder - for now
		topBar.add(new JLabel());

		// Movehandler/Title dragable)
		moveHandler = new JLabel("Notification Center", JLabel.CENTER);
		moveHandler.setFont(new Font("Arial", Font.BOLD, 9));
		moveHandler.setForeground(new Color(200, 200, 200));
		// actions = moveHandler.getListeners(MouseMotionListener.class);
		topBar.add(moveHandler);

		// The tools (minimize, maximize and close)
		JPanel windowToolsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		windowToolsPanel.setOpaque(false);
		windowToolsPanel.setPreferredSize(new Dimension(60, 50));

		JLabel close = new JLabel(new ImageIcon("images/window/close.png"));
		close.addMouseListener(new MouseAdapter() {

			public void mouseReleased(MouseEvent e) {
				toggleVisibility();
			}
		});

		close.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 2));
		windowToolsPanel.add(close);
		topBar.add(windowToolsPanel);

		JPanel outerPanel = new JPanel();

		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 600);
		outerPanel.setPreferredSize(new Dimension(900, 600 - moveHandlerHeight));
		outerPanel.setSize(new Dimension(900, 600 - moveHandlerHeight));
		outerPanel.setBackground(backgroundColor);
		GridBagLayout gbl_outerPanel = new GridBagLayout();
		gbl_outerPanel.columnWidths = new int[] { 160, 768, 0 };
		gbl_outerPanel.rowHeights = new int[] { 755, 0 };
		gbl_outerPanel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_outerPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		outerPanel.setLayout(gbl_outerPanel);

		Integer messageCountMSI = unreadMessages.get("MSI");
		if (messageCountMSI == null)
			messageCountMSI = 0;

		String[] colHeadings = { "ID", "Title" };
		DefaultTableModel model = new DefaultTableModel(30, colHeadings.length);
		model.setColumnIdentifiers(colHeadings);

		masterPanel = new JPanel(new BorderLayout());
		masterPanel.add(topBar, BorderLayout.NORTH);
		masterPanel.add(outerPanel, BorderLayout.CENTER);

		JPanel LeftLabelPane = new JPanel();
		LeftLabelPane.setLayout(new FlowLayout());
		
//		LeftLabelPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, new Color(30, 30, 30), new Color(
//				45, 45, 45)));
		LeftLabelPane.setBorder(new MatteBorder(0, 0, 0, 1, new Color(30, 30, 30)));
		
//		LeftLabelPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		LeftLabelPane.setBackground(backgroundColor);
		GridBagConstraints gbc_LeftLabelPane = new GridBagConstraints();
		gbc_LeftLabelPane.fill = GridBagConstraints.BOTH;
		gbc_LeftLabelPane.insets = new Insets(0, 0, 0, 5);
		gbc_LeftLabelPane.gridx = 0;
		gbc_LeftLabelPane.gridy = 0;
		outerPanel.add(LeftLabelPane, gbc_LeftLabelPane);

		JPanel labelContainer = new JPanel();
		labelContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		labelContainer.setPreferredSize(new Dimension(notificationWidth, notificationHeight * 3));
		((FlowLayout) labelContainer.getLayout()).setVgap(3);
		labelContainer.setBackground(backgroundColor);
		LeftLabelPane.add(labelContainer);

		// Style the MSI notification panel
		msiPanelLeft = new JPanel();
		msiPanelLeft.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		msiPanelLeft.setBackground(new Color(65, 65, 65));
		msiPanelLeft.setPreferredSize(new Dimension(notificationWidth, notificationHeight));
		msiPanelLeft.setSize(new Dimension(notificationWidth, notificationHeight));

		// Style the Route notification panel
		routePanelLeft = new JPanel();
		routePanelLeft.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		routePanelLeft.setBackground(new Color(65, 65, 65));
		routePanelLeft.setPreferredSize(new Dimension(notificationWidth, notificationHeight));
		routePanelLeft.setSize(new Dimension(notificationWidth, notificationHeight));

		// Create labels for each service

		// MSI
		JLabel notification = new JLabel("  MSI");
		notification.setPreferredSize(new Dimension(98, notificationHeight));
		notification.setSize(new Dimension(76, notificationHeight));
		notification.setFont(new Font("Arial", Font.PLAIN, 11));
		notification.setForeground(new Color(237, 237, 237));
		msiPanelLeft.add(notification);
		
		// Unread messages
		JLabel messages = new JLabel(messageCountMSI.toString(), SwingConstants.RIGHT);
		messages.setPreferredSize(new Dimension(20, notificationHeight));
		messages.setSize(new Dimension(20, notificationHeight));
		messages.setFont(new Font("Arial", Font.PLAIN, 9));
		messages.setForeground(new Color(100, 100, 100));
		messages.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		msiPanelLeft.add(messages);
		// The unread indicator
		JLabel msiUnreadIndicator = new JLabel();
		msiUnreadIndicator.setPreferredSize(new Dimension(7, notificationHeight));
		msiUnreadIndicator.setSize(new Dimension(7, notificationHeight));
		msiPanelLeft.add(msiUnreadIndicator);

		labelContainer.add(msiPanelLeft);

		// Route Exchange
		JLabel notificationRoute = new JLabel("  Route Exchange");
		notificationRoute.setPreferredSize(new Dimension(98, notificationHeight));
		notificationRoute.setSize(new Dimension(76, notificationHeight));
		notificationRoute.setFont(new Font("Arial", Font.PLAIN, 11));
		notificationRoute.setForeground(new Color(237, 237, 237));
		routePanelLeft.add(notificationRoute);

		// The unread indicator
		JLabel unreadIndicatorRoute = new JLabel();
		unreadIndicatorRoute.setPreferredSize(new Dimension(7, notificationHeight));
		unreadIndicatorRoute.setSize(new Dimension(7, notificationHeight));
		routePanelLeft.add(unreadIndicatorRoute);

		labelContainer.add(routePanelLeft);

		// Make list of labels to use when updating service
		indicatorLabels.put("MSI", msiUnreadIndicator);
		unreadMessagesLabels.put("MSI", messages);

		notificationContentPanel = new JPanel();
		GridBagConstraints gbc_notificationContentPanel = new GridBagConstraints();
		gbc_notificationContentPanel.fill = GridBagConstraints.BOTH;
		gbc_notificationContentPanel.gridx = 1;
		gbc_notificationContentPanel.gridy = 0;
		outerPanel.add(notificationContentPanel, gbc_notificationContentPanel);
		GridBagLayout gbl_notificationContentPanel = new GridBagLayout();
		gbl_notificationContentPanel.columnWidths = new int[]{752, 0};
		gbl_notificationContentPanel.rowHeights = new int[]{900, 0};
		gbl_notificationContentPanel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_notificationContentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		notificationContentPanel.setLayout(gbl_notificationContentPanel);
		masterPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, new Color(30, 30, 30), new Color(
				45, 45, 45)));
		this.getContentPane().add(masterPanel);
		
		msiPanel = new MSINotificationPanel();
		msiTable = msiPanel.getMsiTable();
		
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		
		
		//Add notification panel
		notificationContentPanel.add(msiPanel, gbc_panel);
		msiPanel.setVisible(true);
		
		JPanel pink = new JPanel();
		pink.setBackground(Color.pink);
//		notificationContentPanel.add(pink, gbc_panel);
		
		
		addMouseListeners();
		
	}

	public void newMessage(final String service) throws InterruptedException {
		final int blinks = 20;
		final Runnable doChangeIndicator = new Runnable() {

			JLabel unreadIndicator = indicatorLabels.get(service);
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

	
	public void setMessages(String service, int messageCount) throws InterruptedException {

		JLabel unread = unreadMessagesLabels.get(service);
		JLabel unreadIndicator = indicatorLabels.get(service);
		Integer currentCount = unreadMessages.get(service);

		if (currentCount == null)
			currentCount = 0;

		// If no unread messages, remove the red indicator for the service
		if (messageCount == 0)
			unreadIndicator.setOpaque(false);

		// Update the unread messages label if it differs
		if (messageCount != currentCount)
			unread.setText(Integer.toString(messageCount));

		// If new unread messages are received, start the blinking indicator
		if (messageCount > currentCount) {
			unreadIndicator.setOpaque(true);
			newMessage(service);
		}

		unreadMessages.put(service, messageCount);
	}

//	public void removeArea() {
//		area.setText(doc.toString());
//		pane_3.setVisible(false);
//		scrollPane_1.setVisible(false);
//	}

//	public void styleButton(JLabel label, Color color) {
//		label.setPreferredSize(new Dimension(125, 25));
//		label.setFont(new Font("Arial", Font.PLAIN, 11));
//		label.setForeground(new Color(237, 237, 237));
//		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
//		label.setBackground(color);
//		label.setOpaque(true);
//	}

//	public void addMouseListeners() {
//
//		msiPanel.addMouseListener(new MouseAdapter() {
//			public void mousePressed(MouseEvent e) {
//				msiPanel.setBackground(leftButtonColorClicked);
//			}
//
//			public void mouseReleased(MouseEvent e) {
//				msiPanel.setBackground(leftButtonColor);
//			}
//
//			public void mouseClicked(MouseEvent e) {
//				// Activate and update table
//				leftScrollPane.setViewportView(msiTable);
//				msiTable.setFocusable(true);
//				showMiddleTable(0); // 0 = MSI
//
//			}
//		});
//
//		routePanel.addMouseListener(new MouseAdapter() {
//			public void mousePressed(MouseEvent e) {
//				routePanel.setBackground(leftButtonColorClicked);
//			}
//
//			public void mouseReleased(MouseEvent e) {
//				routePanel.setBackground(leftButtonColor);
//			}
//
//			public void mouseClicked(MouseEvent e) {
//				// Activate and update table
//				leftScrollPane.setViewportView(routeTable);
//				routeTable.setFocusable(true);
//				showMiddleTable(1); // 0 = Route Exchange
//			}
//		});
//
//		but_read.addMouseListener(new MouseAdapter() {
//			public void mouseClicked(MouseEvent e) {
//				if (selectedService == 0) { // MSI
//					int rowAfter = selectedRow;
//
//					MsiMessage msiMessage = msiHandler.getMessageList().get(selectedRow).msiMessage;
//					msiHandler.setAcknowledged(msiMessage);
//					msiUpdate();
//					showMiddleTable(0);
//					msiTable.changeSelection(rowAfter, 0, false, false);
//
//				}
//			}
//		});
//
//		but_goto.addMouseListener(new MouseAdapter() {
//			public void mouseClicked(MouseEvent e) {
//				if (mainFrame.getActiveMapWindow() != null) {
//					mainFrame.getActiveMapWindow().getChartPanel()
//							.zoomToPoint(msiTableModel.getMessageLatLon(selectedRow));
//				} else if (mainFrame.getMapWindows().size() > 0) {
//					mainFrame.getMapWindows().get(0).getChartPanel()
//							.zoomToPoint(msiTableModel.getMessageLatLon(selectedRow));
//				}
//			}
//		});
//
//		but_delete.addMouseListener(new MouseAdapter() {
//			public void mouseClicked(MouseEvent e) {
//				if (selectedService == 0) { // MSI
//
//					MsiMessage msiMessage = msiHandler.getMessageList().get(selectedRow).msiMessage;
//					msiHandler.deleteMessage(msiMessage);
//					msiUpdate();
//					showMiddleTable(0);
//				}
//			}
//		});
//	}

	public void showMessage(int service, int msgId) {

		int index = -1;

		List<MsiMessageExtended> messages = msiTableModel.getMessages();
		for (int i = 0; i < messages.size(); i++) {
			MsiMessageExtended message = messages.get(i);
			if (message.msiMessage.getMessageId() == msgId) {

				index = i;
				break;
			}
		}
		
		msiPanel.readMessage(index, index);
	}


	@Override
	public void findAndInit(Object obj) {
		if (obj instanceof MsiHandler) {
			msiHandler = (MsiHandler) obj;
			msiHandler.addListener(this);
			msiTableModel = new MsiTableModel(msiHandler);
			msiTable.setModel(msiTableModel);
			msiPanel.setMsiHandler(msiHandler);
			msiPanel.initTable();
		}
		if (obj instanceof MainFrame) {
			mainFrame = (MainFrame) obj;
			ToolbarMoveMouseListener mml = new ToolbarMoveMouseListener(this, mainFrame);
			topBar.addMouseListener(mml);
			topBar.addMouseMotionListener(mml);
		}
		if (obj instanceof AisServices) {
			aisService = (AisServices) obj;
			// msiHandler.addListener(this);
//			routeTableModel = new RouteExchangeTableModel(aisService);
		}
	}

	@Override
	public void msiUpdate() {
		try {
			setMessages("MSI", msiHandler.getUnAcknowledgedMSI());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("New messages are in");
		msiPanel.updateTable();
		

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * Change the visiblity
	 */
	public void toggleVisibility() {
		setVisible(!this.isVisible());
	}

	
	public void addMouseListeners() {

		msiPanelLeft.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				msiPanelLeft.setBackground(leftButtonColorClicked);
			}

			public void mouseReleased(MouseEvent e) {
				msiPanelLeft.setBackground(leftButtonColor);
			}

			public void mouseClicked(MouseEvent e) {
				// Activate and update table
				
				//aispanel not visible
				msiPanel.setVisible(true);

			}
		});
		
		routePanelLeft.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				routePanelLeft.setBackground(leftButtonColorClicked);
			}

			public void mouseReleased(MouseEvent e) {
				routePanelLeft.setBackground(leftButtonColor);
			}

			public void mouseClicked(MouseEvent e) {
				msiPanel.setVisible(false);
			}
		});
	}

	public void toggleVisibility(int service) {

		setVisible(!this.isVisible());
		setNotificationView(service);
		
	}
	
	
	private void setNotificationView(int service){
		switch (service) {
		case 0:

			//Activate MSI
			//aisPanel.setVisible(false);
			
			msiPanel.setVisible(true);
			
			break;
			
		case 1:
			
			//aisPanel.setVisible(true);
			msiPanel.setVisible(false);
			
			break;	
		default:
			break;
		}
	}
}


