/*
 * Copyright 2012 Danish Maritime Authority. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 *   2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Authority ``AS IS'' 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of Danish Maritime Authority.
 * 
 */
package dk.frv.enav.esd.gui.route;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import dk.frv.enav.esd.service.ais.AisServices;
import dk.frv.enav.esd.service.ais.RouteSuggestionData;
import dk.frv.enav.ins.common.text.Formatter;

/**
 * Table model for Route Exchange Notifications
 */
public class RouteExchangeTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	private static final String[] AREA_COLUMN_NAMES = { "ID", "MMSI", "Route", "Date", "Status"};
	private static final String[] COLUMN_NAMES = { "ID", "MMSI", "Route", "Status" };
	
	private AisServices aisService;
	
	private List<RouteSuggestionData> messages = new ArrayList<RouteSuggestionData>();


	/**
	 * Constructor for creating the msi table model
	 * @param msiHandler
	 */
	public RouteExchangeTableModel(AisServices aisService) {
		super();
		this.aisService = aisService;
		
		updateMessages();
	}
	
	/**
	 * Get column class at specific index
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		Object value = getValueAt(0, columnIndex);
		if (value == null) {
			return String.class;
		}
		return value.getClass();
	}

	/**
	 * Get the column count
	 */
	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}
	
	public int areaGetColumnCount() {
		return AREA_COLUMN_NAMES.length;
	}

	/**
	 * Return the column names
	 */
	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}
	
	public String areaGetColumnName(int column) {
		return AREA_COLUMN_NAMES[column];
	}

	/**
	 * Return messages
	 * 
	 * @return
	 */
	public List<RouteSuggestionData> getMessages() {
		return messages;
		 
	}

	/**
	 * Get the row count
	 */
	@Override
	public int getRowCount() {
		return messages.size();
	}

	/**
	 * Get the value at a specific row and colum index
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		if(rowIndex == -1) return "";
		RouteSuggestionData message = messages.get(rowIndex);
		
		switch (columnIndex) {
		case 0:
			return message.getId();
		case 1:
			return message.getMmsi();
		case 2:
			return message.getRoute().getName();
		case 3:
			return message.getStatus();
		default:
			return "";

		}
	}
	
	public Object areaGetValueAt(int rowIndex, int columnIndex) {
		if(rowIndex == -1) return "";
		RouteSuggestionData message = messages.get(rowIndex);
		
		switch (columnIndex) {
		case 0:
			return message.getId();
		case 1:
			return message.getMmsi();
		case 2:
			return message.getRoute().getName();
		case 3:
			return Formatter.formatShortDateTime(message.getTimeSent());
		case 4:
			return message.getStatus();
		default:
			return "";
		}
	}

	/**
	 * Update messages
	 */
	public void updateMessages() {
		
		messages.clear();
		
		   for (Iterator<RouteSuggestionData> it = aisService.getRouteSuggestions().values().iterator(); it.hasNext();) {
			   messages.add(it.next());
		   }
	}

}
