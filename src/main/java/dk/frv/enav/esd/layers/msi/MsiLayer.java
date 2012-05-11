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
package dk.frv.enav.esd.layers.msi;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.List;

import javax.swing.SwingUtilities;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMList;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.common.xml.msi.MsiLocation;
import dk.frv.enav.common.xml.msi.MsiMessage;
import dk.frv.enav.esd.event.DragMouseMode;
import dk.frv.enav.esd.event.NavigationMouseMode;
import dk.frv.enav.esd.event.SelectMouseMode;
import dk.frv.enav.esd.gui.JMapFrame;
import dk.frv.enav.esd.msi.MsiHandler;
import dk.frv.enav.ins.gps.GnssTime;

/**
 * Layer handling all msi messages
 *
 */
public class MsiLayer extends OMGraphicHandlerLayer implements MapMouseListener {	
	private static final long serialVersionUID = 1L;

	private MsiHandler msiHandler = null;
	
	private OMGraphicList graphics = new OMGraphicList();
	private MapBean mapBean = null;
	private JMapFrame jMapFrame = null;
	
	private OMGraphic closest = null;
	private MsiInfoPanel msiInfoPanel = null;	
	
	/**
	 * Constructor for the layer
	 */
	public MsiLayer() {
		
	}

	/**
	 * Call an update on messages if something has changed
	 */
	public void doUpdate() {
		graphics.clear();
		Date now = GnssTime.getInstance().getDate();
		// Get messages
		List<MsiHandler.MsiMessageExtended> messages = msiHandler.getMessageList();
		for (MsiHandler.MsiMessageExtended message : messages) {
			
			// Not able to show messages without location
			if (!message.msiMessage.hasLocation()) {
				continue;
			}
			
			// Is it valid now
			if (!message.isValidAt(now)) {
				continue;
			}
			
			// Create MSI graphic
			MsiGraphic msiGraphic = new MsiGraphic(message);
			graphics.add(msiGraphic);
			
			if(mapBean != null && message.relevant){
				MsiDirectionalIcon direction = new MsiDirectionalIcon(mapBean);
				direction.setMarker(message);
				graphics.add(direction);
			}
		}
		doPrepare();
	}
	
	
	@Override
	public void findAndInit(Object obj) {
		if (obj instanceof MsiHandler) {
			msiHandler = (MsiHandler)obj;
		}
		if (obj instanceof MapBean){
			mapBean = (MapBean)obj;
		}
		if (obj instanceof JMapFrame){
			jMapFrame = (JMapFrame) obj;
			msiInfoPanel = new MsiInfoPanel();
			jMapFrame.getGlassPanel().add(msiInfoPanel);
		}		
	}
	
	public MapMouseListener getMapMouseListener() {
        return this;
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
	public boolean mouseClicked(MouseEvent e) {
		if(e.getButton() != MouseEvent.BUTTON3){
			return false;
		}
		return false;
	}
	
	@Override
	public boolean mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
//		if(mouseDelegator.getActiveMouseModeID() == RouteEditMouseMode.modeID) {
//			mousePosition = null;
//			doUpdate();
//		}
	}

	@Override
	public void mouseMoved() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean mouseMoved(MouseEvent e) {
		
		// Show description on hover
		OMGraphic newClosest = null;
		OMList<OMGraphic> allClosest = graphics.findAll(e.getX(), e.getY(), 3.0f);
		
		for (OMGraphic omGraphic : allClosest) {
			if (omGraphic instanceof MsiSymbolGraphic || omGraphic instanceof MsiDirectionalIcon) {
				newClosest = omGraphic;
				break;
			}
		}
		
		if (newClosest != closest) {
			Point containerPoint = SwingUtilities.convertPoint(mapBean, e.getPoint(), jMapFrame);
			if (newClosest instanceof MsiSymbolGraphic) {
				closest = newClosest;
				MsiSymbolGraphic msiSymbolGraphic = (MsiSymbolGraphic)newClosest;
				msiInfoPanel.setPos((int)containerPoint.getX(), (int)containerPoint.getY() - 10);
				msiInfoPanel.showMsiInfo(msiSymbolGraphic.getMsiMessage());
				jMapFrame.getGlassPanel().setVisible(true);
				return true;
			} else if (newClosest instanceof MsiDirectionalIcon) {
				closest = newClosest;
				jMapFrame.getGlassPanel().setVisible(true);
				return true;
			} else {
				msiInfoPanel.setVisible(false);
				jMapFrame.getGlassPanel().setVisible(false);
				closest = null;
				return false;				
			}
		}
		return false;
	}

	@Override
	public boolean mousePressed(MouseEvent arg0) {
		return false;
	}

	@Override
	public boolean mouseReleased(MouseEvent e) {

		return false;
	}

	@Override
	public synchronized OMGraphicList prepare() {

		graphics.project(getProjection());
		return graphics;
	}

	/**
	 * Move and center the map around a specific msi message
	 * @param msiMessage
	 */
	public void zoomTo(MsiMessage msiMessage) {		
		if (!msiMessage.hasLocation()) {
			return;
		}
		
		MsiLocation msiLocation = msiMessage.getLocation();
		GeoLocation center = msiLocation.getCenter();
		mapBean.setCenter(center.getLatitude(), center.getLongitude());
		mapBean.setScale(80000);		
	}	

}
