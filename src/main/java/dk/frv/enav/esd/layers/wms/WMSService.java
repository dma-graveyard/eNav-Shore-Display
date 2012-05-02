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

	private ChartPanel chartPanel;
	private OMGraphicList wmsList = new OMGraphicList();

	public WMSService() {

		// chartPanel.getMap()
		super();
		// WMSPlugIn wms = new WMSPlugIn();
		setImageFormat("image/png");
		setLayers("cells");
		setWmsVersion("1.1.1");
		setStyles("style-id-245");
		setVendorSpecificNames("EPSG");
		setVendorSpecificValues("4326");
		setQueryHeader("http://kortforsyningen.kms.dk/soe_enc_primar");
		setTransparent("TRUE");

		// Projection p = bean.getProjection();

		String height = "undefined";
		String width = "undefined";

		/*
		 * if (p != null) { bbox =
		 * Double.toString(p.getUpperLeft().getLongitude()) + "," +
		 * Double.toString(p.getLowerRight().getLatitude()) + "," +
		 * Double.toString(p.getLowerRight().getLongitude()) + "," +
		 * Double.toString(p.getUpperLeft().getLatitude()); height =
		 * Integer.toString(p.getHeight()); width =
		 * Integer.toString(p.getWidth()); }
		 */
		width = "1680";
		height = "1050";

		/*
		 * http://kortforsyningen.kms.dk/soe_enc_primar?ignoreillegallayers=TRUE
		 * &transparent=TRUE &login=StatSofart &password=114karls &VERSION=1.1.1
		 * &REQUEST=GetMap &SRS=EPSG:4326 &WIDTH=1680 &HEIGHT=1050 &LAYERS=cells
		 * &STYLES=style-id-245 &TRANSPARENT=TRUE &FORMAT=image/png
		 */

		String queryString = getQueryHeader() + "?ignoreillegallayers=TRUE" + "&transparent=" + getTransparent()
				+ "&login=StatSofart" + "&password=114karls" + "&VERSION=" + getWmsVersion() + "&REQUEST=GetMap"
				+ "&SRS=" + getVendorSpecificNames() + ":" + getVendorSpecificValues() + "&WIDTH=" + width + "&HEIGHT="
				+ height + "&LAYERS=" + getLayers() + "&STYLES=" + getStyles() + "&FORMAT=" + getImageFormat();

		// System.out.println(wms.createQueryString(bean.getProjection()));

		queryString = "http://kortforsyningen.kms.dk/ftopo?ignoreillegallayers=TRUE&transparent=TRUE&service=WMS&REQUEST=GetMap&SERVICE=WMS&VERSION=1.1.1&LAYERS=ftk_f100&STYLES=&FORMAT=image/png&BGCOLOR=0xFFFFFF&TRANSPARENT=TRUE&SRS=EPSG:32629&BBOX=612615.5069764,6871781.27364377,622761.062857702,6882542.40257854&WIDTH=445&HEIGHT=472&ticket=5e1212b2670a2b1905d01affb02ffaa5";
		System.out.println(queryString);
		java.net.URL url = null;

		try {
			System.out.println("Inside URL code");
			url = new java.net.URL(queryString);

			java.net.HttpURLConnection urlc = (java.net.HttpURLConnection) url.openConnection();

			urlc.setRequestProperty("User-Agent",
					"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.1.14) Gecko/20080404 Firefox/2.0.0.14");

			System.out.println("Using Proxy: " + urlc.usingProxy());

			urlc.setDoInput(true);
			urlc.setDoOutput(true);
			urlc.setRequestMethod("GET");

			// Check if correct image found with urlc.getContentType()
			urlc.disconnect();
			ImageIcon ii = new ImageIcon(url);
			CenterRaster wmsImage = new CenterRaster(55.6760968, 12.568337, 445, 472, ii);
			wmsList.add(wmsImage);

		} catch (java.net.MalformedURLException murle) {
			System.out.println("Bad URL!");
		} catch (java.io.IOException ioe) {
			System.out.println("IO Exception");
		}


	}

	/*
	 * @Override public void findAndInit(Object obj) { if (obj instanceof
	 * ChartPanel) { chartPanel = (ChartPanel) obj; } }
	 */

	public OMGraphicList getWmsList() {
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
