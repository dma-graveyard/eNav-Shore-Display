/*
 * Copyright 2011 Danish Maritime Authority. All rights reserved.
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
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Authority ``AS IS'' 
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
package dk.frv.enav.esd.services.shore;

import org.apache.log4j.Logger;

import com.bbn.openmap.MapHandlerChild;

import dk.frv.enav.common.xml.ShoreServiceResponse;
import dk.frv.enav.common.xml.msi.request.MsiPollRequest;
import dk.frv.enav.common.xml.msi.response.MsiResponse;
import dk.frv.enav.esd.ais.AisHandler;
import dk.frv.enav.esd.status.ComponentStatus;
import dk.frv.enav.esd.status.IStatusComponent;
import dk.frv.enav.esd.status.ShoreServiceStatus;


/**
 * Shore service component providing the functional link to shore.
 */
public class ShoreServices extends MapHandlerChild implements IStatusComponent {
	
	private static final Logger LOG = Logger.getLogger(ShoreServices.class);

	private AisHandler aisHandler;
//	private GpsHandler gpsHandler;
//	private EnavSettings enavSettings;
	private ShoreServiceStatus status = new ShoreServiceStatus();
	
//	public ShoreServices(EnavSettings enavSettings) {
//		this.enavSettings = enavSettings; 
//	}
	
	public ShoreServices() {
	
	}

	public static double floatToDouble (float converThisNumberToFloat) {

		String floatNumberInString = String.valueOf(converThisNumberToFloat);
		double floatNumberInDouble = Double.parseDouble(floatNumberInString);
		return floatNumberInDouble;

		}	
	
	
	public MsiResponse msiPoll(int lastMessage) throws ShoreServiceException {
		// Create request
		MsiPollRequest msiPollRequest = new MsiPollRequest();
		msiPollRequest.setLastMessage(lastMessage);
		
		// Add request parameters
//		addRequestParameters(msiPollRequest);
		
		MsiResponse msiResponse = (MsiResponse)makeRequest("/api/xml/msi", "dk.frv.enav.common.xml.msi.request", "dk.frv.enav.common.xml.msi.response", msiPollRequest); 
		
		return msiResponse;
	}
	
	
	private ShoreServiceResponse makeRequest(String uri, String reqContextPath, String resContextPath, Object request) throws ShoreServiceException {
		// Create HTTP request
		ShoreHttp shoreHttp = new ShoreHttp(uri);
		// Init HTTP
		shoreHttp.init();		
		// Set content
		try {
			shoreHttp.setXmlMarshalContent(reqContextPath, request);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Failed to make XML request: " + e.getMessage());
			throw new ShoreServiceException(ShoreServiceErrorCode.INTERNAL_ERROR);
		}
		
		// Make request
		try {
			shoreHttp.makeRequest();
		} catch (ShoreServiceException e) {
			status.markContactError(e);
			throw e;
		}
		
		ShoreServiceResponse res;
		try {
			Object resObj = shoreHttp.getXmlUnmarshalledContent(resContextPath);
			res = (ShoreServiceResponse)resObj;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Failed to unmarshal XML response: " + e.getMessage());
			throw new ShoreServiceException(ShoreServiceErrorCode.INVALID_RESPONSE);
		}
				
		// Set last fail/contact
		status.markContactSuccess();
		
		// Report if an error response  
		if (res.getErrorCode() != 0) {
			throw new ShoreServiceException(ShoreServiceErrorCode.SERVICE_ERROR, res.getErrorMessage());
		}
		
		return res;
	}
		
	@Override
	public void findAndInit(Object obj) {
		if (aisHandler == null && obj instanceof AisHandler) {
			aisHandler = (AisHandler)obj;
		}
	}
	
	@Override
	public void findAndUndo(Object obj) {
		if (obj == aisHandler) {
			aisHandler = null;
		}
	}
	
	@Override
	public ComponentStatus getStatus() {
		return status;
	}
	
}
