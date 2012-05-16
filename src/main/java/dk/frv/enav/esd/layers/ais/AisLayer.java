/*
 * Copyright 2012 Danish Maritime Authority. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 *   2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Safety Administration ``AS IS'' 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of Danish Maritime Authority.
 * 
 */
package dk.frv.enav.esd.layers.ais;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;

import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMList;

import dk.frv.ais.message.AisMessage;
import dk.frv.enav.esd.ESD;
import dk.frv.enav.esd.ais.AisHandler.AisMessageExtended;
import dk.frv.enav.esd.ais.VesselAisHandler;
import dk.frv.enav.esd.event.DragMouseMode;
import dk.frv.enav.esd.event.NavigationMouseMode;
import dk.frv.enav.esd.event.SelectMouseMode;
import dk.frv.enav.esd.gui.ChartPanel;
import dk.frv.enav.esd.gui.JMapFrame;
import dk.frv.enav.esd.gui.StatusArea;
import dk.frv.enav.esd.layers.msi.MsiInfoPanel;
import dk.frv.enav.esd.layers.wms.WMSInfoPanel;
import dk.frv.enav.esd.nmea.IVesselAisListener;
import dk.frv.enav.ins.ais.VesselPositionData;
import dk.frv.enav.ins.ais.VesselTarget;
import dk.frv.enav.ins.gui.MainFrame;
import dk.frv.enav.ins.layers.ais.AisTargetInfoPanel;

/**
 * The class AisLayer is the layer containing all AIS targets. The class handles
 * the drawing of vessels on the chartPanel.
 * 
 * @author Claes N. Ladefoged, claesnl@gmail.com
 */
public class AisLayer extends OMGraphicHandlerLayer implements Runnable, IVesselAisListener, MapMouseListener {
	private static final long serialVersionUID = 1L;
	private OMGraphicList list = new OMGraphicList();
	private VesselAisHandler aisHandler;
	private List<AisMessageExtended> shipList;
	private ChartPanel chartPanel;
	private HighlightInfoPanel highlightInfoPanel = null;
	private AisTargetInfoPanel aisTargetInfoPanel = new AisTargetInfoPanel();
	private StatusArea statusArea;
	private JMapFrame jMapFrame;

	private HashMap<Long, Vessel> drawnVessels = new HashMap<Long, Vessel>();
	private Vessel vesselComponent;
	private VesselPositionData location;
	private MainFrame mainFrame;
	volatile boolean shouldRun = true;
	private float mapScale = 0;

	OMGraphic highlighted;
	long highlightedMMSI;

	/**
	 * Keeps the AisLayer thread alive
	 */
	@Override
	public void run() {
		while (shouldRun) {
			ESD.sleep(1000);
			drawVessels();
			repaintStatusArea(true);
		}
	}

	/**
	 * Starts the AisLayer thread
	 */
	public AisLayer() {
		(new Thread(this)).start();
	}

	/**
	 * Kills the AisLayer thread
	 */
	public void stop() {
		shouldRun = false;
	}

	/**
	 * Clears all targets from the map and in the local memory
	 */
	public void mapClearTargets() {
		list.clear();
		drawnVessels.clear();
	}

	/**
	 * Draws or updates the vessels on the map
	 */
	private void drawVessels() {
		if (aisHandler != null) {

			if (chartPanel.getMap().getScale() != mapScale) {
				mapScale = chartPanel.getMap().getScale();
				mapClearTargets();
			}
			
			if ((highlightedMMSI != 0 && highlightedMMSI != statusArea.getHighlightedVesselMMSI()) || statusArea.getHighlightedVesselMMSI() == -1){
				System.out.println("Some other panel highlighted a vessel..");
				highlightInfoPanel.setVisible(false);
				highlighted = null;
				highlightedMMSI = 0;
			}

			shipList = aisHandler.getShipList();
			for (int i = 0; i < shipList.size(); i++) {
				if (aisHandler.getVesselTargets().containsKey(shipList.get(i).MMSI)) {

					// Get information
					AisMessageExtended vessel = shipList.get(i);
					VesselTarget vesselTarget = aisHandler.getVesselTargets().get(vessel.MMSI);
					location = vesselTarget.getPositionData();

					double trueHeading = location.getTrueHeading();
					if (trueHeading == 511) {
						trueHeading = location.getCog();
					}

					if (!drawnVessels.containsKey(vessel.MMSI)) {
						vesselComponent = new Vessel(vessel.MMSI);
						list.add(vesselComponent);
						drawnVessels.put(vessel.MMSI, vesselComponent);
					}
					drawnVessels.get(vessel.MMSI).updateLayers(trueHeading, location.getPos().getLatitude(),
							location.getPos().getLongitude(), vesselTarget.getStaticData(), location.getSog(),
							Math.toRadians(location.getCog()), mapScale);
				}
			}
			doPrepare();
		}
	}
	
	private void repaintStatusArea(boolean shouldRepaint){
		System.out.println("Should paint?");
		if(shouldRepaint){
			System.out.println("Yes.. MMSI: "+highlightedMMSI);
			if(highlightedMMSI == 0)
				return;
			System.out.println("Fetching info..");
			HashMap<String, String> info = new HashMap<String, String>();
			Vessel vessel = this.drawnVessels.get(this.highlightedMMSI);
			info.put("MMSI", Long.toString(vessel.getMMSI()));
			info.put("Name", vessel.getName());
			info.put("Heading", vessel.getHeading());
			info.put("Call sign", vessel.getCallSign());
			info.put("Latitude", vessel.getLat());
			info.put("Longitude", vessel.getLon());
			info.put("Sog", vessel.getSog());
			info.put("Eta", vessel.getEta());
			info.put("Destination", vessel.getDest());
			info.put("Ship type", vessel.getShipType());
			statusArea.receiveHighlight(info,vessel.getMMSI());
		} else {
			statusArea.removeHighlight();
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
		if (obj instanceof StatusArea) {
			statusArea = (StatusArea) obj;
		}
		if (obj instanceof JMapFrame) {
			jMapFrame = (JMapFrame) obj;
			highlightInfoPanel = new HighlightInfoPanel();
			jMapFrame.getLoadingPanel().add(highlightInfoPanel);
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
	public boolean mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseClicked(MouseEvent e) {
		OMGraphic newClosest = null;
		OMList<OMGraphic> allClosest = list.findAll(e.getX(), e.getY(), 3.0f);
		for (OMGraphic omGraphic : allClosest) {
			if (omGraphic instanceof VesselLayer) {
				newClosest = omGraphic;
				break;
			}
		}

		if (allClosest.size() == 0) {
			// HIDE GLASS PANE
			highlightedMMSI = 0;
			statusArea.removeHighlight();
			highlightInfoPanel.setVisible(false);
			return false;
		}

		if (newClosest != highlighted) {
			highlighted = newClosest;
			VesselLayer vessel = (VesselLayer) newClosest;
			Vessel ves = drawnVessels.get(vessel.getMMSI());
			highlightedMMSI = vessel.getMMSI();
			Point2D xy = chartPanel.getMap().getProjection().forward(vessel.getLat(), vessel.getLon());
			// MOVE AND SHOW GLASS PANE
			statusArea.setHighlightedVesselMMSI(vessel.getMMSI());
			highlightInfoPanel.displayHighlight((int) xy.getX()-23, (int) xy.getY()-6);
			highlightInfoPanel.setVisible(true);
		} else {
			// HIDE GLASS PANE
			highlightedMMSI = 0;
			statusArea.removeHighlight();
			highlightInfoPanel.setVisible(false);
		}
		doPrepare();
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
		/*
		 * OMGraphic newClosest = null; OMList<OMGraphic> allClosest =
		 * list.findAll(e.getX(), e.getY(), 3.0f); for (OMGraphic omGraphic :
		 * allClosest) { if(omGraphic instanceof VesselLayer){ newClosest =
		 * omGraphic; break; } }
		 * 
		 * if(allClosest.size() == 0){ list.remove(infoBox); }
		 * 
		 * if (newClosest != closest) { list.remove(boxMMSI);
		 * list.remove(boxSog); list.remove(boxCog); list.remove(infoBox);
		 * 
		 * if (newClosest instanceof VesselLayer) { VesselLayer vessel =
		 * (VesselLayer) newClosest; VesselTarget vesselTarget =
		 * aisHandler.getVesselTargets().get(vessel.getMMSI());
		 * 
		 * // Add MouseOverBox location = vesselTarget.getPositionData(); double
		 * lat = location.getPos().getLatitude(); double lon =
		 * location.getPos().getLongitude(); double trueHeading =
		 * location.getTrueHeading(); int[] xpoints = { 10, 10, 200, 200 };
		 * int[] ypoints = { 0, -50, -50, 0 }; infoBox = new OMPoly(lat, lon,
		 * xpoints, ypoints, OMGraphic.DECIMAL_DEGREES); boxMMSI = new
		 * OMText(lat, lon, 15, 0, "MMSI: " + vessel.getMMSI(), font,
		 * OMText.JUSTIFY_LEFT); boxSog = new OMText(lat, lon, 15, 0, "Sog: " +
		 * location.getSog() + " kn", font, OMText.JUSTIFY_LEFT); boxCog = new
		 * OMText(lat, lon, 15, 0, "Cog: " + location.getCog() + " degrees",
		 * font, OMText.JUSTIFY_LEFT); if (trueHeading > 90 && trueHeading <
		 * 270) { int[] ypoints2 = { 0, 50, 50, 0 }; infoBox.setYs(ypoints2);
		 * boxMMSI.setY(15); boxSog.setY(25); boxCog.setY(35); } else {
		 * boxMMSI.setY(-35); boxSog.setY(-25); boxCog.setY(-15); }
		 * infoBox.setFillPaint(new Color(225, 225, 225)); list.add(boxMMSI);
		 * list.add(boxSog); list.add(boxCog); list.add(infoBox); closest =
		 * newClosest; doPrepare(); return true; } } return false;
		 */
		return false;
	}

	@Override
	public void mouseMoved() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
}
