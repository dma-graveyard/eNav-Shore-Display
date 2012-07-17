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

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;

import javax.swing.SwingUtilities;

import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMList;

import dk.frv.ais.message.AisMessage;
import dk.frv.enav.esd.ESD;
import dk.frv.enav.esd.ais.AisHandler.AisMessageExtended;
import dk.frv.enav.esd.ais.AisHandler;
import dk.frv.enav.esd.event.DragMouseMode;
import dk.frv.enav.esd.event.NavigationMouseMode;
import dk.frv.enav.esd.event.SelectMouseMode;
import dk.frv.enav.esd.gui.views.ChartPanel;
import dk.frv.enav.esd.gui.views.JMapFrame;
import dk.frv.enav.esd.gui.views.MainFrame;
import dk.frv.enav.esd.gui.views.MapMenu;
import dk.frv.enav.esd.gui.views.StatusArea;
import dk.frv.enav.esd.nmea.IVesselAisListener;
import dk.frv.enav.ins.ais.VesselPositionData;
import dk.frv.enav.ins.ais.VesselTarget;

/**
 * The class AisLayer is the layer containing all AIS targets. The class handles
 * the drawing of vessels on the chartPanel.
 */
public class AisLayer extends OMGraphicHandlerLayer implements Runnable, IVesselAisListener, MapMouseListener {
	private static final long serialVersionUID = 1L;
	private OMGraphicList list = new OMGraphicList();
	private AisHandler aisHandler;
	private List<AisMessageExtended> shipList;
	private ChartPanel chartPanel;
	// private HighlightInfoPanel highlightInfoPanel = null;
	private AisInfoPanel aisInfoPanel = null;
	private StatusArea statusArea;
	private JMapFrame jMapFrame;
	private HashMap<Long, Vessel> drawnVessels = new HashMap<Long, Vessel>();
	private Vessel vesselComponent;
	private VesselPositionData location;
	volatile boolean shouldRun = true;
	private float mapScale = 0;
	private boolean selectionOnScreen = false;
	private IntendedRouteInfoPanel intendedRouteInfoPanel = new IntendedRouteInfoPanel();
	private MapMenu aisTargetMenu = null;

	// private OMGraphic highlighted;
	// private VesselLayer highlightedVessel;

	private OMGraphic closest = null;

	// long highlightedMMSI;

	private MainFrame mainFrame;

	AisTargetGraphic aisTargetGraphic = new AisTargetGraphic();

	/**
	 * Keeps the AisLayer thread alive
	 */
	@Override
	public void run() {

		while (shouldRun) {
			ESD.sleep(1000);
			drawVessels();
		}
		drawnVessels.clear();
		list.clear();
		list.add(aisTargetGraphic);
	}

	/**
	 * Starts the AisLayer thread
	 */
	public AisLayer() {
		list.add(aisTargetGraphic);
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
		list.add(aisTargetGraphic);
		drawnVessels.clear();
	}

	public void removeSelection() {
		aisTargetGraphic.setVisible(false);
		mainFrame.setSelectedMMSI(-1);
		// selectedMMSI = -1;

		statusArea.removeHighlight();

		doPrepare();
	}

	/**
	 * Draws or updates the vessels on the map
	 */
	private void drawVessels() {
		if (aisHandler != null) {

			selectionOnScreen = false;

			if (chartPanel.getMap().getScale() != mapScale) {
				mapScale = chartPanel.getMap().getScale();
				mapClearTargets();
			}

			// if ((highlightedMMSI != 0 && highlightedMMSI !=
			// statusArea.getHighlightedVesselMMSI())
			// || statusArea.getHighlightedVesselMMSI() == -1) {
			// highlightInfoPanel.setVisible(false);
			// highlighted = null;
			// highlightedMMSI = 0;
			// }

			Point2D lr = chartPanel.getMap().getProjection().getLowerRight();
			Point2D ul = chartPanel.getMap().getProjection().getUpperLeft();
			double lrlat = lr.getY();
			double lrlon = lr.getX();
			double ullat = ul.getY();
			double ullon = ul.getX();

			shipList = aisHandler.getShipList();
			for (int i = 0; i < shipList.size(); i++) {
				if (aisHandler.getVesselTargets().containsKey(shipList.get(i).MMSI)) {
					// Get information
					AisMessageExtended vessel = shipList.get(i);
					VesselTarget vesselTarget = aisHandler.getVesselTargets().get(vessel.MMSI);
					location = vesselTarget.getPositionData();

					// Check if vessel is near map coordinates or it's sending
					// an intended route
					boolean t1 = location.getPos().getLatitude() >= lrlat;
					boolean t2 = (location.getPos().getLatitude() <= ullat);
					boolean t3 = location.getPos().getLongitude() >= ullon;
					boolean t4 = location.getPos().getLongitude() <= lrlon;
					;

					if (!(t1 && t2 && t3 && t4)) {

						if (!vesselTarget.hasIntendedRoute()) {
							continue;
						}
					}

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
							Math.toRadians(location.getCog()), mapScale, vesselTarget);

					if (vesselTarget.getMmsi() == mainFrame.getSelectedMMSI()) {
						aisTargetGraphic.moveSymbol(vesselTarget.getPositionData().getPos().getLatitude(), vesselTarget
								.getPositionData().getPos().getLongitude());
						selectionOnScreen = true;

						setStatusAreaTxt();
					}
				}
			}

			if (!selectionOnScreen) {
				aisTargetGraphic.setVisible(false);
			}

			doPrepare();

			// move ship highlight icon
			// if (highlighted != null) {
			// Point2D newXY = chartPanel.getMap().getProjection()
			// .forward(highlightedVessel.getLat(), highlightedVessel.getLon());
			// if (xy != newXY) {
			// xy = newXY;
			// if(jMapFrame.isLocked())
			// highlightInfoPanel.displayHighlight((int) xy.getX() -
			// offsetLockedX, (int) xy.getY() - offsetLockedY);
			// else
			// highlightInfoPanel.displayHighlight((int) xy.getX() -
			// offsetUnlockedX, (int) xy.getY() - offsetUnlockedY);
			// }
			// }

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
		if (obj instanceof AisHandler) {
			aisHandler = (AisHandler) obj;
		}
		if (obj instanceof ChartPanel) {
			chartPanel = (ChartPanel) obj;
		}
		if (obj instanceof StatusArea) {
			statusArea = (StatusArea) obj;
		}
		if (obj instanceof JMapFrame) {
			jMapFrame = (JMapFrame) obj;
			// highlightInfoPanel = new HighlightInfoPanel();
			// jMapFrame.getGlassPanel().add(highlightInfoPanel);
			aisInfoPanel = new AisInfoPanel();
			jMapFrame.getGlassPanel().add(aisInfoPanel);
			jMapFrame.getGlassPanel().add(intendedRouteInfoPanel);
			jMapFrame.getGlassPanel().setVisible(true);
		}
		if (obj instanceof MainFrame) {
			mainFrame = (MainFrame) obj;
		}
		if (obj instanceof MapMenu) {
			aisTargetMenu = (MapMenu) obj;
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
		ret[1] = NavigationMouseMode.modeID; // "ZoomMouseMode"
		ret[2] = SelectMouseMode.modeID; // "SelectMouseMode"
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

			if (omGraphic instanceof VesselLayer || omGraphic instanceof IntendedRouteWpCircle
					|| omGraphic instanceof IntendedRouteLegGraphic) {
				newClosest = omGraphic;
				break;
			}
		}

		if (e.getButton() == MouseEvent.BUTTON1) {

			if (allClosest.size() == 0) {
				removeSelection();
			}

			if (newClosest != null && newClosest instanceof VesselLayer) {

				mainFrame.setSelectedMMSI(((VesselLayer) newClosest).getMMSI());

				aisTargetGraphic.setVisible(true);
				aisTargetGraphic.moveSymbol(((VesselLayer) newClosest).getLat(), ((VesselLayer) newClosest).getLon());
				doPrepare();

				setStatusAreaTxt();

			}

		}

		if (e.getButton() == MouseEvent.BUTTON3 && newClosest != null) {

			if (newClosest instanceof VesselLayer) {

				VesselLayer vesselLayer = (VesselLayer) newClosest;

				aisTargetMenu.aisSuggestedRouteMenu(vesselLayer.getVessel().getVesselTarget());
				aisTargetMenu.setVisible(true);
				aisTargetMenu.show(this, e.getX() - 2, e.getY() - 2);

				return true;

			} else if (newClosest instanceof IntendedRouteWpCircle) {

				IntendedRouteWpCircle wpCircle = (IntendedRouteWpCircle) newClosest;
				VesselTarget vesselTarget = wpCircle.getIntendedRouteGraphic().getVesselTarget();

				aisTargetMenu.aisSuggestedRouteMenu(vesselTarget);
				aisTargetMenu.setVisible(true);
				aisTargetMenu.show(this, e.getX() - 2, e.getY() - 2);

				return true;
			} else if (newClosest instanceof IntendedRouteLegGraphic) {

				IntendedRouteLegGraphic wpCircle = (IntendedRouteLegGraphic) newClosest;
				VesselTarget vesselTarget = wpCircle.getIntendedRouteGraphic().getVesselTarget();
				aisTargetMenu.aisSuggestedRouteMenu(vesselTarget);
				aisTargetMenu.setVisible(true);
				aisTargetMenu.show(this, e.getX() - 2, e.getY() - 2);

				return true;
			}

		}
		return false;
	}

	private void setStatusAreaTxt() {
		HashMap<String, String> info = new HashMap<String, String>();
		Vessel vessel = this.drawnVessels.get(mainFrame.getSelectedMMSI());
		if (vessel != null) {

			info.put("MMSI", Long.toString(vessel.getMMSI()));
			info.put("Name", vessel.getName());
			info.put("COG", vessel.getHeading());
			info.put("Call sign", vessel.getCallSign());
			info.put("LAT", vessel.getLat());
			info.put("LON", vessel.getLon());
			info.put("SOG", vessel.getSog());
			info.put("ETA", vessel.getEta());
			info.put("DST", vessel.getDest());
			info.put("Type", vessel.getShipType());
			statusArea.receiveHighlight(info, vessel.getMMSI());

			statusArea.receiveHighlight(info, vessel.getMMSI());
		}
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
			newClosest = omGraphic;
			break;
		}

		if (allClosest.size() == 0) {
			aisInfoPanel.setVisible(false);
			intendedRouteInfoPanel.setVisible(false);
			closest = null;
			return false;
		}

		if (newClosest != closest) {
			Point containerPoint = SwingUtilities.convertPoint(chartPanel, e.getPoint(), jMapFrame);

			if (newClosest instanceof IntendedRouteWpCircle) {
				closest = newClosest;
				IntendedRouteWpCircle wpCircle = (IntendedRouteWpCircle) newClosest;
				intendedRouteInfoPanel.setPos((int) containerPoint.getX(), (int) containerPoint.getY() - 10);
				intendedRouteInfoPanel.showWpInfo(wpCircle);
			}

			if (newClosest instanceof VesselLayer) {
				jMapFrame.getGlassPane().setVisible(true);
				closest = newClosest;
				VesselLayer vessel = (VesselLayer) newClosest;
				int x = (int) containerPoint.getX() + 10;
				int y = (int) containerPoint.getY() + 10;
				aisInfoPanel.showAisInfo(drawnVessels.get(vessel.getMMSI()));
				if (chartPanel.getMap().getProjection().getWidth() - x < aisInfoPanel.getWidth()) {
					x -= aisInfoPanel.getWidth() + 20;
				}
				if (chartPanel.getMap().getProjection().getHeight() - y < aisInfoPanel.getHeight()) {
					y -= aisInfoPanel.getHeight() + 20;
				}
				aisInfoPanel.setPos(x, y);
				aisInfoPanel.setVisible(true);

				return true;
			}
		}
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
