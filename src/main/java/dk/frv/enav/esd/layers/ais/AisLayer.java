package dk.frv.enav.esd.layers.ais;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;

import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;
import com.bbn.openmap.omGraphics.OMPoly;
import com.bbn.openmap.omGraphics.OMText;
import com.bbn.openmap.omGraphics.labeled.LabeledOMGraphic;
import com.bbn.openmap.proj.Length;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.message.AisMessage;
import dk.frv.ais.message.ShipTypeCargo;
import dk.frv.enav.esd.ESD;
import dk.frv.enav.esd.ais.AisHandler.AisMessageExtended;
import dk.frv.enav.esd.ais.VesselAisHandler;
import dk.frv.enav.esd.gui.ChartPanel;
import dk.frv.enav.esd.nmea.IVesselAisListener;
import dk.frv.enav.ins.ais.VesselPositionData;
import dk.frv.enav.ins.ais.VesselStaticData;
import dk.frv.enav.ins.ais.VesselTarget;

public class AisLayer extends OMGraphicHandlerLayer implements Runnable, IVesselAisListener {
	private OMGraphic graphic = new OMGraphicList();
	private ChartPanel chartPanel;
	private OMGraphicList list = new OMGraphicList();
	private Map<Long, OMPoly> targets = new HashMap<Long, OMPoly>();
	private static VesselAisHandler aisHandler;
	private List<AisMessageExtended> shipList;

	private VesselLayer heading;
	private VesselLayer ves;
	private VesselLayer speed;
	private LatLonPoint startPos = null;
	private LatLonPoint endPos = null;
	
	private VesselPositionData location;
	private OMPoly poly;
	private Font font = null;
	private OMText label = null;
	private int sizeOffset = 5;

	@Override
	public void run() {
		while (true) {
			ESD.sleep(1000);
			drawVessels();
		}
	}

	public AisLayer() {
		(new Thread(this)).start();
	}

	private void drawVessels() {
		if (aisHandler != null) {
			list.clear();

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
					int[] xPos = { sizeOffset, -sizeOffset, 0 };
					int[] yPos = { sizeOffset, sizeOffset, -sizeOffset };
					ves = new VesselLayer(xPos, yPos);
					ves.setLocation(lat, lon, OMGraphic.DECIMAL_DEGREES, hdgR);
					ves.setFillPaint(new Color(0, 0, 255));
					list.add(ves);

					// Draw heading
					int[] xPosh = { 0, 0 };
					int[] yPosh = { 0, -30 };
					heading = new VesselLayer(xPosh, yPosh);
					heading.setLocation(lat, lon, OMGraphic.DECIMAL_DEGREES, hdgR);
					heading.setFillPaint(new Color(0, 0, 0));
					if (!noHeading)
						list.add(heading);

					// Add MMSI/name tag
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
					
					// Draw speed vector
					/*
					 * Should cogR be with sog instead? This is not done yet.
					 * */
					int[] xPoss = { 0, 0 };
					int[] yPoss = { 0, (int) (-60 * (sog / 60.0)) };
					speed = new VesselLayer(xPoss, yPoss);
					speed.setLocation(lat, lon, OMGraphic.DECIMAL_DEGREES, cogR);
					speed.setFillPaint(new Color(255, 0, 0));
					list.add(speed);
					
					// Draw call sign
					if (staticData != null) {
						label = new OMText(0, 0, 0, 0, Long.toString(shipList.get(i).MMSI), font, OMText.JUSTIFY_CENTER);
						label.setLat(lat);
						label.setLon(lon);
						if (trueHeading > 90 && trueHeading < 270) {
							label.setY(-25);
						} else {
							label.setY(35);
						}
						label.setData("Call Sign: " + staticData.getCallsign());
						list.add(label);
					}
				}
			}
			doPrepare();
		}
	}

	private void updateVessels() {
		if (aisHandler != null) {
			shipList = aisHandler.getShipList();
			for (int i = 0; i < shipList.size(); i++) {
				if (aisHandler.getVesselTargets().containsKey(shipList.get(i).MMSI)) {
				}
			}
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
