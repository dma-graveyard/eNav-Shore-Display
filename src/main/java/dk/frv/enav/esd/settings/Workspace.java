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
import java.util.Properties;

import com.bbn.openmap.proj.coords.LatLonPoint;
import com.bbn.openmap.util.PropUtils;

/**
 * Map/chart settings
 */
public class Workspace implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "map.";

	private int id;
	private String name;
	private Dimension size = new Dimension(1280, 800);
	private Point position = new Point(10, 10);
	private boolean locked = false;
	private LatLonPoint center = new LatLonPoint.Double(56, 11);
	private float scale = 10000000;


	public Workspace() {
	}

	public void readProperties(Properties props) {
		id = PropUtils.intFromProperties(props, PREFIX + "id", id);
		name = props.getProperty(PREFIX + "name");
		double w = PropUtils.doubleFromProperties(props, PREFIX + "appDimensions_w", size.getWidth());
		double h = PropUtils.doubleFromProperties(props, PREFIX + "appDimensions_h", size.getHeight());
		size.setSize(w, h);
		double x = PropUtils.doubleFromProperties(props, PREFIX + "appLocation_x", position.getX());
		double y = PropUtils.doubleFromProperties(props, PREFIX + "appLocation_y", position.getY());
		position.setLocation(x, y);
		locked = PropUtils.booleanFromProperties(props, PREFIX + "locked", locked);
		center.setLatitude(PropUtils.doubleFromProperties(props, PREFIX + "center_lat", center.getLatitude()));
		center.setLongitude(PropUtils.doubleFromProperties(props, PREFIX + "center_lon", center.getLongitude()));
		scale = PropUtils.floatFromProperties(props, PREFIX + "scale", scale);

	}

	public void setProperties(Properties props) {
//		props.put(PREFIX + "center_lat", Double.toString(center.getLatitude()));
//		props.put(PREFIX + "center_lon", Double.toString(center.getLongitude()));
//		props.put(PREFIX + "scale", Double.toString(scale));
//		props.put(PREFIX + "maxScale", Integer.toString(maxScale));

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Dimension getSize() {
		return size;
	}

	public void setSize(Dimension size) {
		this.size = size;
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public LatLonPoint getCenter() {
		return center;
	}

	public void setCenter(LatLonPoint center) {
		this.center = center;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	

	
}
