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
package dk.frv.enav.esd.service.ais;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;

import com.bbn.openmap.MapHandlerChild;

import dk.frv.ais.message.AisMessage6;
import dk.frv.ais.message.AisPosition;
import dk.frv.ais.message.binary.AsmAcknowledge;
import dk.frv.ais.message.binary.RouteSuggestion;
import dk.frv.ais.message.binary.RouteSuggestion.RouteType;
import dk.frv.ais.message.binary.RouteSuggestionReply;
import dk.frv.ais.reader.SendRequest;
import dk.frv.enav.esd.ais.AISRouteExchangeListener;
import dk.frv.enav.esd.route.Route;
import dk.frv.enav.ins.ais.AisHandler;
import dk.frv.enav.ins.settings.Settings;

/**
 * AIS service component providing an AIS link interface.
 */
public class AisServices extends MapHandlerChild {

	private Settings settings;
	private AisHandler aisHandler;
	protected int idCounter = 0;
	RouteSuggestionDataStructure<RouteSuggestionKey, RouteSuggestionData> routeSuggestions = new RouteSuggestionDataStructure<RouteSuggestionKey, RouteSuggestionData>();
	protected Set<AISRouteExchangeListener> routeExchangeListener = new HashSet<AISRouteExchangeListener>();

	public enum AIS_STATUS {
		NOT_SENT, FAILED, SENT_NOT_ACK, RECIEVED_APP_ACK, RECIEVED_ACCEPTED, RECIEVED_REJECTED, RECIEVED_NOTED
	}

	public AisServices() {
		 Random generator = new Random();
		idCounter = generator.nextInt(1000);
	}

	public void acknowledgedRecieved(long mmsi, AsmAcknowledge reply) {
System.out.println("ack?");
		if (routeSuggestions.containsKey(new RouteSuggestionKey(mmsi, reply.getTextSequenceNum()))) {

			System.out.println("Acknowledge recieved for " + mmsi + " " + reply.getTextSequenceNum());

			if (routeSuggestions.get(new RouteSuggestionKey(mmsi, reply.getTextSequenceNum())).getStatus() != AIS_STATUS.RECIEVED_APP_ACK){
				//New change
				
				routeSuggestions.get(new RouteSuggestionKey(mmsi, reply.getTextSequenceNum())).setStatus(
						AIS_STATUS.RECIEVED_APP_ACK);
				routeSuggestions.get(new RouteSuggestionKey(mmsi, reply.getTextSequenceNum())).setAcknowleged(false);
				routeSuggestions.get(new RouteSuggestionKey(mmsi, reply.getTextSequenceNum())).setAppAck(new Date());
				notifyRouteExchangeListeners();
			}

		}
		


	}
	
	public void replyRecieved(long mmsi, RouteSuggestionReply message) {

		if (routeSuggestions.containsKey(new RouteSuggestionKey(mmsi, message.getRefMsgLinkId()))) {

//			System.out.println("Reply recieved for " + mmsi + " " + message.getRefMsgLinkId());
			int response = message.getResponse();

			switch (response) {
			case 0:
				if (routeSuggestions.get(new RouteSuggestionKey(mmsi, message.getRefMsgLinkId())).getStatus() != AIS_STATUS.RECIEVED_ACCEPTED){
					// Accepted
					routeSuggestions.get(new RouteSuggestionKey(mmsi, message.getRefMsgLinkId())).setStatus(
							AIS_STATUS.RECIEVED_ACCEPTED);
					routeSuggestions.get(new RouteSuggestionKey(mmsi, message.getRefMsgLinkId())).setAcknowleged(false);
					notifyRouteExchangeListeners();
				}

				break;
			case 1:
				// Rejected
				if (routeSuggestions.get(new RouteSuggestionKey(mmsi, message.getRefMsgLinkId())).getStatus() != AIS_STATUS.RECIEVED_REJECTED){
					// Accepted
					routeSuggestions.get(new RouteSuggestionKey(mmsi, message.getRefMsgLinkId())).setStatus(
							AIS_STATUS.RECIEVED_REJECTED);
					routeSuggestions.get(new RouteSuggestionKey(mmsi, message.getRefMsgLinkId())).setAcknowleged(false);
					notifyRouteExchangeListeners();
				}
				break;
			case 2:
				// Noted
				if (routeSuggestions.get(new RouteSuggestionKey(mmsi, message.getRefMsgLinkId())).getStatus() != AIS_STATUS.RECIEVED_NOTED){
					// Accepted
					routeSuggestions.get(new RouteSuggestionKey(mmsi, message.getRefMsgLinkId())).setStatus(
							AIS_STATUS.RECIEVED_NOTED);
					routeSuggestions.get(new RouteSuggestionKey(mmsi, message.getRefMsgLinkId())).setAcknowleged(false);
					notifyRouteExchangeListeners();
				}
				break;
			default:
				break;
			}
		}

	}

	/**
	 * Add a listener to the asService
	 * 
	 * @param listener
	 */
	public synchronized void addRouteExchangeListener(AISRouteExchangeListener listener) {
		routeExchangeListener.add(listener);
	}
	
	protected synchronized void notifyRouteExchangeListeners(){

		for (AISRouteExchangeListener listener : routeExchangeListener) {
			listener.aisUpdate();
		}

	}


	public void sendRouteSuggestion(int mmsiDestination, Route route) {
//		System.out.println("Send Route Suggestion");

		// Create route suggestion - intended route ASM
		RouteSuggestion routeSuggestion = new RouteSuggestion();
		routeSuggestion.setRouteType(RouteType.RECOMMENDED.getType());
		routeSuggestion.setDuration(0);

		// Convert the route

		// Recalculate all remaining ETA's

		int maxWps = 8;

//		Date start = route.getStarttime();
//
//		if (start == null) {
//			start = new Date();
//		}
		
		Date start = new Date();

		// Set start time
		Calendar cal = Calendar.getInstance();
		cal.setTime(start);
		cal.setTimeZone(TimeZone.getTimeZone("GMT+0000"));
		routeSuggestion.setStartMonth(cal.get(Calendar.MONTH) + 1);
		routeSuggestion.setStartDay(cal.get(Calendar.DAY_OF_MONTH));
		routeSuggestion.setStartHour(cal.get(Calendar.HOUR_OF_DAY));
		routeSuggestion.setStartMin(cal.get(Calendar.MINUTE));

		routeSuggestion.setStartMonth(cal.get(Calendar.MONTH) + 1);
		routeSuggestion.setStartDay(cal.get(Calendar.DAY_OF_MONTH));
		routeSuggestion.setStartHour(cal.get(Calendar.HOUR_OF_DAY));
		routeSuggestion.setStartMin(cal.get(Calendar.MINUTE));

		int waypoints;

		if (maxWps < route.getWaypoints().size()) {
			waypoints = maxWps;
		} else {
			waypoints = route.getWaypoints().size();
		}

		// Add waypoints
		for (int i = 0; i < waypoints; i++) {
			routeSuggestion.addWaypoint(new AisPosition(route.getWaypoints().get(i).getPos()));
		}

		int id = getID();

		// Generate the uniqueID based on mmsiDestination and current time
		routeSuggestion.setMsgLinkId(id);

		// Generate msg6 type AIS
		AisMessage6 msg6 = new AisMessage6();
		msg6.setAppMessage(routeSuggestion);

		msg6.setRetransmit(0);
		msg6.setDestination(mmsiDestination);

		// Add it to the hashmap
		routeSuggestions.put(new RouteSuggestionKey(mmsiDestination, id), new RouteSuggestionData(id, mmsiDestination,
				route, start, AIS_STATUS.NOT_SENT, false, null));

		// Create a send request
		SendRequest sendRequest = new SendRequest(msg6, 1, mmsiDestination);

		// Create a send thread
		AisSendThread aisSendThread = new AisSendThread(sendRequest, this, id);

		// Start send thread
		aisSendThread.start();
		
		notifyRouteExchangeListeners();
	}

	synchronized int getID() {
		idCounter++;
		return idCounter;
	}

	@Override
	public void findAndInit(Object obj) {
		if (settings == null && obj instanceof Settings) {
			settings = (Settings) obj;
		} else if (aisHandler == null && obj instanceof AisHandler) {
			aisHandler = (AisHandler) obj;
		}
	}

	public void sendResult(boolean sendOk, int mmsi, int id) {

		if (sendOk) {
			if (routeSuggestions.get(new RouteSuggestionKey(mmsi, id)).getStatus() != AIS_STATUS.RECIEVED_APP_ACK){
				routeSuggestions.get(new RouteSuggestionKey(Long.valueOf(mmsi), id)).setStatus(AIS_STATUS.SENT_NOT_ACK);
				
			}
		} else {
			routeSuggestions.get(new RouteSuggestionKey(Long.valueOf(mmsi), id)).setStatus(AIS_STATUS.FAILED);
		}

		if (aisHandler == null)
			return;
		if (sendOk) {
			aisHandler.getAisStatus().markSuccesfullSend();
		} else {
			aisHandler.getAisStatus().markFailedSend();
		}
	}

	public RouteSuggestionDataStructure<RouteSuggestionKey, RouteSuggestionData> getRouteSuggestions() {
		return routeSuggestions;
	}
	
	public void setAcknowledged(long l, int id){
		routeSuggestions.get(new RouteSuggestionKey(l, id)).setAcknowleged(true);
		notifyRouteExchangeListeners();
	}
	
	public void removeSuggestion(long l, int id){
		routeSuggestions.remove(new RouteSuggestionKey(l, id));
		notifyRouteExchangeListeners();
	}
	
	public int getUnkAck(){
		
		int counter = 0;
		
	    Collection<RouteSuggestionData> c = routeSuggestions.values();
	    
	    //obtain an Iterator for Collection
	    Iterator<RouteSuggestionData> itr = c.iterator();
	   
	    //iterate through HashMap values iterator
	    while(itr.hasNext()){
	    	RouteSuggestionData value = itr.next();
	    	if (!value.isAcknowleged()){
	    		counter++;
	    	}
	    }
	    
		return counter;
	}
	

}
