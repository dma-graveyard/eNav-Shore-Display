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

import java.io.Serializable;
import java.util.Properties;

import com.bbn.openmap.proj.coords.LatLonPoint;
import com.bbn.openmap.util.PropUtils;

/**
 * Map/chart settings
 */
public class MapSettings implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "map.";

	private LatLonPoint center = new LatLonPoint.Double(56, 11);
	private float scale = 10000000;
	private int maxScale = 5000;
	

	public MapSettings() {
	}

	public LatLonPoint getCenter() {
		return center;
	}

	public int getMaxScale() {
		return maxScale;
	}

	public float getScale() {
		return scale;
	}

	/**
	 * Read the properties element and set the internal variables
	 * @param props
	 */
	public void readProperties(Properties props) {
		center.setLatitude(PropUtils.doubleFromProperties(props, PREFIX + "center_lat", center.getLatitude()));
		center.setLongitude(PropUtils.doubleFromProperties(props, PREFIX + "center_lon", center.getLongitude()));
		scale = PropUtils.floatFromProperties(props, PREFIX + "scale", scale);
		maxScale = PropUtils.intFromProperties(props, PREFIX + "maxScale", maxScale);
	}

	public void setCenter(LatLonPoint center) {
		this.center = center;
	}

	public void setMaxScale(int maxScale) {
		this.maxScale = maxScale;
	}
	
	/**
	 * Set the properties to the value from the internal, usually called
	 * when saving settings to file
	 * @param props
	 */
	public void setProperties(Properties props) {
		props.put(PREFIX + "center_lat", Double.toString(center.getLatitude()));
		props.put(PREFIX + "center_lon", Double.toString(center.getLongitude()));
		props.put(PREFIX + "scale", Double.toString(scale));
		props.put(PREFIX + "maxScale", Integer.toString(maxScale));

	}
	
	public void setScale(float scale) {
		this.scale = scale;
	}
	
}
