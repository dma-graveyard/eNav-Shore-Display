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
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Safety Administration ``AS IS'' 
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
package dk.frv.enav.esd.layers.ais;

import javax.swing.ImageIcon;

import dk.frv.enav.esd.ESD;
import dk.frv.enav.ins.common.graphics.CenterRaster;

/**
 * Vessel layer that creates a directed vessel with icon
 * 
 * @author Claes N. Ladefoged, claesnl@gmail.com
 * 
 */
public class VesselLayer extends CenterRaster {
	private static final long serialVersionUID = 1L;
	private long MMSI;
	private ImageIcon vesselIcon;
	private double lat;
	private double lon;
	private double trueHeading;
	private String shipType;

	/**
	 * Initialize a vessel with default icon
	 * @param MMSI Key of vessel
	 */
	public VesselLayer(long MMSI) {
		super(0, 0, 24, 24, new ImageIcon(ESD.class.getResource("/images/vesselIcons/white1_90.png")));
		this.MMSI = MMSI;
	}

	/**
	 * Rotates vessel icon
	 * @param trueHeading Direction of vessel icon
	 */
	public void setHeading(double trueHeading) {
		if (this.trueHeading != trueHeading) {
			this.trueHeading = trueHeading;
			this.setRotationAngle(Math.toRadians(trueHeading - 90));
		}
	}

	/**
	 * Moves the vessel icon
	 * @param lat Latitude position of vessel icon
	 * @param lon Longitude position of vessel icon
	 */
	public void setLocation(double lat, double lon) {
		if (this.lat != lat || this.lon != lon) {
			this.lat = lat;
			this.lon = lon;
			this.setLat(lat);
			this.setLon(lon);
		}
	}

	/**
	 * Changes vessel icon based on ship type
	 * @param shipType Ship type relative to "GUIDELINES FOR THE INSTALLATION OF A SHIPBORNE AUTOMATIC IDENTIFICATION SYSTEM (AIS)"
	 */
	public void setImageIcon(String shipType) {
		if(this.shipType != shipType) {	
			this.shipType = shipType;
			if (shipType.startsWith("Passenger"))
				vesselIcon = new ImageIcon(ESD.class.getResource("/images/vesselIcons/blue1_90.png"));
			else if (shipType.startsWith("Cargo"))
				vesselIcon = new ImageIcon(ESD.class.getResource("/images/vesselIcons/lightgreen1_90.png"));
			else if (shipType.startsWith("Tug"))
				vesselIcon = new ImageIcon(ESD.class.getResource("/images/vesselIcons/cyan1_90.png"));
			else if (shipType.startsWith("Tanker"))
				vesselIcon = new ImageIcon(ESD.class.getResource("/images/vesselIcons/red1_90.png"));
			else if (shipType.startsWith("Port"))
				vesselIcon = new ImageIcon(ESD.class.getResource("/images/vesselIcons/cyan1_90.png"));
			else if (shipType.startsWith("Dredging"))
				vesselIcon = new ImageIcon(ESD.class.getResource("/images/vesselIcons/white0.png"));
			else if (shipType.startsWith("Sailing"))
				vesselIcon = new ImageIcon(ESD.class.getResource("/images/vesselIcons/brown1_90.png"));
			else if (shipType.startsWith("Pleasure"))
				vesselIcon = new ImageIcon(ESD.class.getResource("/images/vesselIcons/magenta1_90.png"));
			else if (shipType.startsWith("Sar"))
				vesselIcon = new ImageIcon(ESD.class.getResource("/images/vesselIcons/cyan1_90.png"));
			else if (shipType.startsWith("Fishing"))
				vesselIcon = new ImageIcon(ESD.class.getResource("/images/vesselIcons/brown1_90.png"));
			else if (shipType.startsWith("Diving"))
				vesselIcon = new ImageIcon(ESD.class.getResource("/images/vesselIcons/cyan1_90.png"));
			else if (shipType.startsWith("Pilot"))
				vesselIcon = new ImageIcon(ESD.class.getResource("/images/vesselIcons/cyan1_90.png"));
			else if (shipType.startsWith("Undefined"))
				vesselIcon = new ImageIcon(ESD.class.getResource("/images/vesselIcons/lightgray1_90.png"));
			else if (shipType.startsWith("Unknown"))
				vesselIcon = new ImageIcon(ESD.class.getResource("/images/vesselIcons/lightgray1_90.png"));
			else {
				vesselIcon = new ImageIcon(ESD.class.getResource("/images/vesselIcons/lightgray1_90.png"));
			}
			this.setImageIcon(vesselIcon);
		}
	}

	/**
	 * Get the MMSI attached to the layer
	 * @return MMSI Key of vessel
	 */
	public long getMMSI() {
		return this.MMSI;
	}
}
