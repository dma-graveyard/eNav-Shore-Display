package dk.frv.enav.esd.layers.wms;

import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.frv.enav.esd.ESD;
import dk.frv.enav.esd.gui.ChartPanel;
import dk.frv.enav.esd.gui.JMapFrame;

public class WMSLayer extends OMGraphicHandlerLayer implements Runnable {
	private static final long serialVersionUID = 1L;
	private OMGraphicList list = new OMGraphicList();
	private ChartPanel chartPanel;
	private WMSInfoPanel wmsInfoPanel = null;
	private JMapFrame jMapFrame;

	
	volatile boolean shouldRun = true;
	private WMSService wmsService;
	private Double upperLeftLon = 0.0;
	private Double upperLeftLat = 0.0;
	private Double lowerRightLon = 0.0;
	private Double lowerRightLat = 0.0;
	private int height = -1;
	private int width = -1;

	@Override
	public void run() {
		while (shouldRun) {
			ESD.sleep(700);

			// Check is changed
			if (upperLeftLon != chartPanel.getMap().getProjection().getUpperLeft().getX()
					|| upperLeftLat != chartPanel.getMap().getProjection().getUpperLeft().getY()
					|| lowerRightLon != chartPanel.getMap().getProjection().getLowerRight().getX()
					|| lowerRightLat != chartPanel.getMap().getProjection().getLowerRight().getY()
					|| width != chartPanel.getMap().getWidth() || height != chartPanel.getMap().getHeight()) {

				System.out.println("New request");
//				wmsInfoPanel.showText("Loading");
				wmsInfoPanel.displayLoadingImage();
//				wmsInfoPanel.setVisible(true);
				wmsInfoPanel.setPos(jMapFrame.getChartPanel().getHeight()/2 - 50,jMapFrame.getChartPanel().getWidth()/2 - 50);
				jMapFrame.getGlassPanel().setVisible(true);
				upperLeftLon = chartPanel.getMap().getProjection().getUpperLeft().getX();
				upperLeftLat = chartPanel.getMap().getProjection().getUpperLeft().getY();
				lowerRightLon = chartPanel.getMap().getProjection().getLowerRight().getX();
				lowerRightLat = chartPanel.getMap().getProjection().getLowerRight().getY();

				width = chartPanel.getMap().getWidth();
				height = chartPanel.getMap().getHeight();

				// System.out.println(chartPanel.getMap().getProjection().forward(chartPanel.getMap().getProjection().getLowerRight()));
				// System.out.println(upperLeftLon);
				// System.out.println(upperLeftLat);
				// System.out.println(lowerRightLon);
				// System.out.println(lowerRightLat);

				wmsService.setWMSPosition(chartPanel.getMap().getProjection().getCenter().getX(), chartPanel.getMap()
						.getProjection().getCenter().getY(), upperLeftLon, upperLeftLat, lowerRightLon, lowerRightLat,
						width, height);
				drawWMS(wmsService.getWmsList());
			}
		}
	}

	public WMSLayer() {
		wmsService = new WMSService();
		(new Thread(this)).start();

	}

	public void stop() {
		shouldRun = false;
	}

	public void drawWMS(OMGraphicList list) {
		this.list.clear();
		this.list.add(list);
//		wmsInfoPanel.setVisible(false);
		doPrepare();
		jMapFrame.getGlassPanel().setVisible(false);
	}

	@Override
	public synchronized OMGraphicList prepare() {
		list.project(getProjection());
		return list;
	}

	@Override
	public void findAndInit(Object obj) {
		if (obj instanceof ChartPanel) {
			chartPanel = (ChartPanel) obj;
			// chartPanel.getMapHandler().addPropertyChangeListener("WMS", pcl)
		}
		if (obj instanceof JMapFrame){
			jMapFrame = (JMapFrame) obj;
			wmsInfoPanel = new WMSInfoPanel();
			jMapFrame.getGlassPanel().add(wmsInfoPanel);
		}		

	}

}
