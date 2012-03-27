package dk.frv.enav.esd.layers.ais;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMPoly;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.message.AisMessage;
import dk.frv.enav.esd.ESD;
import dk.frv.enav.esd.ais.AisHandler.AisMessageExtended;
import dk.frv.enav.esd.ais.VesselAisHandler;
import dk.frv.enav.esd.gui.ChartPanel;
import dk.frv.enav.esd.nmea.IVesselAisListener;

public class AisLayer extends OMGraphicHandlerLayer implements Runnable, IVesselAisListener {
	private OMGraphic graphic = new OMGraphicList();
	private ChartPanel chartPanel;
	private OMGraphicList list = new OMGraphicList();
	private Map<Long, OMPoly> targets = new HashMap<Long, OMPoly>();
	private static VesselAisHandler aisHandler;

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
			int[] xPos = { -5, 0, 5, -5 };
			int[] yPos = { -5, 5, -5, -5 };
			list.clear();
			
			List<AisMessageExtended> shipList = aisHandler.getShipList();
			for (int i = 0; i < shipList.size(); i++) {
				if (aisHandler.getVesselTargets().containsKey(shipList.get(i).MMSI)) {
					GeoLocation theShip = aisHandler.getVesselTargets().get(shipList.get(i).MMSI).getPositionData().getPos();
					OMPoly poly = new OMPoly(theShip.getLatitude(), theShip.getLongitude(), xPos, yPos, 0);
					poly.setFillPaint(new Color(0));
					list.add(poly);
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
