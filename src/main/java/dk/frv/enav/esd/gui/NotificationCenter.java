package dk.frv.enav.esd.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
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
import javax.swing.table.TableModel;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import dk.frv.enav.esd.gui.msi.MsiTableModel;
import dk.frv.enav.esd.msi.IMsiUpdateListener;
import dk.frv.enav.esd.msi.MsiHandler;

import javax.swing.JTable;


public class NotificationCenter extends ComponentFrame implements ListSelectionListener, ActionListener, IMsiUpdateListener {

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
	private JTable table;
	private MsiHandler msiHandler;
	
	private JTextPane area = new JTextPane();
	private StringBuilder doc = new StringBuilder();
	private JScrollPane jScrollPane = new JScrollPane();
	
	private MsiTableModel msiTableModel;

	/**
	 * Create the frame.
	 */
	public NotificationCenter() {
		setResizable(false);
		setTitle("Notification Center");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 653, 458);
		backgroundPane = new JPanel();
		backgroundPane.setBorder(new LineBorder(new Color(0, 0, 0)));
		backgroundPane.setBackground(new Color(83, 83, 83));
		
		setContentPane(backgroundPane);
		backgroundPane.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("653px:grow"),},
			new RowSpec[] {
				FormFactory.NARROW_LINE_GAP_ROWSPEC,
				RowSpec.decode("30px:grow"),
				RowSpec.decode("428px:grow"),}));
		
		JPanel topPanel = new JPanel();
		topPanel.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(70, 70, 70)));
		backgroundPane.add(topPanel, "1, 2, fill, fill");
		topPanel.setLayout(null);
		topPanel.setBackground(new Color(83, 83, 83));
		
		lblRead = new JLabel("READ");
		lblRead.setHorizontalAlignment(SwingConstants.CENTER);
		styleButton(lblRead,topButtonColor);
		lblRead.setBounds(7, 0, 125, 25);
		topPanel.add(lblRead);
		
		lblDelete = new JLabel("DELETE");
		lblDelete.setHorizontalAlignment(SwingConstants.CENTER);
		styleButton(lblDelete,topButtonColor);
		lblDelete.setBounds(145, 0, 125, 25);
		topPanel.add(lblDelete);
		
		lblGoto = new JLabel("GOTO");
		lblGoto.setHorizontalAlignment(SwingConstants.CENTER);
		styleButton(lblGoto,topButtonColor);
		lblGoto.setBounds(283, 0, 125, 25);
		topPanel.add(lblGoto);
		
		lblClose = new JLabel("CLOSE");
		lblClose.setHorizontalAlignment(SwingConstants.CENTER);
		styleButton(lblClose,topButtonColor);
		lblClose.setBounds(428, 0, 125, 25);
		topPanel.add(lblClose);
		
		JPanel bottomPanel = new JPanel();
		backgroundPane.add(bottomPanel, "1, 3, fill, fill");
		bottomPanel.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 0, 140, 428);
		scrollPane.setBorder(null);
		bottomPanel.add(scrollPane);

		
		
		JPanel menuPanel = new JPanel();
		scrollPane.setViewportView(menuPanel);
		menuPanel.setBackground(new Color(83, 83, 83));
		menuPanel.setLayout(null);

		JPanel labelContainer = new JPanel();
		labelContainer.setLocation(0, 0);
		labelContainer.setBackground(new Color(83, 83, 83));
		labelContainer.setSize(new Dimension(140, 417));
		
		menuPanel.add(labelContainer);
		
		String padding = "   ";
		
		MSI = new JLabel(padding+"MSI");
		MSI.setHorizontalAlignment(SwingConstants.LEFT);
		labelContainer.add(MSI);
		styleButton(MSI,leftButtonColor);
		labelContainer.add(MSI);
		
		AIS = new JLabel(padding+"AIS");
		AIS.setHorizontalAlignment(SwingConstants.LEFT);
		labelContainer.add(AIS);
		styleButton(AIS,leftButtonColor);
		
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new MatteBorder(0, 1, 0, 0, (Color) new Color(70, 70, 70)));
		contentPane.setBounds(140, 0, 513, 428);
		bottomPanel.add(contentPane);
		contentPane.setBackground(new Color(83, 83, 83));
		contentPane.setLayout(null);
		
		JPanel messageContent = new JPanel();
		messageContent.setBounds(10, 140, 470, 230);
		messageContent.setBackground(Color.LIGHT_GRAY);
		messageContent.setLayout(null);
		contentPane.add(messageContent);		
		
		table = new JTable();
		table.setBounds(10,10,470,120);
		table.setBackground(Color.GRAY);
		table.setShowVerticalLines(false);
		table.setShowGrid(false);
		
		contentPane.add(table);
		
		addMouseListeners();
		
	}
	
	public void setModel(TableModel model){
		table.setModel(model);
	}
	
	
	public void styleButton(JLabel label, Color color){
		label.setPreferredSize(new Dimension(125, 25));
		label.setFont(new Font("Arial", Font.PLAIN, 11));
		label.setForeground(new Color(237, 237, 237));
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		label.setBackground(color);
		label.setOpaque(true);
	}
	
	public void styleText(JLabel label){
	}
	
	public void addMouseListeners(){
		
		
		AIS.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				AIS.setBackground(leftButtonColorClicked);
			}

			public void mouseReleased(MouseEvent e) {
				AIS.setBackground(leftButtonColor);
			}
		});
		
		/**
		 * Add control for MSI notifications
		 * */
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
			}
		});
		table.getSelectionModel().addListSelectionListener(new MSIRowListener());
		
		lblRead.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				lblRead.setBackground(topButtonColorClicked);
			}

			public void mouseReleased(MouseEvent e) {
				lblRead.setBackground(topButtonColor);
			}
		});
		
		lblDelete.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				lblDelete.setBackground(topButtonColorClicked);
			}

			public void mouseReleased(MouseEvent e) {
				lblDelete.setBackground(topButtonColor);
			}
		});
		
		lblGoto.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				lblGoto.setBackground(topButtonColorClicked);
			}

			public void mouseReleased(MouseEvent e) {
				lblGoto.setBackground(topButtonColor);
			}
		});
		
		lblClose.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				lblClose.setBackground(topButtonColorClicked);
			}

			public void mouseReleased(MouseEvent e) {
				lblClose.setBackground(topButtonColor);
			}
			
			public void mouseClicked(MouseEvent e){
				toggleVisibility();
			}
		});
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
