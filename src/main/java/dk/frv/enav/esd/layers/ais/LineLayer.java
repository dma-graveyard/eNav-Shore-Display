package dk.frv.enav.esd.layers.ais;

import java.awt.Color;

import com.bbn.openmap.omGraphics.OMLine;
import com.bbn.openmap.proj.Length;
import com.bbn.openmap.proj.coords.LatLonPoint;

public class LineLayer extends OMLine {

	private static final long serialVersionUID = 1L;
	private LatLonPoint startPos = null;
	private LatLonPoint endPos = null;
	double[] speedLL = new double[4];
	public static final float STROKE_WIDTH = 1.5f;

	public LineLayer() {
		super();
		super.setRenderType(OMLine.RENDERTYPE_LATLON);
		super.setLinePaint(new Color(255, 0, 0));
	}

	public void setLocation(double latPoint, double lonPoint, int units, double heading, double sog) {
		speedLL[0] = (float) latPoint;
		speedLL[1] = (float) lonPoint;
		this.startPos = new LatLonPoint.Double(latPoint, lonPoint);
		float length = (float) Length.NM.toRadians(6.0 * (sog / 60.0));
		this.endPos = startPos.getPoint(length, heading);
		speedLL[2] = endPos.getLatitude();
		speedLL[3] = endPos.getLongitude();
		super.setLL(speedLL);
	}
}
