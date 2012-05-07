package dk.frv.enav.esd.layers.ais;

import com.bbn.openmap.omGraphics.OMPoly;

public class HeadingLayer extends OMPoly {

	private static final long serialVersionUID = 1L;
	private double heading;
	private int[] origXPoints;
	private int[] origYPoints;
	private int[] xPoints;
	private int[] yPoints;
	private long MMSI;

	public HeadingLayer(long MMSI, int[] origXPoints, int[] origYPoints) {
		super();
		this.MMSI = MMSI;
		this.origXPoints = origXPoints;
		this.origYPoints = origYPoints;
		this.xPoints = new int[origXPoints.length];
		this.yPoints = new int[origYPoints.length];
		this.heading = 0;
	}

	public void setLocation(double latPoint, double lonPoint, int units, double heading) {
		if (this.heading != heading) {
			for (int i = 0; i < origXPoints.length; i++) {
				xPoints[i] = (int) (origXPoints[i] * Math.cos(heading) - origYPoints[i] * Math.sin(heading));
				yPoints[i] = (int) (origXPoints[i] * Math.sin(heading) + origYPoints[i] * Math.cos(heading));
			}
			this.heading = heading;
		}
		super.setLocation(latPoint, lonPoint, units, xPoints, yPoints);
	}

	public long getMMSI() {
		return MMSI;
	}
	
}
