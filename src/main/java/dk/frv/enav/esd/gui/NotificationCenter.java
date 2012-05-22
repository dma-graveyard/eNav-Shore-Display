package dk.frv.enav.esd.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.esd.event.ToolbarMoveMouseListener;
import dk.frv.enav.esd.gui.msi.MsiTableModel;
import dk.frv.enav.esd.msi.IMsiUpdateListener;
import dk.frv.enav.esd.msi.MsiHandler;

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

	private JTable table;
	private MsiHandler msiHandler;
	private MsiTableModel msiTableModel;
	
	private JPanel pane_3;
	private JScrollPane scrollPane_1;
	private JLabel MSI;
	private JLabel AIS;
	private Color leftButtonColor = Color.DARK_GRAY;
	private Color leftButtonColorClicked = new Color(45, 45, 45);
	private Color backgroundColor = new Color(83, 83, 83);
	private JTextPane area = new JTextPane();
	private StringBuilder doc = new StringBuilder();
	private JLabel but_read;
	private JLabel but_goto;
	private JLabel but_delete;
	private int selectedRow;
	private JLabel moveHandler;
	private JPanel masterPanel;
	private static int moveHandlerHeight = 18;
	private JPanel mapPanel;

	public NotificationCenter() {
		super("NOTCENTER", false, true, false, false);

		// Strip off window looks
		setRootPaneCheckingEnabled(false);
		((javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI()).setNorthPane(null);
		this.setBorder(null);

		// Map tools
		mapPanel = new JPanel(new GridLayout(1, 3));
		mapPanel.setPreferredSize(new Dimension(500, moveHandlerHeight));
		mapPanel.setOpaque(true);
		mapPanel.setBackground(Color.DARK_GRAY);
		mapPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(30, 30, 30)));

		// Placeholder - for now
		mapPanel.add(new JLabel());

		// Movehandler/Title dragable)
		moveHandler = new JLabel("Notification Center", JLabel.CENTER);
		moveHandler.setFont(new Font("Arial", Font.BOLD, 9));
		moveHandler.setForeground(new Color(200, 200, 200));
		// actions = moveHandler.getListeners(MouseMotionListener.class);
		mapPanel.add(moveHandler);

		// The tools (minimize, maximize and close)
		JPanel mapToolsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		mapToolsPanel.setOpaque(false);
		mapToolsPanel.setPreferredSize(new Dimension(60, 50));

		JLabel close = new JLabel(new ImageIcon("images/window/close.png"));
		close.addMouseListener(new MouseAdapter() {

			public void mouseReleased(MouseEvent e) {
				toggleVisibility();
			}

		});
		close.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 2));
		mapToolsPanel.add(close);
		mapPanel.add(mapToolsPanel);

		JPanel buttonPanel = new JPanel();

		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 600);
		buttonPanel.setPreferredSize(new Dimension(900, 600 - moveHandlerHeight));
		buttonPanel.setSize(new Dimension(900, 600 - moveHandlerHeight));
		buttonPanel.setBackground(backgroundColor);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 135, 365, 400 };
		gridBagLayout.rowHeights = new int[] { 100, 540 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0, 1.0 };
		gridBagLayout.rowWeights = new double[] { 1.0 };
		buttonPanel.setLayout(gridBagLayout);

		JPanel pane = new JPanel();
		pane.setLayout(new GridLayout(0, 1));
		pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		pane.setBackground(backgroundColor);
		GridBagConstraints gbc_pane = new GridBagConstraints();
		gbc_pane.fill = GridBagConstraints.BOTH;
		gbc_pane.gridx = 0;
		gbc_pane.gridy = 0;
		gbc_pane.gridheight = 2;
		buttonPanel.add(pane, gbc_pane);

		JPanel labelContainer = new JPanel();
		labelContainer.setLocation(0, 0);
		labelContainer.setSize(new Dimension(135, 600));
		labelContainer.setBackground(backgroundColor);
		pane.add(labelContainer);

		MSI = new JLabel("  MSI");
		MSI.setHorizontalAlignment(SwingConstants.LEFT);
		labelContainer.add(MSI);
		styleButton(MSI, leftButtonColor);

		AIS = new JLabel("  AIS");
		AIS.setHorizontalAlignment(SwingConstants.LEFT);
		labelContainer.add(AIS);
		styleButton(AIS, leftButtonColor);

		// Center
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBorder(new EmptyBorder(0, 0, 0, 0));
		GridBagConstraints gbc_scrollPane_2 = new GridBagConstraints();
		gbc_scrollPane_2.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_2.gridx = 1;
		gbc_scrollPane_2.gridy = 0;
		gbc_scrollPane_2.gridheight = 2;
		buttonPanel.add(scrollPane_2, gbc_scrollPane_2);
		String[] colHeadings = {"ID","Title"};
		DefaultTableModel model = new DefaultTableModel(35, colHeadings.length);
		model.setColumnIdentifiers(colHeadings);
		table = new JTable(model) {
			private static final long serialVersionUID = 1L;

			public Component prepareRenderer(TableCellRenderer renderer, int Index_row, int Index_col) {
				Component comp = super.prepareRenderer(renderer, Index_row, Index_col);
				if (Index_row % 2 == 0) {
					comp.setBackground(new Color(49, 49, 49));
				} else {
					comp.setBackground(new Color(65, 65, 65));
				}
				return comp;
			}

			public boolean isCellEditable(int rowIndex, int vColIndex) {
				return false;
			}
		};

		JTableHeader header = table.getTableHeader();
		header.setBackground(Color.BLACK);
		header.setForeground(Color.BLACK);
		header.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
		//header.setAutoscrolls(false);
		header.setEnabled(false);
		header.setPreferredSize(new Dimension(100,20));
		TableCellRenderer renderer = header.getDefaultRenderer();
		JLabel label = (JLabel) renderer;
		label.setHorizontalAlignment(JLabel.LEFT);
		JPanel buttonCorner = new JPanel();
		buttonCorner.setBackground(Color.BLACK);
		scrollPane_2.setCorner(JScrollPane.UPPER_RIGHT_CORNER, buttonCorner);
		
		table.setBorder(new EmptyBorder(0, 0, 0, 0));
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setBackground(new Color(49, 49, 49));
		table.setShowVerticalLines(false);
		table.setShowHorizontalLines(false);
		table.setShowGrid(false);
		table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
		table.setForeground(Color.white);
		table.setSelectionForeground(Color.gray);
		table.setRowSelectionAllowed(false);
		table.setRowHeight(20);
		table.setFocusable(false);
		int tablewidth = 345;
		int col1width = 50;
		int col2width = tablewidth - col1width;
		TableColumn col1 = table.getColumnModel().getColumn(0);
		col1.setPreferredWidth(col1width);
		TableColumn col2 = table.getColumnModel().getColumn(1);
		col2.setPreferredWidth(col2width);
		scrollPane_2.getViewport().setBackground(backgroundColor);
		scrollPane_2.setViewportView(table);

		// Right
		GridBagConstraints gbc_scrollPane_3 = new GridBagConstraints();
		gbc_scrollPane_3.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_3.gridx = 2;
		gbc_scrollPane_3.gridy = 0;
		pane_3 = new JPanel();
		pane_3.setBackground(backgroundColor);
		pane_3.setLayout(new GridLayout(0, 3));
		pane_3.setVisible(false);
		but_read = new JLabel("READ");
		but_read.setBorder(BorderFactory.createLineBorder(Color.black));
		but_goto = new JLabel("GOTO");
		but_goto.setBorder(BorderFactory.createLineBorder(Color.black));
		but_delete = new JLabel("DELETE");
		but_delete.setBorder(BorderFactory.createLineBorder(Color.black));
		pane_3.add(but_read);
		pane_3.add(but_goto);
		pane_3.add(but_delete);
		buttonPanel.add(pane_3, gbc_scrollPane_3);

		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 2;
		gbc_scrollPane_1.gridy = 1;
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setVisible(false);
		buttonPanel.add(scrollPane_1, gbc_scrollPane_1);

		area.setEditable(false);
		area.setContentType("text/html");
		area.setPreferredSize(new Dimension(100, 100));
		area.setLocation(0, 0);
		area.setLayout(null);
		area.setForeground(Color.white);
		area.setBackground(backgroundColor);

		scrollPane_1.setViewportView(area);

		addMouseListeners();

		masterPanel = new JPanel(new BorderLayout());
		masterPanel.add(mapPanel, BorderLayout.NORTH);
		masterPanel.add(buttonPanel, BorderLayout.SOUTH);
		masterPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, new Color(30, 30, 30), new Color(
				45, 45, 45)));
		this.getContentPane().add(masterPanel);
		this.setVisible(true);
	}

	public void setModel(TableModel model) {
		table.setModel(model);
	}

	public void styleButton(JLabel label, Color color) {
		label.setPreferredSize(new Dimension(125, 25));
		label.setFont(new Font("Arial", Font.PLAIN, 11));
		label.setForeground(new Color(237, 237, 237));
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		label.setBackground(color);
		label.setOpaque(true);
	}

	public void styleText(JLabel label) {
	}

	public void addMouseListeners() {

		AIS.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				AIS.setBackground(leftButtonColorClicked);
			}

			public void mouseReleased(MouseEvent e) {
				AIS.setBackground(leftButtonColor);
			}
		});

		MSI.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				MSI.setBackground(leftButtonColorClicked);
			}

			public void mouseReleased(MouseEvent e) {
				MSI.setBackground(leftButtonColor);
			}
			
			public void mouseClicked(MouseEvent e) {
				// Activate and update table
				table.setFocusable(true);
				msiTableModel = new MsiTableModel(msiHandler);
				table.setModel(msiTableModel);
				table.getSelectionModel().addListSelectionListener(new MSIRowListener());
			}
		});
		
		but_goto.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {				
				mainFrame.getMapWindows().get(0).getChartPanel().zoomToPoint(msiTableModel.getMessageLatLon(selectedRow));
			}
		});
	}

	private class MSIRowListener implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent event) {
			if (event.getValueIsAdjusting()) {
				return;
			}
								
			DefaultListSelectionModel values = (DefaultListSelectionModel) event.getSource();
			
			// Show buttons and area in right pane
			pane_3.setVisible(true);
			scrollPane_1.setVisible(true);
			selectedRow = values.getAnchorSelectionIndex();
			// Update area
			doc.delete(0, doc.length());
			for (int i = 0; i < table.getModel().getColumnCount(); i++) {
				doc.append("<b>" + table.getModel().getColumnName(i) + ":</b> "
						+ table.getModel().getValueAt(values.getAnchorSelectionIndex(), i) + "<br /><br />");
			}
			area.setText(doc.toString());
		}
	}


	@Override
	public void findAndInit(Object obj) {
		if (obj instanceof MsiHandler) {
			msiHandler = (MsiHandler) obj;
			msiHandler.addListener(this);
		}
		if (obj instanceof MainFrame) {
			System.out.println("jajJ");
			mainFrame = (MainFrame) obj;

			ToolbarMoveMouseListener mml = new ToolbarMoveMouseListener(this, mainFrame);
			mapPanel.addMouseListener(mml);
			mapPanel.addMouseMotionListener(mml);
		}
	}

	@Override
	public void msiUpdate() {
		// TODO Auto-generated method stub

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

}
