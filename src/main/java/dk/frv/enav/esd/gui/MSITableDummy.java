package dk.frv.enav.esd.gui;

import javax.swing.table.AbstractTableModel;

public class MSITableDummy extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private String[] columnNames = 
	{
			"ID",
			"Ver",
			"Priority",
			"Updated",
			"Main Area",
			"Message",
			"Valid From",
			"Valid Until"
	};
	private Object[][] data = {
    		{new Integer(182), new Integer(3), "ROUTINE", "11/10 10:10:57(1)","Great Belt","Light house exploded","11/10 00:00:00(1)","N/A"},
    		{new Integer(183), new Integer(3), "ROUTINE", "11/10 10:10:57(1)","Great Belt","Light house exploded","11/10 00:00:00(1)","N/A"},
    		{new Integer(184), new Integer(3), "ROUTINE", "11/10 10:10:57(1)","Great Belt","Light house exploded","11/10 00:00:00(1)","N/A"},
    		{new Integer(185), new Integer(3), "ROUTINE", "11/10 10:10:57(1)","Great Belt","Light house exploded","11/10 00:00:00(1)","N/A"},
    		{new Integer(186), new Integer(3), "ROUTINE", "11/10 10:10:57(1)","Great Belt","Light house exploded","11/10 00:00:00(1)","N/A"},
    		{new Integer(187), new Integer(3), "ROUTINE", "11/10 10:10:57(1)","Great Belt","Light house exploded","11/10 00:00:00(1)","N/A"},
    		{new Integer(188), new Integer(3), "ROUTINE", "11/10 10:10:57(1)","Great Belt","Light house exploded","11/10 00:00:00(1)","N/A"},
    		{new Integer(189), new Integer(3), "ROUTINE", "11/10 10:10:57(1)","Great Belt","Light house exploded","11/10 00:00:00(1)","N/A"},
    		{new Integer(190), new Integer(3), "ROUTINE", "11/10 10:10:57(1)","Great Belt","Light house exploded","11/10 00:00:00(1)","N/A"},
    		{new Integer(191), new Integer(3), "ROUTINE", "11/10 10:10:57(1)","Great Belt","Light house exploded","11/10 00:00:00(1)","N/A"},
    		{new Integer(192), new Integer(3), "ROUTINE", "11/10 10:10:57(1)","Great Belt","Light house exploded","11/10 00:00:00(1)","N/A"},
    		{new Integer(193), new Integer(3), "ROUTINE", "11/10 10:10:57(1)","Great Belt","Light house exploded","11/10 00:00:00(1)","N/A"},
    		{new Integer(194), new Integer(3), "ROUTINE", "11/10 10:10:57(1)","Great Belt","Light house exploded","11/10 00:00:00(1)","N/A"},
    		{new Integer(195), new Integer(3), "ROUTINE", "11/10 10:10:57(1)","Great Belt","Light house exploded","11/10 00:00:00(1)","N/A"},
    		{new Integer(196), new Integer(3), "ROUTINE", "11/10 10:10:57(1)","Great Belt","Light house exploded","11/10 00:00:00(1)","N/A"},
    		{new Integer(197), new Integer(3), "ROUTINE", "11/10 10:10:57(1)","Great Belt","Light house exploded","11/10 00:00:00(1)","N/A"},
    		{new Integer(198), new Integer(3), "ROUTINE", "11/10 10:10:57(1)","Great Belt","Light house exploded","11/10 00:00:00(1)","N/A"},
    		{new Integer(199), new Integer(3), "ROUTINE", "11/10 10:10:57(1)","Great Belt","Light house exploded","11/10 00:00:00(1)","N/A"},
    		{new Integer(200), new Integer(3), "ROUTINE", "11/10 10:10:57(1)","Great Belt","Light house exploded","11/10 00:00:00(1)","N/A"},
    		{new Integer(201), new Integer(3), "ROUTINE", "11/10 10:10:57(1)","Great Belt","Light house exploded","11/10 00:00:00(1)","N/A"},
    };
    
    public MSITableDummy(){
    	super();
    }

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