package dk.frv.enav.esd.layers.ais;

import java.awt.Color;
import java.util.HashMap;
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
			list.clear();
			// Long theShipMMSI = aisHandler.getShipList().get(0).MMSI;
			Long theShipMMSI = (long) 218125000;
			if (aisHandler.getVesselTargets().containsKey(theShipMMSI)) {
				GeoLocation theShip = aisHandler.getVesselTargets().get(theShipMMSI).getPositionData().getPos();

				int[] xPos = { -5, 0, 5, -5 };
				int[] yPos = { -5, 5, -5, -5 };
				OMPoly poly = new OMPoly(theShip.getLatitude(), theShip.getLongitude(), xPos, yPos, 0);
				poly.setFillPaint(new Color(0));
				list.add(poly);
				doPrepare();
			}
		}

		// list.clear();
		// int[] xPos = {-5,0,5,-5};
		// int[] yPos = {-5,5,-5,-5};
		//
		// double shipLat = 55.6761;
		// double shipLon = 12.5683;
		// OMPoly poly = new OMPoly(shipLat, shipLon, xPos, yPos, 0);
		// poly.setFillPaint(new Color(0));
		// list.add(poly);
		//
		// shipLat = 55.0309;
		// shipLon = 14.9924;
		// poly = new OMPoly(shipLat, shipLon, xPos, yPos, 0);
		// poly.setFillPaint(new Color(0));
		// list.add(poly);
		//
		// doPrepare();
	}

	@Override
	public synchronized OMGraphicList prepare() {
		list.project(getProjection());
		return list;
	}

	@Override
	public void findAndInit(Object obj) {
		System.out.println(obj.toString());
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
