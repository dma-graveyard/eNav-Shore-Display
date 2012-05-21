package dk.frv.enav.esd.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import com.bbn.openmap.dataAccess.shape.DbfHandler.Rule;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import dk.frv.enav.esd.gui.msi.MsiTableModel;
import dk.frv.enav.esd.msi.IMsiUpdateListener;
import dk.frv.enav.esd.msi.MsiHandler;

import javax.swing.JTable;

public class NotificationCenter extends ComponentFrame implements ListSelectionListener, ActionListener,
		IMsiUpdateListener {

	private static final long serialVersionUID = 1L;

	private JPanel backgroundPane;

	Border paddingLeft = BorderFactory.createMatteBorder(0, 8, 0, 0, new Color(65, 65, 65));
	Border paddingBottom = BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(83, 83, 83));
	Border notificationPadding = BorderFactory.createCompoundBorder(paddingBottom, paddingLeft);
	Border notificationsIndicatorImportant = BorderFactory.createMatteBorder(0, 0, 0, 10, new Color(206, 120, 120));
	Border paddingLeftPressed = BorderFactory.createMatteBorder(0, 8, 0, 0, new Color(45, 45, 45));
	Border notificationPaddingPressed = BorderFactory.createCompoundBorder(paddingBottom, paddingLeftPressed);

	private JLabel MSI;
	private JLabel AIS;
	private JLabel lblRead;
	private JLabel lblDelete;
	private JLabel lblGoto;
	private JLabel lblClose;

	private Color topButtonColor = Color.BLACK;
	private Color topButtonColorClicked = new Color(45, 45, 45);
	private Color leftButtonColor = Color.DARK_GRAY;
	private Color leftButtonColorClicked = new Color(45, 45, 45);
	private Color backgroundColor = new Color(83, 83, 83);
	private JTable table;
	private MsiHandler msiHandler;

	private JTextPane area = new JTextPane();
	private StringBuilder doc = new StringBuilder();
	private JScrollPane jScrollPane = new JScrollPane();

	private MsiTableModel msiTableModel;

	private String padding = "   ";

	public NotificationCenter() {
		setResizable(false);
		setTitle("Notification Center");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 600);
		backgroundPane = new JPanel();
		backgroundPane.setBorder(new LineBorder(new Color(0, 0, 0)));
		backgroundPane.setBackground(new Color(83, 83, 83));

		setContentPane(backgroundPane);

		backgroundPane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		// Left pane
		JPanel leftPane = new JPanel();
		leftPane.setLayout(null);
		leftPane.setBackground(Color.blue);
		c.weightx = 0.155;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		backgroundPane.add(leftPane, c);

		// Center pane
		JScrollPane middleContainer = new JScrollPane();
		c.weightx = 0.4;
		c.gridx = 1;
		backgroundPane.add(middleContainer,c);
		/*JPanel centerPane = new JPanel();
		centerPane.setLayout(null);
		centerPane.setBackground(Color.pink);
		c.weightx = 0.4;
		c.gridx = 1;
		backgroundPane.add(centerPane, c);
		 */
		
		// Right pane
		JPanel rightPane = new JPanel();
		rightPane.setLayout(null);
		rightPane.setBackground(Color.red);
		c.weightx = 0.445;
		c.gridx = 2;
		backgroundPane.add(rightPane, c);

		// Left panel
		JPanel labelContainer = new JPanel();
		labelContainer.setLocation(0, 0);
		labelContainer.setSize(new Dimension(135, 600));
		labelContainer.setBackground(null);
		leftPane.add(labelContainer);

		MSI = new JLabel(padding + "MSI");
		MSI.setHorizontalAlignment(SwingConstants.LEFT);
		labelContainer.add(MSI);
		styleButton(MSI, leftButtonColor);

		AIS = new JLabel(padding + "AIS");
		AIS.setHorizontalAlignment(SwingConstants.LEFT);
		labelContainer.add(AIS);
		styleButton(AIS, leftButtonColor);

		addMouseListeners();

		// Middle panel

		String[] columnNames = { "ID", "Title" };
		Object[][] data = new Object[50][2];
		for (int i = 0; i < 50; i++) {
			data[i][0] = i;
			data[i][1] = "Lighthouse exploded for the " + i + "th time";
		}
		table = new JTable(data, columnNames) {
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
		};
		table.setBounds(0, 0, 365, 552);
		JTableHeader header = table.getTableHeader();
		header.setBackground(Color.yellow);
		table.setLayout(null);
		table.setBackground(new Color(49, 49, 49));
		table.setShowVerticalLines(false);
		table.setShowGrid(false);
		table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
		table.setForeground(Color.white);
		table.setEnabled(false);
		// table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setRowSelectionAllowed(true);
		table.setSelectionForeground(Color.gray);
		table.setSelectionBackground(Color.red);
		middleContainer.add(table);

		/*
		 * setResizable(false); setTitle("Notification Center");
		 * setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); setBounds(100, 100,
		 * 900, 600); backgroundPane = new JPanel();
		 * backgroundPane.setBorder(new LineBorder(new Color(0, 0, 0)));
		 * backgroundPane.setBackground(new Color(83, 83, 83));
		 * 
		 * setContentPane(backgroundPane); backgroundPane.setLayout(new
		 * FormLayout(new ColumnSpec[] { ColumnSpec.decode("135px:grow"),
		 * ColumnSpec.decode("365px:grow"), ColumnSpec.decode("400px:grow"), },
		 * new RowSpec[] { RowSpec .decode("600px:grow"), }));
		 * 
		 * // Main panels JPanel leftPanel = new JPanel();
		 * backgroundPane.add(leftPanel, "1, 1, fill, fill");
		 * leftPanel.setLayout(null); leftPanel.setBackground(backgroundColor);
		 * 
		 * JPanel middlePanel = new JPanel(); backgroundPane.add(middlePanel,
		 * "2, 1, fill, fill"); middlePanel.setLayout(null);
		 * middlePanel.setBackground(backgroundColor);
		 * 
		 * JPanel rightPanel = new JPanel(); backgroundPane.add(rightPanel,
		 * "3, 1, fill, fill"); rightPanel.setLayout(null);
		 * rightPanel.setBackground(backgroundColor);
		 * 
		 * // Left panel JPanel labelContainer = new JPanel();
		 * labelContainer.setLocation(0, 0); labelContainer.setSize(new
		 * Dimension(135, 600)); labelContainer.setBackground(null);
		 * leftPanel.add(labelContainer);
		 * 
		 * MSI = new JLabel(padding + "MSI");
		 * MSI.setHorizontalAlignment(SwingConstants.LEFT);
		 * labelContainer.add(MSI); styleButton(MSI, leftButtonColor);
		 * 
		 * AIS = new JLabel(padding + "AIS");
		 * AIS.setHorizontalAlignment(SwingConstants.LEFT);
		 * labelContainer.add(AIS); styleButton(AIS, leftButtonColor);
		 * 
		 * // Middle panel JScrollPane middleContainer = new
		 * JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		 * JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		 * middleContainer.setLocation(0, 0); middleContainer.setSize(new
		 * Dimension(365, 552)); middleContainer.setLayout(null);
		 * middleContainer.getVerticalScrollBar().setVisible(true);
		 * middlePanel.add(middleContainer);
		 * 
		 * String[] columnNames = { "ID", "Title" }; Object[][] data = new
		 * Object[50][2]; for (int i = 0; i < 50; i++) { data[i][0] = i;
		 * data[i][1] = "Lighthouse exploded for the " + i + "th time"; } table
		 * = new JTable(data, columnNames) { private static final long
		 * serialVersionUID = 1L;
		 * 
		 * public Component prepareRenderer(TableCellRenderer renderer, int
		 * Index_row, int Index_col) { Component comp =
		 * super.prepareRenderer(renderer, Index_row, Index_col); if (Index_row
		 * % 2 == 0) { comp.setBackground(new Color(49, 49, 49)); } else {
		 * comp.setBackground(new Color(65, 65, 65)); } return comp; } };
		 * table.setBounds(0, 0, 265, 552); JTableHeader header =
		 * table.getTableHeader(); header.setBackground(Color.yellow);
		 * table.setLayout(null); table.setBackground(new Color(49, 49, 49));
		 * table.setShowVerticalLines(false); table.setShowGrid(false);
		 * table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
		 * table.setForeground(Color.white); table.setEnabled(false);
		 * //table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		 * table.setRowSelectionAllowed( true ); table.setSelectionForeground(
		 * Color.gray ); table.setSelectionBackground( Color.red );
		 * 
		 * middleContainer.add(table);
		 * 
		 * // Right panel JScrollPane rightContainer = new
		 * JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		 * JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		 * rightContainer.setBounds(0, 0, 375, 552);
		 * rightContainer.setBackground(Color.BLACK);
		 * rightContainer.setLayout(null); rightPanel.add(rightContainer);
		 * 
		 * area.setEditable(false); area.setContentType("text/html");
		 * area.setPreferredSize(new Dimension(100, 100)); area.setLocation(0,
		 * 0); area.setVisible(true); area.setLayout(null);
		 * area.setForeground(Color.black); area.setBackground(Color.red);
		 * doc.append(
		 * "<table><tr><td><b>Notification</b></td><td style=\"color:red;\">Center</td></tr></table>"
		 * ); area.setText(doc.toString()); rightContainer.add(area);
		 * 
		 * addMouseListeners();
		 */
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
				msiTableModel = new MsiTableModel(msiHandler);
				setModel(msiTableModel);
				table.getSelectionModel().addListSelectionListener(new MSIRowListener());
			}
		});

		/*
		 * lblRead.addMouseListener(new MouseAdapter() { public void
		 * mousePressed(MouseEvent e) {
		 * lblRead.setBackground(topButtonColorClicked); }
		 * 
		 * public void mouseReleased(MouseEvent e) {
		 * lblRead.setBackground(topButtonColor); } });
		 * 
		 * lblDelete.addMouseListener(new MouseAdapter() { public void
		 * mousePressed(MouseEvent e) {
		 * lblDelete.setBackground(topButtonColorClicked); }
		 * 
		 * public void mouseReleased(MouseEvent e) {
		 * lblDelete.setBackground(topButtonColor); } });
		 * 
		 * lblGoto.addMouseListener(new MouseAdapter() { public void
		 * mousePressed(MouseEvent e) {
		 * lblGoto.setBackground(topButtonColorClicked); }
		 * 
		 * public void mouseReleased(MouseEvent e) {
		 * lblGoto.setBackground(topButtonColor); } });
		 * 
		 * lblClose.addMouseListener(new MouseAdapter() { public void
		 * mousePressed(MouseEvent e) {
		 * lblClose.setBackground(topButtonColorClicked); }
		 * 
		 * public void mouseReleased(MouseEvent e) {
		 * lblClose.setBackground(topButtonColor); }
		 * 
		 * public void mouseClicked(MouseEvent e){ toggleVisibility(); } });
		 */
	}

	private class MSIRowListener implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent event) {
			if (event.getValueIsAdjusting()) {
				return;
			}
			DefaultListSelectionModel values = (DefaultListSelectionModel) event.getSource();
			doc.delete(0, doc.length());
			for (int i = 0; i < msiTableModel.getColumnCount(); i++) {
				doc.append("<b>" + msiTableModel.getColumnName(i) + ":</b> "
						+ msiTableModel.getValueAt(values.getAnchorSelectionIndex(), i) + "<br /><br />");
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
