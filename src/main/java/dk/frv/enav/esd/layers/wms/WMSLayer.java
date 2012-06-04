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
package dk.frv.enav.esd.layers.wms;

import sun.awt.image.PixelConverter.Bgrx;

import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.frv.enav.esd.ESD;
import dk.frv.enav.esd.gui.ChartPanel;
import dk.frv.enav.esd.gui.JMapFrame;
import dk.frv.enav.esd.gui.MainFrame;

/**
 * Layer handling all WMS data and displaying of it
 * 
 * @author David A. Camre (davidcamre@gmail.com)
 * 
 */
public class WMSLayer extends OMGraphicHandlerLayer implements Runnable {
	private static final long serialVersionUID = 1L;
	private OMGraphicList list = new OMGraphicList();
	private ChartPanel chartPanel;
	private WMSInfoPanel wmsInfoPanel = null;
	private JMapFrame jMapFrame;
	private MainFrame mainFrame;

	volatile boolean shouldRun = true;
	private WMSService wmsService;
	private Double upperLeftLon = 0.0;
	private Double upperLeftLat = 0.0;
	private Double lowerRightLon = 0.0;
	private Double lowerRightLat = 0.0;
	private int height = -1;
	private int width = -1;

	/**
	 * Constructor that starts the WMS layer in a seperate thread
	 */
	public WMSLayer() {
		wmsService = new WMSService();
		(new Thread(this)).start();

	}

	public WMSService getWmsService() {
		return wmsService;
	}

	/**
	 * Draw the WMS onto the map
	 * 
	 * @param list
	 *            of elements to be drawn
	 */
	public void drawWMS(OMGraphicList list) {
		this.list.clear();
		this.list.add(list);
		// wmsInfoPanel.setVisible(false);

		if (wmsService.isWmsImage() && this.isVisible()) {
			jMapFrame.getChartPanel().getBgLayer().setVisible(false);
		} else {
			jMapFrame.getChartPanel().getBgLayer().setVisible(true);
		}

		doPrepare();
	}

	@Override
	public void findAndInit(Object obj) {
		if (obj instanceof ChartPanel) {
			chartPanel = (ChartPanel) obj;
			// chartPanel.getMapHandler().addPropertyChangeListener("WMS", pcl)
		}
		if (obj instanceof JMapFrame) {
			jMapFrame = (JMapFrame) obj;
			wmsInfoPanel = new WMSInfoPanel();
			jMapFrame.getGlassPanel().add(wmsInfoPanel);
			wmsInfoPanel.setPos(20, 30);
		}
		if (obj instanceof MainFrame){
			mainFrame = (MainFrame) obj;
		}

	}

	@Override
	public synchronized OMGraphicList prepare() {
		list.project(getProjection());
		return list;
	}

	@Override
	public void run() {
		while (shouldRun) {
			ESD.sleep(1000);
			
//			if (this.isVisible() && jMapFrame.getWidth() > 0 && jMapFrame.getWidth() > 0 && chartPanel.getMap().getScale() <= 3428460) {
			if (mainFrame.isWmsLayerEnabled() && jMapFrame.getWidth() > 0 && jMapFrame.getWidth() > 0 && chartPanel.getMap().getScale() <= 3428460) {
				setVisible(true);
				chartPanel.getBgLayer().setVisible(false);
				
				// if (height != chartPanel.getMap().getHeight() || width !=
				// chartPanel.getMap().getWidth()){
				// wmsInfoPanel.setPos( (jMapFrame.getChartPanel().getHeight() /
				// 2) -50, (jMapFrame.getChartPanel().getWidth() / 2) - 50);
				// }

				if (upperLeftLon != chartPanel.getMap().getProjection().getUpperLeft().getX()
						|| upperLeftLat != chartPanel.getMap().getProjection().getUpperLeft().getY()
						|| lowerRightLon != chartPanel.getMap().getProjection().getLowerRight().getX()
						|| lowerRightLat != chartPanel.getMap().getProjection().getLowerRight().getY()
						|| width != chartPanel.getMap().getWidth() || height != chartPanel.getMap().getHeight()) {

					// System.out.println("New request");
					// wmsInfoPanel.showText("Loading");
					// System.out.println(jMapFrame.getHeight());
					// System.out.println(jMapFrame.getWidth());

					wmsInfoPanel.displayLoadingImage();
					// wmsInfoPanel.setVisible(true);

					jMapFrame.getGlassPanel().setVisible(true);

					upperLeftLon = chartPanel.getMap().getProjection().getUpperLeft().getX();
					upperLeftLat = chartPanel.getMap().getProjection().getUpperLeft().getY();
					lowerRightLon = chartPanel.getMap().getProjection().getLowerRight().getX();
					lowerRightLat = chartPanel.getMap().getProjection().getLowerRight().getY();

					width = chartPanel.getMap().getWidth();
					height = chartPanel.getMap().getHeight();

//					System.out.println(height);
//					System.out.println(width);

					// System.out.println(chartPanel.getMap().getProjection().forward(chartPanel.getMap().getProjection().getLowerRight()));
					// System.out.println(upperLeftLon);
					// System.out.println(upperLeftLat);
					// System.out.println(lowerRightLon);
					// System.out.println(lowerRightLat);

					wmsService.setZoomLevel(chartPanel.getMap().getScale());
					wmsService.setWMSPosition(chartPanel.getMap().getProjection().getCenter().getX(), chartPanel
							.getMap().getProjection().getCenter().getY(), upperLeftLon, upperLeftLat, lowerRightLon,
							lowerRightLat, width, height);

					drawWMS(wmsService.getWmsList());
					wmsInfoPanel.setVisible(false);
				}
			}else{
				this.setVisible(false);
				chartPanel.getBgLayer().setVisible(true);
			}
		}
	}

	/**
	 * Stop the thread
	 */
	public void stop() {
		shouldRun = false;
	}

}
