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
package dk.frv.enav.esd.ais;

import java.io.Serializable;
import java.util.Map;

import dk.frv.enav.esd.ais.AtoNTarget;
import dk.frv.enav.esd.ais.SarTarget;
import dk.frv.enav.esd.ais.VesselTarget;

/**
 * Container class for storing AIS view as serialized object
 */
public class AisStore implements Serializable {

	private static final long serialVersionUID = 2L;
	
	private Map<Long, AtoNTarget> atonTargets;
	private Map<Long, VesselTarget> vesselTargets;
	private Map<Long, SarTarget> sarTargets;	
	private VesselTarget ownShip; 

	/**
	 * Constructor
	 */
	public AisStore() {
		
	}

	/**
	 * Returns the targets of type Aton
	 * @return
	 */
	public Map<Long, AtoNTarget> getAtonTargets() {
		return atonTargets;
	}

	/**
	 * Returns the targets of type SAR
	 * @return
	 */
	public Map<Long, SarTarget> getSarTargets() {
		return sarTargets;
	}
	
	public VesselTarget getOwnShip() {
		return ownShip;
	}

	/**
	 * Returns the targets
	 * @return
	 */
	public Map<Long, VesselTarget> getVesselTargets() {
		return vesselTargets;
	}

	/**
	 * Target the aton targets
	 * @param atonTargets
	 */
	public void setAtonTargets(Map<Long, AtoNTarget> atonTargets) {
		this.atonTargets = atonTargets;
	}

	/**
	 * Set the SAR targets
	 * @param sarTargets
	 */
	public void setSarTargets(Map<Long, SarTarget> sarTargets) {
		this.sarTargets = sarTargets;
	}

	/**
	 * Set the vessel targets
	 * @param vesselTargets
	 */
	public void setVesselTargets(Map<Long, VesselTarget> vesselTargets) {
		this.vesselTargets = vesselTargets;
	}
		
}
