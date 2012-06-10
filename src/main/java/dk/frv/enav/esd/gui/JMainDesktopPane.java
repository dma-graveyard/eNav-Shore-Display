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
package dk.frv.enav.esd.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyVetoException;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

public class JMainDesktopPane extends JDesktopPane {
	/**
	 * DesktopPane used for internalframes
	 */
	private static final long serialVersionUID = 1L;

	private static int FRAME_OFFSET = 20;

	private JMainDesktopManager manager;
	private MainFrame mainFrame;

	/**
	 * Initialize the desktop pane
	 * @param mainFrame
	 */
	public JMainDesktopPane(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		manager = new JMainDesktopManager(this);
		setDesktopManager(manager);
		setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
	}

	// public void setBounds(int x, int y, int w, int h) {
	// super.setBounds(x, y, w, h);
	// checkDesktopSize();
	// }

	/**
	 * Add a component
	 * @param frame element to be added
	 * @return
	 */
	public Component add(JInternalFrame frame) {
		JInternalFrame[] array = getAllFrames();
		Point p;
		int w;
		int h;

		Component retval = super.add(frame);
		// checkDesktopSize();

		if (array.length > 0) {
			p = array[0].getLocation();
			p.x = p.x + FRAME_OFFSET;
			p.y = p.y + FRAME_OFFSET;
		} else {
			p = new Point(0, 0);
		}
		frame.setLocation(p.x, p.y);
		if (frame.isResizable()) {

			w = getWidth() - (getWidth() / 3);
			h = getHeight() - (getHeight() / 3);
			// System.out.println(getWidth());
			// System.out.println(getHeight());
			if (w < frame.getMinimumSize().getWidth())
				w = (int) frame.getMinimumSize().getWidth();
			if (h < frame.getMinimumSize().getHeight())
				h = (int) frame.getMinimumSize().getHeight();

			if (w > 700) {
				w = 400;
			}
			if (h > 700) {
				h = 400;
			}

			frame.setSize(w, h);

		}
		moveToFront(frame);
		frame.setVisible(true);
		try {
			frame.setSelected(true);
		} catch (PropertyVetoException e) {
			frame.toBack();
		}

		return retval;
	}

	/**
	 * Added a window that comes from a workspace
	 * @param frame to be added
	 * @param workspaceWindow indicates if it is loaded from a workspace or not
	 * @return
	 */
	public Component add(JInternalFrame frame, boolean workspaceWindow) {
		Component retval = super.add(frame);
		// checkDesktopSize();

		moveToFront(frame);
		// frame.setVisible(true);
		try {
			frame.setSelected(true);
		} catch (PropertyVetoException e) {
			frame.toBack();
		}
		return retval;
	}

	/**
	 * Cascade all internal frames
	 */
	public void cascadeFrames() {
		int x = 0;
		int y = 0;
		JInternalFrame allFrames[] = getAllFrames();

		// manager.setNormalSize();
		int frameHeight = (getBounds().height - 5) - allFrames.length * FRAME_OFFSET;
		int frameWidth = (getBounds().width - 5) - allFrames.length * FRAME_OFFSET;
		for (int i = allFrames.length - 1; i >= 0; i--) {
			allFrames[i].setSize(frameWidth, frameHeight);
			allFrames[i].setLocation(x, y);
			x = x + FRAME_OFFSET;
			y = y + FRAME_OFFSET;
		}
	}

	/**
	 * Return the JMainDesktopManager
	 * @return manager
	 */
	public JMainDesktopManager getManager() {
		return manager;
	}

	/**
	 * Function called when one of its components are closed
	 */
	public void remove(Component c) {

		if (c instanceof JMapFrame) {
			mainFrame.removeMapWindow((JMapFrame) c);
			// Thread(this)).start();
			
			//Cleanup of the threads attached to the window - important if adding new layers
			((JMapFrame) c).getChartPanel().getAisLayer().stop();
			((JMapFrame) c).getChartPanel().getWmsLayer().stop();
		}

		if (c instanceof NotificationCenter) {
			mainFrame.toggleNotificationCenter();
			return;
		}

		super.remove(c);

		// manager.setFramesAlwaysOnTop();

	}

	/**
	 * Sets all component size properties ( maximum, minimum, preferred) to the
	 * given dimension.
	 */
	public void setAllSize(Dimension d) {
		setMinimumSize(d);
		setMaximumSize(d);
		setPreferredSize(d);
	}

	/**
	 * Sets all component size properties ( maximum, minimum, preferred) to the
	 * given width and height.
	 */
	public void setAllSize(int width, int height) {
		setAllSize(new Dimension(width, height));
	}

	/**
	 * Tile all internal frames
	 */
	public void tileFrames() {
		java.awt.Component allFrames[] = getAllFrames();
		manager.setNormalSize();

		int jMapFramesCount = 0;

		for (int i = 0; i < allFrames.length; i++) {
			if (allFrames[i] instanceof JMapFrame) {
				jMapFramesCount++;
			}
		}

		int frameWidth = getBounds().width / jMapFramesCount;

		int frameHeight = getBounds().height / jMapFramesCount;
		int y = 0;
		int x = 0;

		for (int i = 0; i < allFrames.length; i++) {
			if (allFrames[i] instanceof JMapFrame) {
				// allFrames[i].setSize(getBounds().width, frameHeight);
				allFrames[i].setSize(frameWidth, getBounds().height);
				allFrames[i].setLocation(x, 0);
				y = y + frameHeight;
				x = x + frameWidth;
			}
		}
	}

	// private void checkDesktopSize() {
	// if (getParent() != null && isVisible())
	// manager.resizeDesktop();
	// }
}