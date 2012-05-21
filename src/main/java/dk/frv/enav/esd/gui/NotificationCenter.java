package dk.frv.enav.esd.gui;

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

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

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

	private JTable table;
	private MsiHandler msiHandler;
	
	private JPanel pane_3;
	private JScrollPane scrollPane_1;
	private JLabel MSI;
	private JLabel AIS;
	private Color leftButtonColor = Color.DARK_GRAY;
	private Color leftButtonColorClicked = new Color(45, 45, 45);
	private Color backgroundColor = new Color(83, 83, 83);
	private JTextPane area = new JTextPane();
	private StringBuilder doc = new StringBuilder();

	public NotificationCenter() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 600);
		getContentPane().setBackground(backgroundColor);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 135, 365, 400 };
		gridBagLayout.rowHeights = new int[] { 100, 540 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0, 1.0 };
		gridBagLayout.rowWeights = new double[] { 1.0 };
		getContentPane().setLayout(gridBagLayout);
		
		JPanel pane = new JPanel();
		pane.setLayout(new GridLayout(0, 1));
		pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		pane.setBackground(backgroundColor);
		GridBagConstraints gbc_pane = new GridBagConstraints();
		gbc_pane.fill = GridBagConstraints.BOTH;
		gbc_pane.gridx = 0;
		gbc_pane.gridy = 0;
		gbc_pane.gridheight = 2;
		getContentPane().add(pane, gbc_pane);

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
		getContentPane().add(scrollPane_2, gbc_scrollPane_2);

		String[] columnNames = { "ID", "Title" };
		Object[][] data = new Object[50][2];
		for (int i = 0; i < 50; i++) {
			data[i][0] = i;
			data[i][1] = "Lighthouse exploded for the " + i + "th time";
		}
		table = new JTable(data, columnNames) {
			private static final long serialVersionUID = 1L;

			public Component prepareRenderer(TableCellRenderer renderer,
					int Index_row, int Index_col) {
				Component comp = super.prepareRenderer(renderer, Index_row,
						Index_col);
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
		TableCellRenderer renderer = header.getDefaultRenderer();
		JLabel label = (JLabel)renderer;
		label.setHorizontalAlignment(JLabel.LEFT);
		label.setBackground(Color.black);
		JPanel buttonCorner = new JPanel();
		buttonCorner.setBackground(Color.BLACK);
		scrollPane_2.setCorner(JScrollPane.UPPER_RIGHT_CORNER,buttonCorner);
		header.setBackground(Color.BLACK);
		header.setForeground(Color.red);
		header.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
		header.setAutoscrolls(false);
		header.setEnabled(false);
		table.setBorder(new EmptyBorder(0,0,0,0));
		table.setIntercellSpacing(new Dimension(0,0));
		table.setBackground(new Color(49, 49, 49));
		table.setShowVerticalLines(false);
		table.setShowHorizontalLines(false);
		table.setShowVerticalLines(false);
		table.setShowGrid(false);
		table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
		table.setForeground(Color.white);
		table.setSelectionForeground(Color.gray);
		table.setSelectionBackground(Color.red);
		table.setRowSelectionAllowed(true);
		int tablewidth = 345;
		int col1width = 50;
		int col2width = tablewidth - col1width;
		TableColumn col1 = table.getColumnModel().getColumn(0);
		col1.setPreferredWidth(col1width);
		TableColumn col2 = table.getColumnModel().getColumn(1);
		col2.setPreferredWidth(col2width);
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
		JLabel but = new JLabel("READ");
		but.setBorder(BorderFactory.createLineBorder(Color.black));
		JLabel but2 = new JLabel("GOTO");
		but2.setBorder(BorderFactory.createLineBorder(Color.black));
		JLabel but3 = new JLabel("DELETE");
		but3.setBorder(BorderFactory.createLineBorder(Color.black));
		pane_3.add(but);
		pane_3.add(but2);
		pane_3.add(but3);
		getContentPane().add(pane_3, gbc_scrollPane_3);
		
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 2;
		gbc_scrollPane_1.gridy = 1;
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setVisible(false);
		getContentPane().add(scrollPane_1, gbc_scrollPane_1);
		
		area.setEditable(false);
		area.setContentType("text/html");
		area.setPreferredSize(new Dimension(100, 100));
		area.setLocation(0, 0);
		area.setLayout(null);
		area.setForeground(Color.white);
		area.setBackground(backgroundColor);

		scrollPane_1.setViewportView(area);

		addMouseListeners();
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
		});
		
		table.getSelectionModel()
		.addListSelectionListener(new TableRowListener());
	}

	private class TableRowListener implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent event) {
			if (event.getValueIsAdjusting()) {
				return;
			}
			DefaultListSelectionModel values = (DefaultListSelectionModel) event
					.getSource();

			// Show buttons and area in right pane
			pane_3.setVisible(true);
			scrollPane_1.setVisible(true);
			
			// Update area
			doc.delete(0, doc.length());
			for (int i = 0; i < table.getModel().getColumnCount(); i++) {
				doc.append("<b>"
						+ table.getModel().getColumnName(i)
						+ ":</b> "
						+ table.getModel().getValueAt(
								values.getAnchorSelectionIndex(), i)
						+ "<br /><br />");
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
