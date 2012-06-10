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
package dk.frv.enav.esd.settings;

import java.awt.Dimension;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.bbn.openmap.proj.coords.LatLonPoint;
import com.bbn.openmap.util.PropUtils;

import dk.frv.enav.esd.gui.JMapFrame;

/**
 * Map/chart settings
 */
public class Workspace implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "workspace.";

	boolean validWorkspace = false;
	private List<String> name = new ArrayList<String>();
	private List<Dimension> size = new ArrayList<Dimension>();
	private List<Point> position = new ArrayList<Point>();
	// private Point position = new Point(10, 10);
	private List<Boolean> locked = new ArrayList<Boolean>();
	private List<Boolean> alwaysInFront = new ArrayList<Boolean>();
	private List<LatLonPoint> center = new ArrayList<LatLonPoint>();
	// private LatLonPoint center = new LatLonPoint.Double(56, 11);
	private List<Float> scale = new ArrayList<Float>();
	private List<Boolean> maximized = new ArrayList<Boolean>();
	private Point toolbarPosition = new Point();
	private Point notificationAreaPosition = new Point();
	private Point statusPosition = new Point();

	public Workspace() {
	}

	public List<Boolean> getAlwaysInFront() {
		return alwaysInFront;
	}

	public List<LatLonPoint> getCenter() {
		return center;
	}

	public List<String> getName() {
		return name;
	}

	public Point getNotificationAreaPosition() {
		return notificationAreaPosition;
	}

	public List<Point> getPosition() {
		return position;
	}

	public List<Float> getScale() {
		return scale;
	}

	public List<Dimension> getSize() {
		return size;
	}

	public Point getStatusPosition() {
		return statusPosition;
	}

	public Point getToolbarPosition() {
		return toolbarPosition;
	}

	public List<Boolean> isLocked() {
		return locked;
	}

	public List<Boolean> isMaximized() {
		return maximized;
	}

	public boolean isValidWorkspace() {
		return validWorkspace;
	}

	/**
	 * Read the properties element and set the internal variables
	 * 
	 * @param props
	 */
	public void readProperties(Properties props) {

		try {
			Collections.addAll(name, (props.getProperty(PREFIX + "name").split("//")));

			String[] w = props.getProperty(PREFIX + "size_w").split("//");
			String[] h = props.getProperty(PREFIX + "size_h").split("//");

			for (int i = 0; i < w.length; i++) {
				size.add(new Dimension((int) Double.parseDouble(w[i]), (int) Double.parseDouble(h[i])));
			}

			String[] x = props.getProperty(PREFIX + "position_x").split("//");
			String[] y = props.getProperty(PREFIX + "position_y").split("//");

			for (int i = 0; i < x.length; i++) {
				position.add(new Point((int) Double.parseDouble(x[i]), (int) Double.parseDouble(y[i])));
			}

			String[] lockedInput = props.getProperty(PREFIX + "locked").split("//");
			for (int i = 0; i < lockedInput.length; i++) {
				locked.add(Boolean.parseBoolean(lockedInput[i]));
			}

			String[] maximizedInput = props.getProperty(PREFIX + "maximized").split("//");
			for (int i = 0; i < maximizedInput.length; i++) {
				maximized.add(Boolean.parseBoolean(maximizedInput[i]));
			}

			String[] alwaysInFrontInput = props.getProperty(PREFIX + "alwaysInFront").split("//");
			for (int i = 0; i < alwaysInFrontInput.length; i++) {
				alwaysInFront.add(Boolean.parseBoolean(alwaysInFrontInput[i]));
			}

			String[] center_lat = props.getProperty(PREFIX + "center_lat").split("//");
			String[] center_lon = props.getProperty(PREFIX + "center_lon").split("//");

			for (int i = 0; i < w.length; i++) {
				center.add(new LatLonPoint.Double(Double.parseDouble(center_lat[i]), Double.parseDouble(center_lon[i])));
			}

			String[] scaleInput = props.getProperty(PREFIX + "scale").split("//");
			for (int i = 0; i < scaleInput.length; i++) {
				scale.add(Float.parseFloat(scaleInput[i]));
			}

			double x_pos = PropUtils.doubleFromProperties(props, PREFIX + "toolbar_pos_x", toolbarPosition.getX());
			double y_pos = PropUtils.doubleFromProperties(props, PREFIX + "toolbar_pos_y", toolbarPosition.getY());
			toolbarPosition.setLocation(x_pos, y_pos);

			x_pos = PropUtils.doubleFromProperties(props, PREFIX + "notification_pos_x",
					notificationAreaPosition.getX());
			y_pos = PropUtils.doubleFromProperties(props, PREFIX + "notification_pos_y",
					notificationAreaPosition.getY());
			notificationAreaPosition.setLocation(x_pos, y_pos);

			x_pos = PropUtils.doubleFromProperties(props, PREFIX + "status_pos_x", statusPosition.getX());
			y_pos = PropUtils.doubleFromProperties(props, PREFIX + "status_pos_y", statusPosition.getY());
			statusPosition.setLocation(x_pos, y_pos);

			validWorkspace = true;
		} catch (Exception e) {
//			System.out.println(e.getMessage());
		}

	}

	public void setAlwaysInFront(List<Boolean> alwaysInFront) {
		this.alwaysInFront = alwaysInFront;
	}

	public void setCenter(List<LatLonPoint> center) {
		this.center = center;
	}

	public void setLocked(List<Boolean> locked) {
		this.locked = locked;
	}

	public void setMaximized(List<Boolean> maximized) {
		this.maximized = maximized;
	}

	public void setName(List<String> name) {
		this.name = name;
	}

	public void setNotificationAreaPosition(Point notificationAreaPosition) {
		this.notificationAreaPosition = notificationAreaPosition;
	}

	public void setPosition(List<Point> position) {
		this.position = position;
	}

	/**
	 * Set the properties to the value from the internal, usually called when
	 * saving settings to file
	 * 
	 * @param props
	 */
	public void setProperties(Properties props, List<JMapFrame> mapWindows) {
		String name = "";
		String size_h = "";
		String size_w = "";
		String position_x = "";
		String position_y = "";
		String locked = "";
		String center_lat = "";
		String center_lon = "";
		String scale = "";
		String alwaysInFront = "";
		String maximized = "";

		for (int i = 0; i < mapWindows.size(); i++) {
			name = name + mapWindows.get(i).getTitle() + "//";
			size_h = size_h + mapWindows.get(i).getSize().getHeight() + "//";
			size_w = size_w + mapWindows.get(i).getSize().getWidth() + "//";
			position_x = position_x + mapWindows.get(i).getLocation().getX() + "//";
			position_y = position_y + mapWindows.get(i).getLocation().getY() + "//";
			locked = locked + mapWindows.get(i).isLocked() + "//";
			maximized = maximized + mapWindows.get(i).isMaximum() + "//";
			center_lat = center_lat + mapWindows.get(i).getChartPanel().getMap().getCenter().getY() + "//";
			center_lon = center_lon + mapWindows.get(i).getChartPanel().getMap().getCenter().getX() + "//";
			scale = scale + mapWindows.get(i).getChartPanel().getMap().getScale() + "//";
			alwaysInFront = alwaysInFront + mapWindows.get(i).isInFront() + "//";

		}
		props.put(PREFIX + "name", name);
		props.put(PREFIX + "size_h", size_h);
		props.put(PREFIX + "size_w", size_w);
		props.put(PREFIX + "position_x", position_x);
		props.put(PREFIX + "position_y", position_y);
		props.put(PREFIX + "locked", locked);
		props.put(PREFIX + "maximized", maximized);
		props.put(PREFIX + "center_lat", center_lat);
		props.put(PREFIX + "center_lon", center_lon);
		props.put(PREFIX + "scale", scale);
		props.put(PREFIX + "alwaysInFront", alwaysInFront);

		props.put(PREFIX + "toolbar_pos_x", Double.toString(toolbarPosition.getX()));
		props.put(PREFIX + "toolbar_pos_y", Double.toString(toolbarPosition.getY()));

		props.put(PREFIX + "notification_pos_x", Double.toString(notificationAreaPosition.getX()));
		props.put(PREFIX + "notification_pos_y", Double.toString(notificationAreaPosition.getY()));

		props.put(PREFIX + "status_pos_x", Double.toString(statusPosition.getX()));
		props.put(PREFIX + "status_pos_y", Double.toString(statusPosition.getY()));

	}

	public void setScale(List<Float> scale) {
		this.scale = scale;
	}

	public void setSize(List<Dimension> size) {
		this.size = size;
	}

	public void setStatusPosition(Point statusPosition) {
		this.statusPosition = statusPosition;
	}

	public void setToolbarPosition(Point toolbarPosition) {
		this.toolbarPosition = toolbarPosition;
	}

	public void setValidWorkspace(boolean validWorkspace) {
		this.validWorkspace = validWorkspace;
	}

}
