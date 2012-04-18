package dk.frv.enav.esd.gui;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;

public class NotificationCenter extends JInternalFrame{
	private static final long serialVersionUID = 1L;
	private JTextPane area = new JTextPane();
	private StringBuilder doc = new StringBuilder();
	private MenuTable menu; 
	private MSITableDummy rt;
	
	public NotificationCenter(){
		super("Notification Center", true, true, true, true);
		setSize(800, 600);
		setLocation(600, 200);
		setVisible(false);
		
		// Add main container
		JPanel mainContainer = new JPanel();
		mainContainer.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		add(mainContainer);		
		
		// Add left container / menu
		menu = new MenuTable();
		JTable leftTable = new JTable(menu);
		leftTable.setShowVerticalLines(false);
		leftTable.setGridColor(new Color(224,224,224));
		leftTable.getSelectionModel().addListSelectionListener(new RowListener1());
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.2;
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		mainContainer.add(new JScrollPane(leftTable),c);
				
		// Add right container
		JPanel rightContainer = new JPanel();
		rightContainer.setLayout(new GridBagLayout());
		c.gridx = 1;
		c.weightx = 0.8;
		mainContainer.add(new JScrollPane(rightContainer),c);

		// Test
		GridBagConstraints c2 = new GridBagConstraints();
		rt = new MSITableDummy();
		JTable rightTable = new JTable(rt);
		rightTable.getSelectionModel().addListSelectionListener(new RowListener2());
		rightTable.setShowVerticalLines(false);
		rightTable.setGridColor(new Color(224,224,224));
		c2.gridx = 0;
		c2.gridy = 0;
		c2.weightx = 1;
		c2.fill = GridBagConstraints.BOTH;
		c2.weighty = 0.3;
		JScrollPane pane = new JScrollPane(rightTable,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pane.setPreferredSize(new Dimension(500,100));
		rightContainer.add(pane,c2);
		c2.weighty = 0.7;
		c2.gridy = 1;
		area.setEditable(false);
		area.setContentType("text/html");
		area.setPreferredSize(new Dimension(530,400));
		rightContainer.add(new JScrollPane(area,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),c2);		
		doc.append("<table><tr><td><b>KOMÆLK</b></td><td style=\"color:red;\">FRA ARLA</td></tr></table>");
		area.setText(doc.toString());
		
	}
	
	public void toggleVisibility(){
		if(this.isVisible())
			this.setVisible(false);
		else
			this.setVisible(true);
	}
	
	private class RowListener1 implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {     	
            if (event.getValueIsAdjusting()) {
                return;
            }
            DefaultListSelectionModel values = (DefaultListSelectionModel) event.getSource();
            doc.delete(0,doc.length());
            doc.append(menu.getValueAt(values.getAnchorSelectionIndex(),0));
            area.setText(doc.toString());
        }
    }
	
	private class RowListener2 implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {     	
            if (event.getValueIsAdjusting()) {
                return;
            }
            DefaultListSelectionModel values = (DefaultListSelectionModel) event.getSource();
            doc.delete(0,doc.length());
            doc.append("<font style=\"color:red;\">"+rt.getValueAt(values.getAnchorSelectionIndex(),5)+"</font>");
            area.setText(doc.toString());
        }
    }
	
	class MenuTable extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		private String[] columnNames = {"Service name","Unread Messages"};
        private Object[][] data = {
		    {"MSI", new Integer(10)},
		    {"Guard Zones", new Integer(2)},
		    {"Skype", new Integer(2)},
		    {"Risk Index", new Integer(0)},
		    {"MSN", new Integer(1)}
        };

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }
    }	
}
