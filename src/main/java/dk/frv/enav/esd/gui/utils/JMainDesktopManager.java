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
package dk.frv.enav.esd.gui.utils;

import java.awt.Dimension;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import dk.frv.enav.esd.ESD;
import dk.frv.enav.esd.gui.route.RouteManagerDialog;
import dk.frv.enav.esd.gui.views.JMainDesktopPane;
import dk.frv.enav.esd.gui.views.JMapFrame;
import dk.frv.enav.esd.gui.views.JSettingsWindow;
import dk.frv.enav.esd.gui.views.NotificationArea;
import dk.frv.enav.esd.gui.views.NotificationCenter;
import dk.frv.enav.esd.gui.views.SendRouteDialog;
import dk.frv.enav.esd.gui.views.StatusArea;
import dk.frv.enav.esd.gui.views.ToolBar;

public class JMainDesktopManager extends DefaultDesktopManager {
	/**
	 * Desktopmanager used in controlling windows
	 */
	private static final long serialVersionUID = 1L;
	private JMainDesktopPane desktop;
	private HashMap<Integer, JInternalFrame> toFront;
	private ToolBar toolbar;
	private NotificationCenter notCenter;
	private NotificationArea notificationArea;
	private StatusArea statusArea;
	private JSettingsWindow settings;
	private RouteManagerDialog routeManager;
	private SendRouteDialog routeDialog;
	
	/**
	 * Constructor for desktopmanager
	 * @param desktop
	 */
	public JMainDesktopManager(JMainDesktopPane desktop) {
		this.desktop = desktop;
		toFront = new HashMap<Integer, JInternalFrame>();
	}

	/**
	 * Activate a frame and handle the ordering
	 */
	public void activateFrame(JInternalFrame f) {

		if (f instanceof JMapFrame) {

			if (ESD.getMainFrame() != null){
			ESD.getMainFrame().setActiveMapWindow((JMapFrame) f);
			}
			
			if (toFront.size() == 0) {
				super.activateFrame(f);
			} else {
				if (toFront.containsKey(((JMapFrame) f).getId())) {
					super.activateFrame(f);
				} else {
					super.activateFrame(f);
					Iterator<Map.Entry<Integer, JInternalFrame>> it = toFront.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<Integer, JInternalFrame> pairs = it.next();
						super.activateFrame(pairs.getValue());
					}
				}
			}

		}
		super.activateFrame(statusArea);
		super.activateFrame(notificationArea);
		super.activateFrame(toolbar);
		super.activateFrame(notCenter);
		super.activateFrame(settings);
		super.activateFrame(routeManager);
		super.activateFrame(routeDialog);
	}
	
	public void clearToFront(){
		toFront.clear();
	}

	/**
	 * Set an internalframe to be infront
	 * @param id
	 * @param f
	 */
	public void addToFront(int id, JInternalFrame f) {
		if (toFront.containsKey(id)) {
			toFront.remove(id);
		} else {
			toFront.put(id, f);
		}
	}

	/**
	 * Dragging ended of component
	 */
	public void endDraggingFrame(JComponent f) {
		super.endDraggingFrame(f);
		resizeDesktop();
	}

	/**
	 * Resizing ended of component
	 */
	public void endResizingFrame(JComponent f) {
		super.endResizingFrame(f);
		resizeDesktop();
	}

	/**
	 * return the scrollPane
	 * @return
	 */
	private JScrollPane getScrollPane() {
		if (desktop.getParent() instanceof JViewport) {
			JViewport viewPort = (JViewport) desktop.getParent();
			if (viewPort.getParent() instanceof JScrollPane)
				return (JScrollPane) viewPort.getParent();
		}
		return null;
	}

	/**
	 * Get scrollPane insets
	 * @return
	 */
	private Insets getScrollPaneInsets() {
		JScrollPane scrollPane = getScrollPane();
		if (scrollPane == null)
			return new Insets(0, 0, 0, 0);
		else
			return getScrollPane().getBorder().getBorderInsets(scrollPane);
	}

	/**
	 * Resize desktop
	 */
	public void resizeDesktop() {
		int x = 0;
		int y = 0;
		JScrollPane scrollPane = getScrollPane();
		Insets scrollInsets = getScrollPaneInsets();

		if (scrollPane != null) {
			JInternalFrame allFrames[] = desktop.getAllFrames();
			for (int i = 0; i < allFrames.length; i++) {
				if (allFrames[i].getX() + allFrames[i].getWidth() > x) {
					x = allFrames[i].getX() + allFrames[i].getWidth();
				}
				if (allFrames[i].getY() + allFrames[i].getHeight() > y) {
					y = allFrames[i].getY() + allFrames[i].getHeight();
				}
			}
			Dimension d = scrollPane.getVisibleRect().getSize();
			if (scrollPane.getBorder() != null) {
				d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right, d.getHeight() - scrollInsets.top
						- scrollInsets.bottom);
			}

			if (x <= d.getWidth())
				x = ((int) d.getWidth()) - 20;
			if (y <= d.getHeight())
				y = ((int) d.getHeight()) - 20;
			desktop.setAllSize(x, y);
			scrollPane.invalidate();
			scrollPane.validate();
		}
	}

	/**
	 * set normal size
	 */
	public void setNormalSize() {
		JScrollPane scrollPane = getScrollPane();
		int x = 0;
		int y = 0;
		Insets scrollInsets = getScrollPaneInsets();

		if (scrollPane != null) {
			Dimension d = scrollPane.getVisibleRect().getSize();
			if (scrollPane.getBorder() != null) {
				d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right, d.getHeight() - scrollInsets.top
						- scrollInsets.bottom);
			}

			d.setSize(d.getWidth() - 20, d.getHeight() - 20);
			desktop.setAllSize(x, y);
			scrollPane.invalidate();
			scrollPane.validate();
		}
	}

	/**
	 * Set notification center
	 * @param notCenter
	 */
	public void setNotCenter(NotificationCenter notCenter) {
		this.notCenter = notCenter;
	}
	
	/**
	 * Set Settings Window
	 * @param notCenter
	 */
	public void setSettings(JSettingsWindow settings) {
		this.settings = settings;
	}
	
	/**
	 * Set RouteManager Window
	 * @param notCenter
	 */
	public void setRouteManager(RouteManagerDialog routeManager) {
		this.routeManager = routeManager;
	}

	/**
	 * Set RouteExchange Dialog
	 * @param notCenter
	 */
	public void setRouteExchangeDialog(SendRouteDialog routeDialog) {
		this.routeDialog = routeDialog;
	}

	/**
	 * Set notification area
	 * @param notificationArea
	 */
	public void setNotificationArea(NotificationArea notificationArea) {
		this.notificationArea = notificationArea;
	}

	/**
	 * Set status area
	 * @param statusArea
	 */
	public void setStatusArea(StatusArea statusArea) {
		this.statusArea = statusArea;
	}

	/**
	 * Set toolbar
	 * @param toolbar
	 */
	public void setToolbar(ToolBar toolbar) {
		this.toolbar = toolbar;
	}
}