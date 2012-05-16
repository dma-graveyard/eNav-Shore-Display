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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import com.bbn.openmap.image.ImageServerConstants;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.plugin.wms.WMSPlugIn;
import com.bbn.openmap.proj.Projection;

import dk.frv.enav.esd.ESD;
import dk.frv.enav.ins.common.graphics.CenterRaster;


public class WMSService extends WMSPlugIn implements ImageServerConstants {

	private OMGraphicList wmsList = new OMGraphicList();
	private String wmsQuery = "";
	private String bbox;
	private String width;
	private String height;
	private int wmsWidth;
	private int wmsHeight;
	private Double wmsullon;
	private Double wmsullat;
//	private Double deltaX = 0.0013;
//	private Double deltaY = 0.00058;
	private Double deltaX = 0.00;
	private Double deltaY = 0.00;
	private boolean wmsImage;
	
	/**
	 * Constructor for the WMS Service - loads the WMS server from the settings file
	 */
	public WMSService() {
		super();
		wmsQuery = ESD.getSettings().getGuiSettings().getWmsQuery();
	}
	
	/**
	 * Set the position of the WMS image and what area we wish to display
	 * @param ullon
	 * @param ullat
	 * @param upperLeftLon
	 * @param upperLeftLat
	 * @param lowerRightLon
	 * @param lowerRightLat
	 * @param w
	 * @param h
	 */
	public void setWMSPosition(Double ullon, Double ullat, Double upperLeftLon, Double upperLeftLat, Double lowerRightLon, Double lowerRightLat, int w, int h){
		this.wmsWidth = w;
		this.wmsHeight = h;
		this.wmsullon = ullon;
		this.wmsullat = ullat;
		//Because finished education and 10 years of experince we know to add the delta values
		this.bbox = Double.toString(upperLeftLon + deltaX) + "," +
				  Double.toString(lowerRightLat + deltaY) + "," +
				  Double.toString(lowerRightLon + deltaX) + "," +
				  Double.toString(upperLeftLat + deltaY);
		this.width = Integer.toString(w);
		this.height = Integer.toString(h);
	}
	

	/**
	 * Get the generated WMS query
	 * @return
	 */
	public String getQueryString(){	
		String queryString = wmsQuery
				+ "&BBOX="+bbox				
				+ "&WIDTH=" + width 
				+ "&HEIGHT=" + height;
				
			
		
		System.out.println(queryString);
		
		return queryString;
	}
	

	/**
	 * After the query has been generated this completes it and returns a OMGraphiclist of the graphics
	 * @return
	 */
	public OMGraphicList getWmsList() {
		
		java.net.URL url = null;
		try {
			url = new java.net.URL(getQueryString());
			java.net.HttpURLConnection urlc = (java.net.HttpURLConnection) url.openConnection();
			urlc.setRequestProperty("User-Agent","Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.1.14) Gecko/20080404 Firefox/2.0.0.14");
			urlc.setDoInput(true);
			urlc.setDoOutput(true);
			urlc.setRequestMethod("GET");
			urlc.disconnect();
			wmsList.clear();
			//wmsList.add(new CenterRaster(55.6760968, 12.568337, 445, 472, new ImageIcon(url)));
			ImageIcon wmsImg = new ImageIcon(url);
			
			if (wmsImg.getIconHeight() == -1 || wmsImg.getIconWidth() ==-1){
//				System.out.println("no WMS");
				Image noImage = (new ImageIcon("images/noWMSAvailable.png")).getImage();
				BufferedImage bi = new BufferedImage(noImage.getWidth(null), noImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
				Graphics g = bi.createGraphics();
				g.drawImage(noImage, 0, 0, wmsWidth, wmsHeight, null, null);  
				ImageIcon noImageIcon = new ImageIcon(bi);  	
				wmsList.add(new CenterRaster(this.wmsullat, this.wmsullon, this.wmsWidth, this.wmsHeight, noImageIcon));
				wmsImage = false;
			}else{
				wmsList.add(new CenterRaster(this.wmsullat, this.wmsullon, this.wmsWidth, this.wmsHeight, wmsImg));
				wmsImage = true;
			}
			
			
			
//			System.out.println(wmsImg.getIconHeight());
//			System.out.println(wmsImg.getIconWidth());
			//If iconHeight or width == -1 no WMS available
			
		} catch (java.net.MalformedURLException murle) {
			System.out.println("Bad URL!");
		} catch (java.io.IOException ioe) {
			System.out.println("IO Exception");
		}
		return wmsList;
	}

	@Override
	public String createQueryString(Projection arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isWmsImage() {
		return wmsImage;
	}

	@Override
	public String getServerName() {
		// TODO Auto-generated method stub
		return null;
	}

}
