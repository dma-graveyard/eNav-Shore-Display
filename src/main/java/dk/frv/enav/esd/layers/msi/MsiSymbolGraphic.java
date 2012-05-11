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

import javax.swing.ImageIcon;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.esd.ESD;
import dk.frv.enav.esd.msi.MsiHandler.MsiMessageExtended;
import dk.frv.enav.ins.common.graphics.CenterRaster;


/**
 * Graphic for MSI symbol 
 */
public class MsiSymbolGraphic extends MsiSymbolPosition {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor
	 * @param message
	 */
	public MsiSymbolGraphic(MsiMessageExtended message) {
		super(message);
		setVague(true);
	}
	
	/**
	 * Createa msi symbol on a location, uses the msi png graphics
	 */
	public void createSymbol(GeoLocation pos) {
		CenterRaster msiSymbol;
		ImageIcon msiSymbolImage;
		int imageWidth;
		int imageHeight;
		if(acknowledged) {
			msiSymbolImage = new ImageIcon(ESD.class.getResource("/images/msi/msi_symbol_32.png"));
			imageWidth = msiSymbolImage.getIconWidth();
			imageHeight = msiSymbolImage.getIconHeight();
		} else {
			msiSymbolImage = new ImageIcon(ESD.class.getResource("/images/msi/msi_unack_symbol_32.png"));
			imageWidth = msiSymbolImage.getIconWidth();
			imageHeight = msiSymbolImage.getIconHeight();
		}
		msiSymbol = new CenterRaster(pos.getLatitude(), pos.getLongitude(), imageWidth, imageHeight, msiSymbolImage);
		add(msiSymbol);
	}
	
}
