package dk.frv.enav.esd.service.ais;

import java.util.Date;

import dk.frv.enav.esd.route.Route;
import dk.frv.enav.esd.service.ais.AisServices.AIS_STATUS;

public class RouteSuggestionData {
	
	private int mmsi;
	private Route route;
	private Date timeSent;
	private AIS_STATUS status;

	public RouteSuggestionData(int mmsi, Route route, Date timeSent, AIS_STATUS status){
		this.mmsi = mmsi;
		this.route = route;
		this.timeSent = timeSent;
		this.status = status;
	}

	public int getMmsi() {
		return mmsi;
	}

	public void setMmsi(int mmsi) {
		this.mmsi = mmsi;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public Date getTimeSent() {
		return timeSent;
	}

	public void setTimeSent(Date timeSent) {
		this.timeSent = timeSent;
	}

	public AIS_STATUS getStatus() {
		return status;
	}

	public void setStatus(AIS_STATUS status) {
		this.status = status;
	}
	
	
	
}
