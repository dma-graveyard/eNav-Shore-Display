package dk.frv.enav.esd.layers.ais;

import javax.swing.ImageIcon;

import dk.frv.enav.esd.ESD;
import dk.frv.enav.ins.common.graphics.CenterRaster;

public class VesselLayer extends CenterRaster {
	private static final long serialVersionUID = 1L;
	private long MMSI;
	private ImageIcon vesselIcon;
	private double lat;
	private double lon;
	private double trueHeading;
	private String shipType;

	public VesselLayer(long MMSI) {
		super(0, 0, 24, 24, new ImageIcon(ESD.class.getResource("/images/vesselIcons/white1_90.png")));
		this.MMSI = MMSI;
	}

	public void setHeading(double trueHeading) {
		if (this.trueHeading != trueHeading) {
			this.trueHeading = trueHeading;
			this.setRotationAngle(Math.toRadians(trueHeading - 90));
		}
	}

	public void setLocation(double lat, double lon) {
		if (this.lat != lat || this.lon != lon) {
			this.lat = lat;
			this.lon = lon;
			this.setLat(lat);
			this.setLon(lon);
		}
	}

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

	public long getMMSI() {
		return this.MMSI;
	}
}
