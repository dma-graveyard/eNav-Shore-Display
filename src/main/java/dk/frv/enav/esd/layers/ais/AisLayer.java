package dk.frv.enav.esd.layers.ais;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMPoly;
import com.bbn.openmap.omGraphics.OMText;
import com.bbn.openmap.omGraphics.labeled.LabeledOMGraphic;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.message.AisMessage;
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
	
	private VesselPositionData location;
	private OMPoly poly;
	private Font font = null;
	private OMText label = null;
	private int sizeOffset = 5;
	private String callSign;
	private String name;

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
			
			// Size of triangle
			int[] xPos = { -sizeOffset, 0, sizeOffset, -sizeOffset };
			int[] yPos = { -sizeOffset, sizeOffset, -sizeOffset, -sizeOffset };
			
			List<AisMessageExtended> shipList = aisHandler.getShipList();
			for (int i = 0; i < shipList.size(); i++) {
				if (aisHandler.getVesselTargets().containsKey(shipList.get(i).MMSI)) {
					AisMessageExtended vessel = shipList.get(i);
					VesselTarget vesselTarget = aisHandler.getVesselTargets().get(vessel.MMSI);
					VesselStaticData staticData = vesselTarget.getStaticData();
					font = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
					location = vesselTarget.getPositionData();
					double lat = location.getPos().getLatitude();
					double lon = location.getPos().getLongitude();
					
					// Draw vessel
					poly = new OMPoly(location.getPos().getLatitude(), location.getPos().getLongitude(), xPos, yPos, 0);
					poly.setFillPaint(new Color(0));
					list.add(poly);
					
					// Add MMSI sign
				    label = new OMText(0, 0, 0, 0, Long.toString(shipList.get(i).MMSI), font, OMText.JUSTIFY_CENTER);
				    label.setLat(lat);
				    label.setLon(lon);
				    label.setY(4*sizeOffset);
				    label.setData("ID: " + Long.toString(vesselTarget.getMmsi()));
				    list.add(label);
				    
				    if(staticData != null){
				    	callSign = staticData.getCallsign();
				    	name = staticData.getName();
				    } else {
				    	callSign = "N/A";
				    	name = "N/A";
				    }
				    
				    // Add call sign
				    label = new OMText(0, 0, 0, 0, Long.toString(shipList.get(i).MMSI), font, OMText.JUSTIFY_RIGHT);
				    label.setLat(lat);
				    label.setLon(lon);
				    label.setX(-2*sizeOffset);
				    label.setY(sizeOffset);
				    label.setData("Call Sign: " + callSign);
				    list.add(label);
				    
				    // Add call sign
				    label = new OMText(0, 0, 0, 0, Long.toString(shipList.get(i).MMSI), font, OMText.JUSTIFY_LEFT);
				    label.setLat(lat);
				    label.setLon(lon);
				    label.setX(2*sizeOffset);
				    label.setY(sizeOffset);
				    label.setData("Name: " + name);
				    list.add(label);
				    
				    // Add heading
				    float trueHeading = location.getTrueHeading();
				    System.out.println(trueHeading);
				    boolean noHeading = false;
			        if (trueHeading == 511) {
			            trueHeading = location.getCog();
			            noHeading = true;
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
