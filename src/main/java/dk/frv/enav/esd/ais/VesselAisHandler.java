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
package dk.frv.enav.esd.ais;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import dk.frv.ais.binary.SixbitException;
import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.message.AisBinaryMessage;
import dk.frv.ais.message.AisMessage;
import dk.frv.ais.message.AisMessage18;
import dk.frv.ais.message.AisMessage21;
import dk.frv.ais.message.AisMessage5;
import dk.frv.ais.message.AisMessage6;
import dk.frv.ais.message.AisPositionMessage;
import dk.frv.ais.message.binary.AddressedRouteInformation;
import dk.frv.ais.message.binary.AisApplicationMessage;
import dk.frv.enav.esd.nmea.IVesselAisListener;
import dk.frv.enav.esd.nmea.NmeaSensor;
import dk.frv.enav.esd.settings.Settings;
import dk.frv.enav.esd.ESD;
import dk.frv.enav.esd.ais.AisAdressedRouteSuggestion;
import dk.frv.enav.esd.ais.IAisRouteSuggestionListener;
import dk.frv.enav.esd.ais.VesselPositionData;
import dk.frv.enav.esd.ais.VesselStaticData;
import dk.frv.enav.esd.ais.VesselTarget;
import dk.frv.enav.esd.common.util.Converter;
import dk.frv.enav.esd.gps.GpsData;
import dk.frv.enav.esd.gps.GpsHandler;
import dk.frv.enav.esd.nmea.SensorType;

/**
 * AisHandler for Vessels
 * @author David
 *
 */
public class VesselAisHandler extends AisHandler implements IVesselAisListener {
	
	private static final Logger LOG = Logger.getLogger(VesselAisHandler.class);

	protected static final double SIMULATED_AIS_RANGE = 20;		

	protected GpsHandler gpsHandler = null;
	protected double aisRange = 0;
	protected VesselTarget ownShip = new VesselTarget();
	protected NmeaSensor nmeaSensor = null;
	protected Settings settings;
	protected List<IAisRouteSuggestionListener> suggestionListeners = new ArrayList<IAisRouteSuggestionListener>();

	/**
	 * Constructor for VesselsAisHandler
	 * @param settings
	 */
	public VesselAisHandler(Settings settings) {
		super(settings.getAisSettings().isShowIntendedRouteByDefault(), settings.getAisSettings().isStrict());
		this.settings = settings;
		if (settings.getSensorSettings().isSimulateGps() && settings.getSensorSettings().getAisSensorRange() == 0) {
			aisRange = SIMULATED_AIS_RANGE;
			ownShip.setMmsi(settings.getSensorSettings().getSimulatedOwnShip());
		} else {
			aisRange = settings.getSensorSettings().getAisSensorRange();
		}
		sartMmsiPrefix = settings.getAisSettings().getSartPrefix();
		ESD.startThread(this, "AisHandler");
	}

	/**
	 * Add a route suggestion
	 * @param routeSuggestionListener
	 */
	public void addRouteSuggestionListener(IAisRouteSuggestionListener routeSuggestionListener) {
		suggestionListeners.add(routeSuggestionListener);
	}
	
	/**
	 * Find and init bean function used in initializing other classes
	 */
	@Override
	public void findAndInit(Object obj) {
		if (obj instanceof GpsHandler) {
			gpsHandler = (GpsHandler) obj;
		}
		if (nmeaSensor == null && obj instanceof NmeaSensor) {			
			NmeaSensor sensor = (NmeaSensor)obj;
			if (sensor.isSensorType(SensorType.AIS)) {
				LOG.info("Found AIS sensor");
				nmeaSensor = sensor;
				nmeaSensor.addAisListener(this);
			}
		}
		super.findAndInit(obj);
	}


	@Override
	public void findAndUndo(Object obj) {
		if (obj == nmeaSensor) {
			nmeaSensor.removeAisListener(this);
		}
	}

	/**
	 * Get range of AIS
	 * @return
	 */
	public double getAisRange() {
		return aisRange;
	}

	/**
	 * Return own ship
	 * @return
	 */
	public synchronized VesselTarget getOwnShip() {		
		if (ownShip == null) return null;
		return new VesselTarget(ownShip);
	}
	
	/**
	 * Get list of all ships
	 * @return
	 */
	public synchronized List<AisMessageExtended> getShipList() {
		List<AisMessageExtended> list = new ArrayList<AisMessageExtended>();

		if (this.getVesselTargets() != null) {
			GeoLocation ownPosition;
			double hdg = -1;
			GeoLocation targetPosition = null;

			for (Long key : this.getVesselTargets().keySet()) {
				String name = " N/A";
				String dst = "N/A";
				VesselTarget currentTarget = this.getVesselTargets().get(key);

				if (currentTarget.getStaticData() != null) {
					name = " " + AisMessage.trimText(this.getVesselTargets().get(key).getStaticData().getName());
				}
				if (!gpsHandler.getCurrentData().isBadPosition()) {
					ownPosition = gpsHandler.getCurrentData().getPosition();

					if (currentTarget.getPositionData().getPos() != null) {
						targetPosition = this.getVesselTargets().get(key).getPositionData().getPos();
						NumberFormat nf = NumberFormat.getInstance();
						nf.setMaximumFractionDigits(2);
						dst = nf.format(Converter.metersToNm(ownPosition.getRhumbLineDistance(targetPosition))) + " NM";
					}
				}
				hdg = currentTarget.getPositionData().getCog();

				// System.out.println("Key: " + key + ", Value: " +
				// this.getVesselTargets().get(key));
				AisMessageExtended newEntry = new AisMessageExtended(name, key, hdg, dst);

				if (!this.getVesselTargets().get(key).isGone()) {
					list.add(newEntry);
				}
			}
		}
		return list;
	}
	
	/**
	 * Determine if position is within range
	 * 
	 * @param pos
	 * @return
	 */
	private synchronized boolean isWithinRange(GeoLocation pos) {
		if (getAisRange() <= 0) {
			return true;
		}
		if (gpsHandler == null) {
			return false;
		}
		GpsData gpsData = gpsHandler.getCurrentData();
		if (gpsData == null) {
			return false;
		}
		if (gpsData.isBadPosition()) {
			// If simulation we will not accept targets before own pos is known
			// once
			if (settings.getSensorSettings().isSimulateGps() && vesselTargets.size() == 0) {
				return false;
			}
		}

		double distance = gpsData.getPosition().getRhumbLineDistance(pos) / 1852.0;
		return (distance <= aisRange);
	}
	
	/**
	 * Try to load AIS view from disk
	 */
	public synchronized void loadView() {
		AisVesselStore aisStore = null;		 
		
		try {
			FileInputStream fileIn = new FileInputStream(aisViewFile);
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);
			aisStore = (AisVesselStore) objectIn.readObject();
			objectIn.close();
			fileIn.close();
		} catch (FileNotFoundException e) {
			// Not an error
		} catch (Exception e) {
			LOG.error("Failed to load AIS view file: " + e.getMessage());
			// Delete possible corrupted or old file
			(new File(aisViewFile)).delete();
		}
		
		if (aisStore == null) {
			return;
		}
		
		// Retrieve targets
		if (aisStore.getVesselTargets() != null) {
			vesselTargets = aisStore.getVesselTargets();
		}
		if (aisStore.getAtonTargets() != null) {
			atonTargets = aisStore.getAtonTargets();
		}
		if (aisStore.getSarTargets() != null) {
			sarTargets = aisStore.getSarTargets();
		}
		
		// Retrieve own ship
		ownShip = aisStore.getOwnShip();
		
		LOG.info("AIS handler loaded total targets: " + (vesselTargets.size() + atonTargets.size() + sarTargets.size()));
				
		// Update status to update old and gone (twice for old and gone)
		updateStatus();
		updateStatus();
		
	}

	/**
	 * Recieve aisMessage
	 */
	@Override
	public synchronized void receive(AisMessage aisMessage) {
		super.receive(aisMessage);
		// Look for route suggestion
		if (aisMessage instanceof AisBinaryMessage) {
			AisBinaryMessage binaryMessage = (AisBinaryMessage) aisMessage;
			AisApplicationMessage appMessage;
			try {
				appMessage = binaryMessage.getApplicationMessage();
			} catch (SixbitException e) {
				LOG.error("Failed to get application specific message: " + e.getMessage());
				return;
			}
			// Handle addressed messages
			if (aisMessage.getMsgId() == 6 && appMessage != null) {

				// Check if for own ship
				AisMessage6 msg6 = (AisMessage6) aisMessage;
				if (ownShip.getMmsi() != msg6.getDestination()) {
					return;
				}

				// Handle adressed route information
				if (appMessage.getDac() == 1 && appMessage.getFi() == 28) {
					AddressedRouteInformation routeInformation = (AddressedRouteInformation) appMessage;
					LOG.info("AddressedRouteInformation: " + routeInformation);
					AisAdressedRouteSuggestion addressedRouteSuggestion = new AisAdressedRouteSuggestion(
							routeInformation);
					addressedRouteSuggestion.setSender(aisMessage.getUserId());
					for (IAisRouteSuggestionListener suggestionListener : suggestionListeners) {
						suggestionListener.receiveRouteSuggestion(addressedRouteSuggestion);
					}
					// Acknowledge the reception
					if (suggestionListeners.size() > 0) {
						aisServices.acknowledgeRouteSuggestion(msg6, routeInformation);
					}
				}
			}

		}
	}

	/**
	 * Method for receiving own ship AIS messages
	 * @param aisMessage
	 */
	@Override
	public synchronized void receiveOwnMessage(AisMessage aisMessage) {
		// Determine if our vessel has changed. Clear if so.
		if (ownShip != null) {
			if (aisMessage.getUserId() != ownShip.getMmsi()) {
				ownShip = new VesselTarget();
			}
		}
		
		if (aisMessage instanceof AisPositionMessage) {
			AisPositionMessage aisPositionMessage = (AisPositionMessage) aisMessage;
			ownShip.setAisClass(VesselTarget.AisClass.A);
			ownShip.setPositionData(new VesselPositionData(aisPositionMessage));
		} else if (aisMessage instanceof AisMessage18) {
			AisMessage18 posMessage = (AisMessage18) aisMessage;
			ownShip.setAisClass(VesselTarget.AisClass.B);
			ownShip.setPositionData(new VesselPositionData(posMessage));
		} else if (aisMessage instanceof AisMessage5) {
			AisMessage5 msg5 = (AisMessage5) aisMessage;
			ownShip.setStaticData(new VesselStaticData(msg5));
		}

//		if (GnssTime.getInstance() != null){
//			ownShip.setLastReceived(GnssTime.getInstance().getDate());
//		}
//		ownShip.setMmsi(aisMessage.getUserId());
	}

	/**
	 * Remove a route suggestion
	 * @param routeSuggestionListener
	 */
	public void removeRouteSuggestionListener(IAisRouteSuggestionListener routeSuggestionListener) {
		suggestionListeners.remove(routeSuggestionListener);
	}
	
	/**
	 * Save AIS view to file
	 */
	public synchronized void saveView() {
		AisVesselStore aisStore = new AisVesselStore();
		aisStore.setVesselTargets(vesselTargets);
		aisStore.setAtonTargets(atonTargets);
		aisStore.setSarTargets(sarTargets);
		ownShip.setPositionData(null);
		aisStore.setOwnShip(ownShip);
		
		try {
			FileOutputStream fileOut = new FileOutputStream(aisViewFile);
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(aisStore);
			objectOut.close();
			fileOut.close();
		} catch (IOException e) {
			e.printStackTrace();
			LOG.error("Failed to save Ais view file: " + e.getMessage());
		}
	}
	
	/**
	 * Set range of AIS
	 * @param aisRange
	 */
	public void setAisRange(double aisRange) {
		this.aisRange = aisRange;
	}

	/**
	 * Update AtoN target
	 * 
	 * @param msg21
	 */
	@Override
	protected synchronized void updateAton(AisMessage21 msg21) {
		if (!isWithinRange(msg21.getPos().getGeoLocation())) {
			return;
		}
		super.updateAton(msg21);
	}
	
	/**
	 * Update position of a vessel
	 */
	@Override
	protected synchronized void updatePos(long mmsi, VesselPositionData positionData, VesselTarget.AisClass aisClass) {
		if (!isWithinRange(positionData.getPos())) {
			return;
		}
		super.updatePos(mmsi, positionData, aisClass);
	}

}
