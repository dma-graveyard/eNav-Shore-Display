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

/**
 * Map/chart settings
 */
public class Workspace implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "map.";

	boolean validWorkspace = false;
	private List<String> name = new ArrayList<String>();
	private List<Dimension> size = new ArrayList<Dimension>();
	private List<Point> position = new ArrayList<Point>();
//	private Point position = new Point(10, 10);
	private List<Boolean> locked = new ArrayList<Boolean>();
	private List<Boolean> alwaysInFront = new ArrayList<Boolean>();
	private List<LatLonPoint> center = new ArrayList<LatLonPoint>();
//	private LatLonPoint center = new LatLonPoint.Double(56, 11);
	private List<Float> scale = new ArrayList<Float>();


	public Workspace() {
	}

	public void readProperties(Properties props) {
		
		try {
			Collections.addAll(name, (props.getProperty(PREFIX + "name").split("//"))); 

			String[] w = props.getProperty(PREFIX + "size_w").split("//");
			String[] h = props.getProperty(PREFIX + "size_h").split("//");
			
			for (int i = 0; i < w.length; i++) {
				size.add(new Dimension(Integer.parseInt(w[i]), Integer.parseInt(h[i])));
			}
		
			String[] x = props.getProperty(PREFIX + "position_x").split("//");
			String[] y = props.getProperty(PREFIX + "position_y").split("//");
			
			for (int i = 0; i < x.length; i++) {
				position.add(new Point(Integer.parseInt(x[i]), Integer.parseInt(y[i])));
			}
			
			String[] lockedInput = props.getProperty(PREFIX + "locked").split("//");
			for (int i = 0; i < lockedInput.length; i++) {
				locked.add(   Boolean.parseBoolean(lockedInput[i]) );
			}
			
			String[] alwaysInFrontInput = props.getProperty(PREFIX + "alwaysInFront").split("//");
			for (int i = 0; i < alwaysInFrontInput.length; i++) {
				alwaysInFront.add(   Boolean.parseBoolean(alwaysInFrontInput[i]) );
			}
			
			String[] center_lat = props.getProperty(PREFIX + "center_lat").split("//");
			String[] center_lon = props.getProperty(PREFIX + "center_lon").split("//");

			for (int i = 0; i < w.length; i++) {
				center.add(new LatLonPoint.Double(Double.parseDouble(center_lat[i]), Double.parseDouble(center_lon[i])));
			}
			
			String[] scaleInput = props.getProperty(PREFIX + "scale").split("//");
			for (int i = 0; i < scaleInput.length; i++) {
				scale.add(   Float.parseFloat(scaleInput[i]) );
			}
			validWorkspace = true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		
	}

	public void setProperties(Properties props) {
//		props.put(PREFIX + "center_lat", Double.toString(center.getLatitude()));
//		props.put(PREFIX + "center_lon", Double.toString(center.getLongitude()));
//		props.put(PREFIX + "scale", Double.toString(scale));
//		props.put(PREFIX + "maxScale", Integer.toString(maxScale));

	}

	public boolean isValidWorkspace() {
		return validWorkspace;
	}

	public void setValidWorkspace(boolean validWorkspace) {
		this.validWorkspace = validWorkspace;
	}

	public List<Boolean> getAlwaysInFront() {
		return alwaysInFront;
	}

	public void setAlwaysInFront(List<Boolean> alwaysInFront) {
		this.alwaysInFront = alwaysInFront;
	}

	public List<String> getName() {
		return name;
	}

	public void setName(List<String> name) {
		this.name = name;
	}

	public List<Dimension> getSize() {
		return size;
	}

	public void setSize(List<Dimension> size) {
		this.size = size;
	}

	public List<Point> getPosition() {
		return position;
	}

	public void setPosition(List<Point> position) {
		this.position = position;
	}

	public List<Boolean> isLocked() {
		return locked;
	}

	public void setLocked(List<Boolean> locked) {
		this.locked = locked;
	}

	public List<LatLonPoint> getCenter() {
		return center;
	}

	public void setCenter(List<LatLonPoint> center) {
		this.center = center;
	}

	public List<Float> getScale() {
		return scale;
	}

	public void setScale(List<Float> scale) {
		this.scale = scale;
	}



	

	

	
}
