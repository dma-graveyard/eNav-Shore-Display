package dk.frv.enav.esd.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import dk.frv.enav.esd.ESD;
import dk.frv.enav.esd.event.ToolbarMoveMouseListener;
import dk.frv.enav.esd.gui.settingtabs.MapSettingsPanel;
import dk.frv.enav.esd.gui.settingtabs.GuiStyler;
import dk.frv.enav.esd.settings.Settings;

public class JSettingsWindow extends ComponentFrame implements MouseListener {

	private static final long serialVersionUID = 1L;

	private JPanel backgroundPane;

	Border paddingLeft = BorderFactory.createMatteBorder(0, 8, 0, 0, new Color(65, 65, 65));
	Border paddingBottom = BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(83, 83, 83));
	Border notificationPadding = BorderFactory.createCompoundBorder(paddingBottom, paddingLeft);
	Border notificationsIndicatorImportant = BorderFactory.createMatteBorder(0, 0, 0, 10, new Color(206, 120, 120));
	Border paddingLeftPressed = BorderFactory.createMatteBorder(0, 8, 0, 0, new Color(45, 45, 45));
	Border notificationPaddingPressed = BorderFactory.createCompoundBorder(paddingBottom, paddingLeftPressed);

	Font defaultFont = new Font("Arial", Font.PLAIN, 11);
	Color textColor = new Color(237, 237, 237);

	private JLabel breadCrumps;

	private JLabel mapSettings;
	private JLabel connections;
	private JLabel windowSettings;
	private JLabel msiLayer;

	private MapSettingsPanel mapSettingsPanel;
	private JPanel connectionsSettingsPanel;
	private JPanel windowSettingsPanel;
	private JPanel msiSettingsPanel;

	private JLabel ok;
	private JLabel cancel;

	MouseMotionListener[] actions;
	private JLabel moveHandler;
	private JPanel masterPanel;
	private JPanel mapPanel;
	private static int moveHandlerHeight = 18;
	public int width;
	public int height;
	JInternalFrame settingsWindow = null;
	private MainFrame mainFrame;
	private Settings settings;

	/**
	 * Create the frame.
	 */
	public JSettingsWindow() {
		super("Settings Window", false, true, false, false);
		setSize(800, 600);
		setLocation(10, 10);

		settings = ESD.getSettings();

		setResizable(false);
		setTitle("Preferences");
		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 661, 481);
		backgroundPane = new JPanel();
		// backgroundPane.setBorder(new LineBorder(new Color(0, 0, 0)));
		// backgroundPane.setBackground(new Color(83, 83, 83));

		backgroundPane.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("653px:grow"), }, new RowSpec[] {
				FormFactory.NARROW_LINE_GAP_ROWSPEC, RowSpec.decode("23px:grow"), RowSpec.decode("428px:grow"), }));

		backgroundPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));

		JPanel topPanel = new JPanel();
		topPanel.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(70, 70, 70)));
		backgroundPane.add(topPanel, "1, 2, fill, fill");
		topPanel.setLayout(null);
		topPanel.setBackground(GuiStyler.backgroundColor);

		breadCrumps = new JLabel("Preferences > Map Settings");
		GuiStyler.styleText(breadCrumps);

		breadCrumps.setBounds(10, 4, 603, 14);
		breadCrumps.setHorizontalAlignment(SwingConstants.LEFT);
		topPanel.add(breadCrumps);

		JPanel bottomPanel = new JPanel();
		backgroundPane.add(bottomPanel, "1, 3, fill, fill");
		bottomPanel.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 0, 140, 428);
		scrollPane.setBorder(null);
		bottomPanel.add(scrollPane);

		// Panels

		JPanel menuPanel = new JPanel();
		scrollPane.setViewportView(menuPanel);
		menuPanel.setBackground(GuiStyler.backgroundColor);
		menuPanel.setLayout(null);

		JPanel labelContainer = new JPanel();
		labelContainer.setLocation(0, 0);
		labelContainer.setBackground(GuiStyler.backgroundColor);
		labelContainer.setSize(new Dimension(140, 500));

		menuPanel.add(labelContainer);

		String padding = "   ";
		mapSettings = new JLabel(padding + "Map Settings");
		GuiStyler.styleTabButton(mapSettings);
		labelContainer.add(mapSettings);

		// underMenu = new JLabel(padding + "submenu");
		// styleUnderMenu(underMenu);
		// labelContainer.add(underMenu);

		connections = new JLabel(padding + "Connections");
		GuiStyler.styleTabButton(connections);
		labelContainer.add(connections);

		windowSettings = new JLabel(padding + "Window Settings");
		GuiStyler.styleTabButton(windowSettings);
		labelContainer.add(windowSettings);

		msiLayer = new JLabel(padding + "MSI Layer");
		GuiStyler.styleTabButton(msiLayer);
		labelContainer.add(msiLayer);

		JPanel contentPane = new JPanel();
		contentPane.setBorder(new MatteBorder(0, 1, 0, 0, (Color) new Color(70, 70, 70)));
		contentPane.setBounds(140, 0, 513, 428);
		bottomPanel.add(contentPane);
		contentPane.setBackground(GuiStyler.backgroundColor);
		contentPane.setLayout(null);

		ok = new JLabel("  OK");
		ok.setBounds(400, 390, 30, 20);
		GuiStyler.styleButton(ok);
		contentPane.add(ok);

		cancel = new JLabel("  CANCEL");
		GuiStyler.styleButton(cancel);
		cancel.setBounds(437, 390, 55, 20);
		contentPane.add(cancel);

		// Content panels
		mapSettingsPanel = new MapSettingsPanel(settings);
		connectionsSettingsPanel = createConnectionsPanel();
		connectionsSettingsPanel.setVisible(false);
		windowSettingsPanel = createConnectionsPanel();
		windowSettingsPanel.setVisible(false);
		msiSettingsPanel = createConnectionsPanel();
		msiSettingsPanel.setVisible(false);

		contentPane.add(mapSettingsPanel);

		contentPane.add(connectionsSettingsPanel);

		contentPane.add(windowSettingsPanel);

		contentPane.add(windowSettingsPanel);

		contentPane.add(msiSettingsPanel);

		// JLabel lblHeadline = new JLabel("Headline");
		// lblHeadline.setBounds(10, 11, 243, 14);
		// generalSettingsPanel.add(lblHeadline);
		//
		// JLabel lblBla = new JLabel("bla");
		// lblBla.setBounds(10, 50, 46, 14);
		// generalSettingsPanel.add(lblBla);
		//
		// JLabel lblBlabla = new JLabel("blabla");
		// lblBlabla.setBounds(53, 87, 46, 14);
		// generalSettingsPanel.add(lblBlabla);

		addMouseListeners();

	}

	public void addMouseListeners() {
		mapSettings.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				mapSettings.setBackground(new Color(45, 45, 45));
				hideAllPanels();
				mapSettingsPanel.setVisible(true);
			}

			public void mouseReleased(MouseEvent e) {
				mapSettings.setBackground(new Color(65, 65, 65));
				breadCrumps.setText("Preferences > Map Settings");
			}

		});

		connections.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				connections.setBackground(new Color(45, 45, 45));
				hideAllPanels();
				connectionsSettingsPanel.setVisible(true);
			}

			public void mouseReleased(MouseEvent e) {
				connections.setBackground(new Color(65, 65, 65));
				breadCrumps.setText("Preferences > Connection Settings");
			}

		});

		windowSettings.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				windowSettings.setBackground(new Color(45, 45, 45));
				hideAllPanels();
				windowSettingsPanel.setVisible(true);
			}

			public void mouseReleased(MouseEvent e) {
				windowSettings.setBackground(new Color(65, 65, 65));
				breadCrumps.setText("Preferences > Window Settings");
			}

		});

		msiLayer.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				msiLayer.setBackground(new Color(45, 45, 45));
				hideAllPanels();
				msiSettingsPanel.setVisible(true);
			}

			public void mouseReleased(MouseEvent e) {
				msiLayer.setBackground(new Color(65, 65, 65));
				breadCrumps.setText("Preferences > MSI Layer Settings");
			}

		});

		ok.addMouseListener(this);
		cancel.addMouseListener(this);

		// ok.addMouseListener(new MouseAdapter() {
		// public void mousePressed(MouseEvent e) {
		// ok.setBackground(new Color(45, 45, 45));
		//
		// System.out.println("OK PRESSED");
		// }
		//
		// public void mouseReleased(MouseEvent e) {
		// ok.setBackground(new Color(65, 65, 65));
		// }
		//
		// });
		//
		// cancel.addMouseListener(new MouseAdapter() {
		// public void mousePressed(MouseEvent e) {
		// cancel.setBackground(new Color(45, 45, 45));
		// System.out.println("Cancel pressed");
		// }
		//
		// public void mouseReleased(MouseEvent e) {
		// cancel.setBackground(new Color(65, 65, 65));
		// }
		//
		// });

	}

	public JPanel createConnectionsPanel() {
		JPanel panel = new JPanel();
		panel.setBackground(GuiStyler.backgroundColor);
		panel.setBounds(10, 11, 493, 406);
		panel.setLayout(null);

		return panel;
	}

	private void hideAllPanels() {
		mapSettingsPanel.setVisible(false);
		connectionsSettingsPanel.setVisible(false);
		windowSettingsPanel.setVisible(false);
		msiSettingsPanel.setVisible(false);
	}

	/**
	 * Function for setting up custom GUI for the map frame
	 */
	public void initGUI() {

		settingsWindow = this;

		// Strip off
		setRootPaneCheckingEnabled(false);
		((javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI()).setNorthPane(null);
		this.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		// Map tools
		mapPanel = new JPanel(new GridLayout(1, 3));
		mapPanel.setPreferredSize(new Dimension(500, moveHandlerHeight));
		mapPanel.setOpaque(true);
		mapPanel.setBackground(Color.DARK_GRAY);
		mapPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(30, 30, 30)));

		ToolbarMoveMouseListener mml = new ToolbarMoveMouseListener(this, mainFrame);
		mapPanel.addMouseListener(mml);
		mapPanel.addMouseMotionListener(mml);

		// Placeholder - for now
		mapPanel.add(new JLabel());

		// Movehandler/Title dragable)
		moveHandler = new JLabel("Preferences", JLabel.CENTER);
		moveHandler.setFont(new Font("Arial", Font.BOLD, 9));
		moveHandler.setForeground(new Color(200, 200, 200));
		moveHandler.addMouseListener(this);
		moveHandler.addMouseListener(mml);
		moveHandler.addMouseMotionListener(mml);
		actions = moveHandler.getListeners(MouseMotionListener.class);
		mapPanel.add(moveHandler);

		// The tools (minimize, maximize and close)
		JPanel mapToolsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		mapToolsPanel.setOpaque(false);
		mapToolsPanel.setPreferredSize(new Dimension(60, 50));

		JLabel close = new JLabel(new ImageIcon("images/window/close.png"));
		close.addMouseListener(new MouseAdapter() {

			public void mouseReleased(MouseEvent e) {
				settingsWindow.setVisible(false);
			}

		});
		mapToolsPanel.add(close);
		mapPanel.add(mapToolsPanel);

		// Create the masterpanel for aligning
		masterPanel = new JPanel(new BorderLayout());
		masterPanel.add(mapPanel, BorderLayout.NORTH);
		masterPanel.add(backgroundPane, BorderLayout.SOUTH);
		masterPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, new Color(30, 30, 30), new Color(
				45, 45, 45)));

		this.setContentPane(masterPanel);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {

		if (arg0.getSource() == ok) {
			// Map settings
			mapSettingsPanel.saveSettings();
			settings.saveToFile();
			this.setVisible(false);
		}

		if (arg0.getSource() == cancel) {
			this.setVisible(false);
		}

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void findAndInit(Object obj) {
		if (obj instanceof MainFrame) {
			mainFrame = (MainFrame) obj;
			initGUI();
		}
	}

	/**
	 * Change the visiblity
	 */
	public void toggleVisibility() {
		setVisible(!this.isVisible());
	}

}
