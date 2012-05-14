package dk.frv.enav.esd.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;

public class JSettingsWindow extends ComponentFrame{
	
	private JPanel contentPane;

	public JSettingsWindow(){
		super("Settings Window", true, true, true, true);
		setSize(800, 600);
		setLocation(10, 10);
		setVisible(true);
		
		
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
		
		JPanel panel = new JPanel();
		contentPane.add(panel);
		
		JPanel breadcrumpPanel = new JPanel();
		breadcrumpPanel.setBounds(0, 0, 434, 32);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		tabbedPane.setBounds(0, 38, 424, 219);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		JPanel firstPanel = new JPanel();
		tabbedPane.addTab("New tab", null, firstPanel, null);
		firstPanel.setLayout(null);
		
		JLabel lblFirstPanel = new JLabel("First panel");
		lblFirstPanel.setBounds(10, 11, 198, 14);
		firstPanel.add(lblFirstPanel);
		
		JPanel secondPanel = new JPanel();
		tabbedPane.addTab("New tab", null, secondPanel, null);
		secondPanel.setLayout(null);
		
		JLabel lblSecondPanel = new JLabel("Second Panel");
		lblSecondPanel.setBounds(10, 11, 115, 14);
		secondPanel.add(lblSecondPanel);
		
		JPanel thirdPanel = new JPanel();
		tabbedPane.addTab("New tab", null, thirdPanel, null);
		thirdPanel.setLayout(null);
		
		JLabel lblThirdPanel = new JLabel("Third Panel");
		lblThirdPanel.setBounds(10, 11, 113, 14);
		thirdPanel.add(lblThirdPanel);
		
		
		panel.setLayout(null);
		panel.add(breadcrumpPanel);
		breadcrumpPanel.setLayout(new BoxLayout(breadcrumpPanel, BoxLayout.X_AXIS));
		
		JLabel lblBreadcrumpsGoesHere = new JLabel("Breadcrumps goes here dfgdgd");
		breadcrumpPanel.add(lblBreadcrumpsGoesHere);
		panel.add(tabbedPane);
	}
	
}
