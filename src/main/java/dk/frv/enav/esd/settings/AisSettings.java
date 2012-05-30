/*
 * Copyright 2011 Danish Maritime Authority. All rights reserved.
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
package dk.frv.enav.esd.settings;

import java.io.Serializable;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.bbn.openmap.util.PropUtils;

/**
 * Sensor settings
 */
public class AisSettings implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG = Logger.getLogger(AisSettings.class);
	
	private static final String PREFIX = "ais.";
	
	public enum SensorConnectionType {
		NONE, TCP, SERIAL, FILE, AIS_SHARED;
		public static SensorConnectionType parseString(String type) {
			if (type.equalsIgnoreCase("TCP")) {
				return TCP;
			} else if (type.equalsIgnoreCase("SERIAL")) {
				return SERIAL;
			} else if (type.equalsIgnoreCase("FILE")) {
				return FILE;
			} else if (type.equalsIgnoreCase("AIS_SHARED")) {
				return AIS_SHARED;
			}
			return NONE;
		}
	}
	
	private boolean allowSending = true;
	private boolean strict = true; // Strict timeout rules
	private SensorConnectionType aisConnectionType = SensorConnectionType.TCP; 
	private String aisHostOrSerialPort = "localhost";
	private String aisFilename = "";
	private int aisTcpPort = 4001;
	private int sartPrefix = 970;
	

	/**
	 * If farther away than this range, the messages are discarded
	 * In nautical miles (theoretical distance is about 40 miles)
	*/
	private double aisSensorRange = 0;


	public AisSettings() {
		
	}
	
	public void readProperties(Properties props) {
		sartPrefix = PropUtils.intFromProperties(props, PREFIX + "sartPrefix", sartPrefix);
		allowSending = PropUtils.booleanFromProperties(props, PREFIX + "allowSending", allowSending);
		strict = PropUtils.booleanFromProperties(props, PREFIX + "strict", strict);
		aisConnectionType = SensorConnectionType.parseString(props.getProperty(PREFIX + "aisConnectionType", aisConnectionType.name()));
		aisHostOrSerialPort = props.getProperty(PREFIX + "aisHostOrSerialPort", aisHostOrSerialPort);
		aisTcpPort = PropUtils.intFromProperties(props, PREFIX + "aisTcpPort", aisTcpPort);
		aisSensorRange = PropUtils.doubleFromProperties(props, PREFIX + "aisSensorRange", aisSensorRange);
		aisFilename = props.getProperty(PREFIX + "aisFilename", aisFilename);
	}
	
	public void setProperties(Properties props) {
		props.put(PREFIX + "sartPrefix", Integer.toString(sartPrefix));
		props.put(PREFIX + "allowSending", Boolean.toString(allowSending));
		props.put(PREFIX + "strict", Boolean.toString(strict));
		props.put(PREFIX + "aisConnectionType", aisConnectionType.name());
		props.put(PREFIX + "aisHostOrSerialPort", aisHostOrSerialPort);
		props.put(PREFIX + "aisTcpPort", Integer.toString(aisTcpPort));
		props.put(PREFIX + "aisSensorRange", Double.toString(aisSensorRange));
		props.put(PREFIX + "aisFilename", aisFilename);
	}

	public SensorConnectionType getAisConnectionType() {
		return aisConnectionType;
	}

	public void setAisConnectionType(SensorConnectionType aisConnectionType) {
		this.aisConnectionType = aisConnectionType;
	}

	public String getAisHostOrSerialPort() {
		return aisHostOrSerialPort;
	}

	public void setAisHostOrSerialPort(String aisHostOrSerialPort) {
		this.aisHostOrSerialPort = aisHostOrSerialPort;
	}

	public int getAisTcpPort() {
		return aisTcpPort;
	}

	public void setAisTcpPort(int aisTcpPort) {
		this.aisTcpPort = aisTcpPort;
	}

	public String getSartPrefix() {
		return Integer.toString(sartPrefix);
	}
	
	public void setSartPrefix(String sartPrefix) {
		this.sartPrefix = new Integer(sartPrefix);
	}
	public boolean isStrict() {
		return strict;
	}
	
	public void setStrict(boolean strict) {
		this.strict = strict;
	}
	
	public double getAisSensorRange() {
		return aisSensorRange;
	}
	
	public void setAisSensorRange(double aisSensorRange) {
		this.aisSensorRange = aisSensorRange;
	}
	
	public String getAisFilename() {
		return aisFilename;
	}
	
	public void setAisFilename(String aisFilename) {
		this.aisFilename = aisFilename;
	}
	
	public boolean isAllowSending() {
		return allowSending;
	}

	public void setAllowSending(boolean allowSending) {
		this.allowSending = allowSending;
	}
	
}
