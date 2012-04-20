package dk.frv.enav.esd.layers.ais;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;
import com.bbn.openmap.omGraphics.OMText;
import com.bbn.openmap.proj.Length;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.frv.ais.message.AisMessage;
import dk.frv.enav.esd.ESD;
import dk.frv.enav.esd.ais.AisHandler.AisMessageExtended;
import dk.frv.enav.esd.ais.VesselAisHandler;
import dk.frv.enav.esd.nmea.IVesselAisListener;
import dk.frv.enav.ins.ais.VesselPositionData;
import dk.frv.enav.ins.ais.VesselStaticData;
import dk.frv.enav.ins.ais.VesselTarget;
import dk.frv.enav.esd.gui.ChartPanel;
import dk.frv.enav.esd.layers.ais.VesselLayer;

public class AisLayer extends OMGraphicHandlerLayer implements Runnable, IVesselAisListener {
	private static final long serialVersionUID = 1L;
	private OMGraphicList list = new OMGraphicList();
	private static VesselAisHandler aisHandler;
	private List<AisMessageExtended> shipList;
	private ChartPanel chartPanel;

	private VesselLayer heading;
	private VesselLayer vesIcon;
	private OMCircle vesCirc;

	private OMLine speedVector;
	private LatLonPoint startPos = null;
	private LatLonPoint endPos = null;
	public static final float STROKE_WIDTH = 1.5f;

	private VesselPositionData location;
	private Font font = null;
	private OMText label = null;
	private int sizeOffset = 5;
	volatile boolean shouldRun = true;

	@Override
	public void run() {
		while (shouldRun) {
			ESD.sleep(1000);
			drawVessels();
		}
	}

	public AisLayer() {
		(new Thread(this)).start();
	}

	public void stop() {
		shouldRun = false;
	}

	private void drawVessels() {
		if (aisHandler != null) {
			list.clear();

			float mapScale = chartPanel.getMap().getScale();
			boolean shouldDisplayVessel = true;
			boolean shouldDisplayHeading = true;
			boolean shouldDisplaySpeed = true;
			boolean shouldDisplayMMSI = true;
			boolean shouldDisplayCallSign = true;

			shipList = aisHandler.getShipList();
			for (int i = 0; i < shipList.size(); i++) {
				if (aisHandler.getVesselTargets().containsKey(shipList.get(i).MMSI)) {
					AisMessageExtended vessel = shipList.get(i);
					VesselTarget vesselTarget = aisHandler.getVesselTargets().get(vessel.MMSI);
					VesselStaticData staticData = vesselTarget.getStaticData();
					font = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
					location = vesselTarget.getPositionData();
					double lat = location.getPos().getLatitude();
					double lon = location.getPos().getLongitude();
					double trueHeading = location.getTrueHeading();
					boolean noHeading = false;
					if (trueHeading == 511) {
						trueHeading = vesselTarget.getPositionData().getCog();
						noHeading = true;
					}
					double hdgR = Math.toRadians(trueHeading);
					double cogR = Math.toRadians(location.getCog());
					double sog = location.getSog();

					// Draw Vessel
					if (mapScale < 1500000.0 && shouldDisplayVessel) {
						// Zoom level is good. Display vessel icon
						int[] xPos = { sizeOffset, -sizeOffset, 0 };
						int[] yPos = { sizeOffset, sizeOffset, -2*sizeOffset };
						vesIcon = new VesselLayer(xPos, yPos);
						vesIcon.setLocation(lat, lon, OMGraphic.DECIMAL_DEGREES, hdgR);
						vesIcon.setFillPaint(new Color(0, 0, 255));
						list.add(vesIcon);
					} else {
						// Zoom level is too large. Display only dots
						vesCirc = new OMCircle(lat, lon, 0.01);
						vesCirc.setFillPaint(new Color(0, 0, 255));
						list.add(vesCirc);
					}

					// Draw heading
					if (mapScale < 750000 && !noHeading && shouldDisplayHeading) {
						int[] xPosh = { 0, 0 };
						int[] yPosh = { 0, -30 };
						heading = new VesselLayer(xPosh, yPosh);
						heading.setLocation(lat, lon, OMGraphic.DECIMAL_DEGREES, hdgR);
						heading.setFillPaint(new Color(0, 0, 0));
						list.add(heading);
					}

					// Draw call sign
					if (staticData != null && mapScale < 750000.0 && shouldDisplayCallSign) {
						label = new OMText(0, 0, 0, 0, "Call Sign: " + staticData.getCallsign(), font,
								OMText.JUSTIFY_CENTER);
						label.setLat(lat);
						label.setLon(lon);
						if (trueHeading > 90 && trueHeading < 270) {
							label.setY(-25);
						} else {
							label.setY(35);
						}
						list.add(label);
					}

					// Draw speed vector
					if (mapScale < 750000.0 && shouldDisplaySpeed) {
						speedVector = new OMLine(0, 0, 0, 0, OMLine.LINETYPE_STRAIGHT);
						speedVector.setStroke(new BasicStroke(STROKE_WIDTH, BasicStroke.CAP_SQUARE,
								BasicStroke.JOIN_MITER, 10.0f, new float[] { 10.0f, 8.0f }, 0.0f));
						speedVector.setLinePaint(new Color(255, 0, 0));
						list.add(speedVector);
						double[] speedLL = new double[4];
						speedLL[0] = (float) lat;
						speedLL[1] = (float) lon;
						startPos = new LatLonPoint.Double(lat, lon);
						float length = (float) Length.NM.toRadians(6.0 * (sog / 60.0));
						endPos = startPos.getPoint(length, cogR);
						speedLL[2] = endPos.getLatitude();
						speedLL[3] = endPos.getLongitude();
						speedVector.setLL(speedLL);
					}

					// Add MMSI/name tag
					if (mapScale < 750000.0 && shouldDisplayMMSI) {
						label = new OMText(0, 0, 0, 0, Long.toString(shipList.get(i).MMSI), font, OMText.JUSTIFY_CENTER);
						label.setLat(lat);
						label.setLon(lon);
						if (trueHeading > 90 && trueHeading < 270) {
							label.setY(-10);
						} else {
							label.setY(20);
						}
						String name;
						if (staticData != null) {
							name = AisMessage.trimText(staticData.getName());
						} else {
							Long mmsi = shipList.get(i).MMSI;
							name = "ID:" + mmsi.toString();
						}
						label.setData(name);
						list.add(label);
					}
				}
			}
			doPrepare();
		}
	}

	@Override
	public synchronized OMGraphicList prepare() {
		list.project(getProjection());
		return list;
	}

	@Override
	public void findAndInit(Object obj) {
		if (obj instanceof VesselAisHandler) {
			aisHandler = (VesselAisHandler) obj;
		}
		if (obj instanceof ChartPanel) {
			chartPanel = (ChartPanel) obj;
		}

	}

	@Override
	public void receive(AisMessage arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveOwnMessage(AisMessage aisMessage) {
		// TODO Auto-generated method stub

	}
}
