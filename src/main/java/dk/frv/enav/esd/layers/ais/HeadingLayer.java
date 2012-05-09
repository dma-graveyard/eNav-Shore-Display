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
package dk.frv.enav.esd.layers.ais;

import com.bbn.openmap.omGraphics.OMPoly;


/**
 * Creates a heading vector on the vessels by extending OMPoly
 * @author Claes N. Ladefoged, claesnl@gmail.com
 */
public class HeadingLayer extends OMPoly {

	private static final long serialVersionUID = 1L;
	private double heading;
	private int[] origXPoints;
	private int[] origYPoints;
	private int[] xPoints;
	private int[] yPoints;
	private long MMSI;

	/**
	 * Initializes the heading with a OMPoly
	 * @param MMSI Key of vessel
	 * @param origXPoints X-Endpoints of vector
	 * @param origYPoints Y-Endpoints of vector
	 */
	public HeadingLayer(long MMSI, int[] origXPoints, int[] origYPoints) {
		super();
		this.MMSI = MMSI;
		this.origXPoints = origXPoints;
		this.origYPoints = origYPoints;
		this.xPoints = new int[origXPoints.length];
		this.yPoints = new int[origYPoints.length];
		this.heading = 0;
	}

	/**
	 * Updates the location and direction of the heading
	 * @param latPoint Latitude of vector
	 * @param lonPoint Longitude of vector
	 * @param units Radians or decimal degrees.
	 * @param heading Direction of vector
	 */
	public void setLocation(double latPoint, double lonPoint, int units, double heading) {
		if (this.heading != heading) {
			for (int i = 0; i < origXPoints.length; i++) {
				xPoints[i] = (int) (origXPoints[i] * Math.cos(heading) - origYPoints[i] * Math.sin(heading));
				yPoints[i] = (int) (origXPoints[i] * Math.sin(heading) + origYPoints[i] * Math.cos(heading));
			}
			this.heading = heading;
		}
		super.setLocation(latPoint, lonPoint, units, xPoints, yPoints);
	}

	/**
	 * Get the MMSI attached to the layer
	 * @return MMSI Key of vessel
	 */
	public long getMMSI() {
		return MMSI;
	}
	
}
