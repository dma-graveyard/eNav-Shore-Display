package dk.frv.enav.esd.layers.wms;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
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

public class WMSLayer extends OMGraphicHandlerLayer implements Runnable {
	private static final long serialVersionUID = 1L;
	private OMGraphicList list = new OMGraphicList();
	private VesselAisHandler aisHandler;
	private List<AisMessageExtended> shipList;
	private ChartPanel chartPanel;
	private AisTargetInfoPanel aisTargetInfoPanel = new AisTargetInfoPanel();

	private MainFrame mainFrame;
	volatile boolean shouldRun = true;
	private WMSService wmsService;

	@Override
	public void run() {
		while (shouldRun) {
			ESD.sleep(1500);
			Double upperLeftLon = chartPanel.getMap().getProjection().getUpperLeft().getX();
			Double upperLeftLat = chartPanel.getMap().getProjection().getUpperLeft().getY();
			Double lowerRightLon = chartPanel.getMap().getProjection().getLowerRight().getX();
			Double lowerRightLat = chartPanel.getMap().getProjection().getLowerRight().getY();
			
			
//			System.out.println(chartPanel.getMap().getProjection().forward(chartPanel.getMap().getProjection().getLowerRight()));
//			System.out.println(upperLeftLon);
//			System.out.println(upperLeftLat);
//			System.out.println(lowerRightLon);
//			System.out.println(lowerRightLat);
			
			int w = chartPanel.getMap().getWidth();
			int h = chartPanel.getMap().getHeight();
			wmsService.setWMSPosition(chartPanel.getMap().getProjection().getCenter().getX(),chartPanel.getMap().getProjection().getCenter().getY(),upperLeftLon,upperLeftLat,lowerRightLon,lowerRightLat,w,h);
			drawWMS(wmsService.getWmsList());
		}
	}

	public WMSLayer() {
		wmsService = new WMSService();
		(new Thread(this)).start();
		
	}

	public void stop() {
		shouldRun = false;
	}

	public void drawWMS(OMGraphicList list){
		this.list.clear();
		this.list.add(list);
		doPrepare();
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
		if (obj instanceof MainFrame) {
			mainFrame = (MainFrame) obj;
			mainFrame.getGlassPanel().add(aisTargetInfoPanel);
		}

	}


}
