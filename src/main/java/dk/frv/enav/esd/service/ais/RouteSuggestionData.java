package dk.frv.enav.esd.service.ais;

import java.util.Date;

import dk.frv.enav.esd.route.Route;
import dk.frv.enav.esd.service.ais.AisServices.AIS_STATUS;

public class RouteSuggestionData {
	
	private long mmsi;
	private Route route;
	private Date timeSent;
	private AIS_STATUS status;
	private int id;
	private boolean acknowleged;

	public RouteSuggestionData(int id, int mmsi, Route route, Date timeSent, AIS_STATUS status, boolean acknowleged){
		this.mmsi = mmsi;
		this.route = route;
		this.timeSent = timeSent;
		this.status = status;
		this.id = id;
		this.acknowleged = acknowleged;
	}

	
	
	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public long getMmsi() {
		return mmsi;
	}

	public void setMmsi(long mmsi) {
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
	
	
	public String toString(){
		return mmsi + " " + route.getName() + " " + status;
	}



	public boolean isAcknowleged() {
		return acknowleged;
	}



	public void setAcknowleged(boolean acknowleged) {
		this.acknowleged = acknowleged;
	}
	
	
}
