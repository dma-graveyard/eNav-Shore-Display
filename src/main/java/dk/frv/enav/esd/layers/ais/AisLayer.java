package dk.frv.enav.esd.layers.ais;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.List;

import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;
import com.bbn.openmap.omGraphics.OMList;
import com.bbn.openmap.omGraphics.OMPoly;
import com.bbn.openmap.omGraphics.OMText;
import com.bbn.openmap.proj.Length;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.frv.ais.message.AisMessage;
import dk.frv.enav.esd.ESD;
import dk.frv.enav.esd.ais.AisHandler.AisMessageExtended;
import dk.frv.enav.esd.ais.VesselAisHandler;
import dk.frv.enav.esd.event.DragMouseMode;
import dk.frv.enav.esd.event.NavigationMouseMode;
import dk.frv.enav.esd.event.SelectMouseMode;
import dk.frv.enav.esd.gui.ChartPanel;
import dk.frv.enav.esd.nmea.IVesselAisListener;
import dk.frv.enav.ins.ais.VesselPositionData;
import dk.frv.enav.ins.ais.VesselStaticData;
import dk.frv.enav.ins.ais.VesselTarget;
import dk.frv.enav.ins.gui.MainFrame;
import dk.frv.enav.ins.layers.ais.AisTargetInfoPanel;

public class AisLayer extends OMGraphicHandlerLayer implements Runnable, IVesselAisListener, MapMouseListener {
	private static final long serialVersionUID = 1L;
	private OMGraphicList list = new OMGraphicList();
	private VesselAisHandler aisHandler;
	private List<AisMessageExtended> shipList;
	private ChartPanel chartPanel;
	private AisTargetInfoPanel aisTargetInfoPanel = new AisTargetInfoPanel();
	
	private OMGraphic closest = null;

	private VesselLayer heading;
	private VesselLayer vesIcon;
	private OMCircle vesCirc;
	
	private OMPoly infoBox;
	private OMText boxMMSI;
	private OMText boxSog;
	private OMText boxCog;

	private OMLine speedVector;
	private LatLonPoint startPos = null;
	private LatLonPoint endPos = null;
	public static final float STROKE_WIDTH = 1.5f;

	private VesselPositionData location;
	private Font font = null;
	private OMText label = null;
	private int sizeOffset = 5;
	private MainFrame mainFrame;
	volatile boolean shouldRun = true;
	private Color shipColor = new Color(78, 78, 78);


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
			boolean putInfoBoxBackIn = false;
			if(list.contains(infoBox)){
				putInfoBoxBackIn = true;
			}
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
						vesIcon = new VesselLayer(shipList.get(i).MMSI,xPos, yPos);
						vesIcon.setLocation(lat, lon, OMGraphic.DECIMAL_DEGREES, hdgR);
						vesIcon.setFillPaint(shipColor);
						list.add(vesIcon);
					} else {
						// Zoom level is too large. Display only dots
						vesCirc = new OMCircle(lat, lon, 0.01);
						vesCirc.setFillPaint(shipColor);
						list.add(vesCirc);
					}

					// Draw heading
					if (mapScale < 750000 && !noHeading && shouldDisplayHeading) {
						int[] xPosh = { 0, 0 };
						int[] yPosh = { 0, -30 };
						heading = new VesselLayer(shipList.get(i).MMSI, xPosh, yPosh);
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
			if(putInfoBoxBackIn){
				list.add(boxMMSI);
				list.add(boxSog);
				list.add(boxCog);
				list.add(infoBox);
			}
			doPrepare();
		}
	}

	@Override
	public synchronized OMGraphicList prepare() {
		list.project(getProjection());
		return list;
	}

	public MapMouseListener getMapMouseListener() {
		return this;
	}
	
	@Override
	public void findAndInit(Object obj) {
		if (obj instanceof VesselAisHandler) {
			aisHandler = (VesselAisHandler) obj;
		}
		if (obj instanceof ChartPanel) {
			chartPanel = (ChartPanel) obj;
		}
		if (obj instanceof MainFrame) {
			mainFrame = (MainFrame) obj;
			mainFrame.getGlassPanel().add(aisTargetInfoPanel);
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

	@Override
	public String[] getMouseModeServiceList() {
		String[] ret = new String[3];
		ret[0] = DragMouseMode.modeID; // "DragMouseMode"
		ret[1] = NavigationMouseMode.modeID; // "ZoomMouseMoude"
		ret[1] = SelectMouseMode.modeID; // "SelectMouseMode"
		return ret;
	}

	@Override
	public boolean mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(MouseEvent e) {		
		OMGraphic newClosest = null;
		OMList<OMGraphic> allClosest = list.findAll(e.getX(), e.getY(), 3.0f);
		for (OMGraphic omGraphic : allClosest) {
			if(omGraphic instanceof VesselLayer){
				newClosest = omGraphic;
				break;
			}
		}

		if(allClosest.size() == 0){
			list.remove(infoBox);
		}
		
		if (newClosest != closest) {
			list.remove(boxMMSI);
			list.remove(boxSog);
			list.remove(boxCog);
			list.remove(infoBox);
			
			if (newClosest instanceof VesselLayer) {
				VesselLayer vessel = (VesselLayer) newClosest;
				VesselTarget vesselTarget = aisHandler.getVesselTargets().get(vessel.getMMSI());
				
				// Add MouseOverBox
				location = vesselTarget.getPositionData();
				double lat = location.getPos().getLatitude();
				double lon = location.getPos().getLongitude();
				double trueHeading = location.getTrueHeading();
				int[] xpoints = { 10, 10, 200, 200 };
				int[] ypoints = { 0, -50, -50, 0 };
				infoBox = new OMPoly(lat, lon, xpoints, ypoints, OMGraphic.DECIMAL_DEGREES);
				boxMMSI = new OMText(lat, lon, 15, 0, "MMSI: " + vessel.getMMSI(), font, OMText.JUSTIFY_LEFT);
				boxSog = new OMText(lat, lon, 15, 0, "Sog: " + location.getSog() + " kn", font, OMText.JUSTIFY_LEFT);
				boxCog = new OMText(lat, lon, 15, 0, "Cog: " + location.getCog() + " degrees", font, OMText.JUSTIFY_LEFT);
				if (trueHeading > 90 && trueHeading < 270) {
					int[] ypoints2 = { 0, 50, 50, 0 };
					infoBox.setYs(ypoints2);
					boxMMSI.setY(15);
					boxSog.setY(25);
					boxCog.setY(35);
				} else {
					boxMMSI.setY(-35);
					boxSog.setY(-25);
					boxCog.setY(-15);
				}
				infoBox.setFillPaint(new Color(225, 225, 225));
				list.add(boxMMSI);
				list.add(boxSog);
				list.add(boxCog);
				list.add(infoBox);
				closest = newClosest;
				doPrepare();
				return true;
			} 
		}
		return false;
	}

	@Override
	public void mouseMoved() {
		// TODO Auto-generated method stub
		
	}
}
