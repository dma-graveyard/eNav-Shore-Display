package dk.frv.enav.esd.gui;
import java.awt.EventQueue;

import javax.swing.JInternalFrame;
import javax.swing.JCheckBox;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import javax.swing.JTable;
import java.awt.GridBagConstraints;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.Insets;
import javax.swing.JSeparator;
import java.awt.Color;


public class NotificationCenterGUI extends JInternalFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable table;
	private JTable table_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NotificationCenterGUI frame = new NotificationCenterGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public NotificationCenterGUI() {
		getContentPane().setBackground(Color.DARK_GRAY);
		setBounds(100, 100, 450, 300);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{99, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		JButton btnAwk = new JButton("AWK");
		GridBagConstraints gbc_btnAwk = new GridBagConstraints();
		gbc_btnAwk.insets = new Insets(0, 0, 5, 5);
		gbc_btnAwk.gridx = 0;
		gbc_btnAwk.gridy = 0;
		getContentPane().add(btnAwk, gbc_btnAwk);
		
		JButton btnDel = new JButton("DEL");
		GridBagConstraints gbc_btnDel = new GridBagConstraints();
		gbc_btnDel.insets = new Insets(0, 0, 5, 5);
		gbc_btnDel.gridx = 1;
		gbc_btnDel.gridy = 0;
		getContentPane().add(btnDel, gbc_btnDel);
		
		table = new JTable();
		table.setBackground(Color.GRAY);
		GridBagConstraints gbc_table = new GridBagConstraints();
		gbc_table.gridwidth = 11;
		gbc_table.gridheight = 3;
		gbc_table.insets = new Insets(0, 0, 5, 0);
		gbc_table.fill = GridBagConstraints.BOTH;
		gbc_table.gridx = 2;
		gbc_table.gridy = 0;
		getContentPane().add(table, gbc_table);
		
		JButton btnGoto = new JButton("GOTO");
		GridBagConstraints gbc_btnGoto = new GridBagConstraints();
		gbc_btnGoto.insets = new Insets(0, 0, 5, 5);
		gbc_btnGoto.gridx = 0;
		gbc_btnGoto.gridy = 2;
		getContentPane().add(btnGoto, gbc_btnGoto);
		
		JButton btnRand = new JButton("RAND");
		GridBagConstraints gbc_btnRand = new GridBagConstraints();
		gbc_btnRand.insets = new Insets(0, 0, 5, 5);
		gbc_btnRand.gridx = 1;
		gbc_btnRand.gridy = 2;
		getContentPane().add(btnRand, gbc_btnRand);
		
		table_1 = new JTable();
		table_1.setBackground(Color.GRAY);
		GridBagConstraints gbc_table_1 = new GridBagConstraints();
		gbc_table_1.gridheight = 6;
		gbc_table_1.gridwidth = 2;
		gbc_table_1.insets = new Insets(0, 0, 0, 5);
		gbc_table_1.fill = GridBagConstraints.BOTH;
		gbc_table_1.gridx = 0;
		gbc_table_1.gridy = 3;
		getContentPane().add(table_1, gbc_table_1);
		
		JTextArea textArea = new JTextArea();
		textArea.setBackground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.gridwidth = 11;
		gbc_textArea.gridheight = 6;
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.gridx = 2;
		gbc_textArea.gridy = 3;
		getContentPane().add(textArea, gbc_textArea);

	}

}
