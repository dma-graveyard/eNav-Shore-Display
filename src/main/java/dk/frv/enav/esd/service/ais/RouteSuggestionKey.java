package dk.frv.enav.esd.service.ais;

public class RouteSuggestionKey {

	private long mmsi;
	private int id;

	public RouteSuggestionKey(long mmsi, int id) {
		this.mmsi = mmsi;
		this.id = id;
	}

	public long getMmsi() {
		return mmsi;
	}

	public int getId() {
		return id;
	}

	@Override
	public boolean equals(Object key) {
		
		RouteSuggestionKey routeKey = (RouteSuggestionKey) key;
		
		if (routeKey.getId() == this.id && routeKey.getMmsi() == this.mmsi) {
			return true;
		} else {
			return false;
		}
	}
	
	public String toString(){
		return "mmsi: " + this.mmsi + " id: " + this.id;
	}
}
