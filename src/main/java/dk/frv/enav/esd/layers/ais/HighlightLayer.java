package dk.frv.enav.esd.layers.ais;

import javax.swing.ImageIcon;

import dk.frv.enav.esd.ESD;
import dk.frv.enav.ins.common.graphics.CenterRaster;

/**
 * Highlight layer that creates a circle under the vessel
 * 
 * @author Claes N. Ladefoged, claesnl@gmail.com
 * 
 */
public class HighlightLayer extends CenterRaster {
	private static final long serialVersionUID = 1L;
	private long MMSI;
	private double lat;
	private double lon;

	/**
	 * Initialize a highlight layer with default position
	 * @param MMSI Key of vessel
	 */
	public HighlightLayer(long MMSI) {
		super(0, 0, 50, 50, new ImageIcon(ESD.class.getResource("/images/ais/highlight.png")));
		this.MMSI = MMSI;
		this.setVisible(false);
	}

	/**
	 * Moves the highlight layer
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
	 * Get the MMSI attached to the layer
	 * @return MMSI Key of vessel
	 */
	public long getMMSI() {
		return this.MMSI;
	}
}
