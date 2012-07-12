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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;

import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;
import com.bbn.openmap.omGraphics.OMText;
import com.bbn.openmap.proj.Length;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.frv.ais.message.AisMessage;
import dk.frv.enav.ins.ais.AisIntendedRoute;
import dk.frv.enav.ins.ais.VesselStaticData;
import dk.frv.enav.ins.ais.VesselTarget;
import dk.frv.enav.ins.common.text.Formatter;
import dk.frv.enav.esd.layers.ais.IntendedRouteGraphic;

/**
 * Vessel class that maintains all the components in a vessel
 * 
 * @author Claes N. Ladefoged, claesnl@gmail.com
 * 
 */
public class Vessel extends OMGraphicList {
	private static final long serialVersionUID = 1L;
	private VesselLayer vessel;
	private OMCircle vesCirc;
	private HeadingLayer heading;
	private String vesselHeading = "N/A";
	private Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 11);;
	private OMText callSign = null;
	private String vesselCallSign = "N/A";
	private OMText nameMMSI = null;
	private String vesselName = "N/A";
	private long MMSI;
	private double lat;
	private double lon;
	private double sog;
	private double cogR;
	private double trueHeading;
	private OMLine speedVector;
	private LatLonPoint startPos = null;
	private LatLonPoint endPos = null;
	public static final float STROKE_WIDTH = 1.5f;
	private Color shipColor = new Color(78, 78, 78);
	private String vesselDest = "N/A";
	private String vesselEta = "N/A";
	private String vesselShiptype = "N/A";
	private IntendedRouteGraphic routeGraphic = new IntendedRouteGraphic();

	/**
	 * Vessel initialization with icon, circle, heading, speedvector, callsign
	 * and name/mmsi.
	 * 
	 * @param MMSI
	 *            Key of vessel
	 * @param staticImages 
	 */
	public Vessel(long MMSI) {
		super();
		this.MMSI = MMSI;

		// Vessel layer
		vessel = new VesselLayer(MMSI);

		// Vessel circle layer
		vesCirc = new OMCircle(0, 0, 0.01);
		vesCirc.setFillPaint(shipColor);

		// Heading layer
		heading = new HeadingLayer(MMSI, new int[] { 0, 0 }, new int[] { 0, -30 });
		heading.setFillPaint(new Color(0, 0, 0));

		// Speed vector layer
		speedVector = new OMLine(0, 0, 0, 0, OMLine.LINETYPE_STRAIGHT);
		speedVector.setStroke(new BasicStroke(STROKE_WIDTH, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f,
				new float[] { 10.0f, 8.0f }, 0.0f));
		speedVector.setLinePaint(new Color(255, 0, 0));

		// Call sign layer
		callSign = new OMText(0, 0, 0, 0, "", font, OMText.JUSTIFY_CENTER);

		// MSI / Name layer
		nameMMSI = new OMText(0, 0, 0, 0, Long.toString(MMSI), font, OMText.JUSTIFY_CENTER);

		this.add(vessel);
		this.add(vesCirc);
		this.add(heading);
		this.add(speedVector);
		this.add(callSign);
		this.add(nameMMSI);
		
		this.add(routeGraphic);
	}

	/**
	 * Updates all the vessel layers with position, data and heading where
	 * needed. Shows them on the map depending on mapScale.
	 * 
	 * @param trueHeading
	 *            Direction of vessel icon
	 * @param lat
	 *            Latitude position of vessel
	 * @param lon
	 *            Longitude position of vessel
	 * @param staticData
	 *            Static information of vessel
	 * @param sog
	 *            Speed over ground
	 * @param cogR
	 *            Course over ground in radians
	 * @param mapScale
	 *            Scale of the chartMap
	 * @param vesselTarget 
	 */
	public void updateLayers(double trueHeading, double lat, double lon, VesselStaticData staticData, double sog,
			double cogR, float mapScale, VesselTarget vesselTarget) {

		vessel.setLocation(lat, lon);
		vessel.setHeading(trueHeading);

		heading.setLocation(lat, lon, OMGraphic.DECIMAL_DEGREES, Math.toRadians(trueHeading));
		vesselHeading = Integer.toString((int) Math.round(trueHeading)) + "°";

		vesselName = "ID:" + this.MMSI;
		if (staticData != null) {
			vessel.setImageIcon(staticData.getShipType().toString());
			callSign.setData("Call Sign: " + staticData.getCallsign());
			vesselCallSign = staticData.getCallsign();
			vesselName = AisMessage.trimText(staticData.getName());
			vesselDest = staticData.getDestination();
			vesselEta = Long.toString(staticData.getEta());
			vesselShiptype = staticData.getShipType().toString();

		}
		nameMMSI.setData(vesselName);

		if (this.lat != lat || this.lon != lon || this.sog != sog || this.cogR != cogR
				|| this.trueHeading != trueHeading) {
			this.lat = lat;
			this.lon = lon;
			this.sog = sog;
			this.cogR = cogR;
			this.trueHeading = trueHeading;

			vesCirc.setLatLon(lat, lon);

			callSign.setLat(lat);
			callSign.setLon(lon);
			if (trueHeading > 90 && trueHeading < 270) {
				callSign.setY(-25);
			} else {
				callSign.setY(35);
			}

			double[] speedLL = new double[4];
			speedLL[0] = (float) lat;
			speedLL[1] = (float) lon;
			startPos = new LatLonPoint.Double(lat, lon);
			float length = (float) Length.NM.toRadians(6.0 * (sog / 60.0));
			endPos = startPos.getPoint(length, cogR);
			speedLL[2] = endPos.getLatitude();
			speedLL[3] = endPos.getLongitude();
			speedVector.setLL(speedLL);

			nameMMSI.setLat(lat);
			nameMMSI.setLon(lon);
			if (trueHeading > 90 && trueHeading < 270) {
				nameMMSI.setY(-10);
			} else {
				nameMMSI.setY(20);
			}
			
			
			AisIntendedRoute aisIntendedRoute = vesselTarget.getAisRouteData();
			routeGraphic.update(vesselName, aisIntendedRoute, vesselTarget.getPositionData().getPos(), vesselTarget);

		}

		// Scale for text-labels
		boolean b1 = mapScale < 750000;
		showHeading(b1);
		showSpeedVector(b1);
		showCallSign(b1);
		showName(b1);
		// Scale for ship icons
		boolean b2 = mapScale < 1500000;
		showVesselIcon(b2);
		showVesselCirc(!b2);
	}

	/**
	 * Toggle visibility of vessel icon on map
	 * 
	 * @param b
	 *            Boolean that tells if layer should be shown or not
	 */
	public void showVesselIcon(boolean b) {
		vessel.setVisible(b);
	}

	/**
	 * Toggle visibility of vessel circle on map
	 * 
	 * @param b
	 *            Boolean that tells if layer should be shown or not
	 */
	public void showVesselCirc(boolean b) {
		vesCirc.setVisible(b);
	}

	/**
	 * Toggle visibility of heading vector on map
	 * 
	 * @param b
	 *            Boolean that tells if layer should be shown or not
	 */
	public void showHeading(boolean b) {
		heading.setVisible(b);
	}

	/**
	 * Toggle visibility of speed vector on map
	 * 
	 * @param b
	 *            Boolean that tells if layer should be shown or not
	 */
	public void showSpeedVector(boolean b) {
		speedVector.setVisible(b);
	}

	/**
	 * Toggle visibility of call sign label on map
	 * 
	 * @param b
	 *            Boolean that tells if layer should be shown or not
	 */
	public void showCallSign(boolean b) {
		callSign.setVisible(b);
	}

	/**
	 * Toggle visibility of name label on map
	 * 
	 * @param b
	 *            Boolean that tells if layer should be shown or not
	 */
	public void showName(boolean b) {
		nameMMSI.setVisible(b);
	}

	public long getMMSI() {
		return this.MMSI;
	}

	public String getName() {
		if (vesselName != null && vesselName.startsWith("ID"))
			return "N/A";
		else
			return vesselName;
	}

	public String getHeading() {
		return vesselHeading;
	}

	public String getCallSign() {
		return vesselCallSign;
	}

	public String getLat() {
		return Formatter.latToPrintable(lat);
	}

	public String getLon() {
		return Formatter.lonToPrintable(lon);
	}

	public String getSog() {
		return Integer.toString((int) Math.round(sog)) + " kn";
	}

	public String getEta() {
		return vesselEta;
	}

	public String getDest() {
		return vesselDest;
	}

	public String getShipType() {
		return vesselShiptype;
	}

}
