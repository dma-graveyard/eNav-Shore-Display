package dk.frv.enav.esd.layers.wms;

import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.bbn.openmap.image.ImageServerConstants;
import com.bbn.openmap.image.WMTConstants;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMRaster;
import com.bbn.openmap.plugin.WebImagePlugIn;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.plugin.wms.WMSPlugIn;

import dk.frv.enav.esd.ais.VesselAisHandler;
import dk.frv.enav.esd.gui.ChartPanel;
import dk.frv.enav.ins.common.graphics.CenterRaster;
import dk.frv.enav.ins.gui.MainFrame;

// http://kortforsyningen.kms.dk/soe_enc_primar?ignoreillegallayers=TRUE&transparent=TRUE&login=StatSofart&password=114karls&VERSION=1.1.1&REQUEST=GetMap&SRS=EPSG:4326&WIDTH=445&HEIGHT=472&LAYERS=cells&STYLES=style-id-245&TRANSPARENT=TRUE&FORMAT=image/gif&BBOX=612615.5069764,6871781.27364377,622761.062857702,6882542.40257854
// http://kortforsyningen.kms.dk/ftopo?ignoreillegallayers=TRUE&transparent=TRUE&service=WMS&REQUEST=GetMap&SERVICE=WMS&VERSION=1.1.1&LAYERS=ftk_f100&STYLES=&FORMAT=image/png&BGCOLOR=0xFFFFFF&TRANSPARENT=TRUE&SRS=EPSG:32629&BBOX=612615.5069764,6871781.27364377,622761.062857702,6882542.40257854&WIDTH=445&HEIGHT=472&ticket=5e1212b2670a2b1905d01affb02ffaa5

public class WMSService extends WMSPlugIn implements ImageServerConstants {

	private OMGraphicList wmsList = new OMGraphicList();
	private String queryString = "";
	private String bbox;
	private String width;
	private String height;
	private int wmsWidth;
	private int wmsHeight;
	private Double wmsullon;
	private Double wmsullat;
	private Double deltaX = 0.0013;
	private Double deltaY = 0.00058;
	
	public WMSService() {
		super();
		setImageFormat("image/gif");
		setLayers("cells");
		setWmsVersion("1.1.1");
		setStyles("style-id-246");
		setVendorSpecificNames("EPSG");
		setVendorSpecificValues("4326");
//		setVendorSpecificValues("3857");
		setQueryHeader("http://kortforsyningen.kms.dk/soe_enc_primar");
		setTransparent("TRUE");
		
	}
	
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
	

	
	public String getQueryString(){	
//		queryString = "http://kartta.liikennevirasto.fi/meriliikenne/dgds/wms_ip/merikartta?&REQUEST=GetMap&SERVICE=WMS&VERSION=1.1.1&LAYERS=cells&FORMAT=image/gif&TRANSPARENT=true&SRS=EPSG:4326";
//		queryString = queryString + "&BBOX="+bbox		
//				+ "&WIDTH=" + width 
//				+ "&HEIGHT=" + height;
		
		
		queryString = getQueryHeader() 
				+ "?ignoreillegallayers=TRUE" 
				+ "&transparent=" + getTransparent()
				+ "&login=StatSofart"
				+ "&password=114karls"
				+ "&VERSION=" + getWmsVersion()
				+ "&REQUEST=GetMap"
				+ "&SRS=" + getVendorSpecificNames() + ":" + getVendorSpecificValues()
				+ "&BBOX="+bbox				
				+ "&WIDTH=" + width 
				+ "&HEIGHT=" + height
				+ "&LAYERS=" + getLayers()
				+ "&STYLES=" + getStyles() 
				+ "&FORMAT=" + getImageFormat()
				+ "&service=WMS"				
				;

//queryString = "http://kartta.liikennevirasto.fi/meriliikenne/dgds/wms_ip/merikartta?&REQUEST=GetMap&SERVICE=WMS&VERSION=1.1.1&LAYERS=cells&FORMAT=image/gif&TRANSPARENT=true&SRS=EPSG:4326&BBOX=16.8763,58.81432171570781,22.5013,61.60697637138628&WIDTH=256&HEIGHT=260";
//		System.out.println(queryString);
//		this.seti

		
		return queryString;
	}
	

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
			wmsList.add(new CenterRaster(this.wmsullat, this.wmsullon, this.wmsWidth, this.wmsHeight, new ImageIcon(url)));
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

	@Override
	public String getServerName() {
		// TODO Auto-generated method stub
		return null;
	}

}
