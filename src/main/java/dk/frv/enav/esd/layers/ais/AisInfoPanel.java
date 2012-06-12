package dk.frv.enav.esd.layers.ais;

import dk.frv.enav.esd.gui.utils.InfoPanel;

/**
 * AIS mouse over info
 */
public class AisInfoPanel extends InfoPanel {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor
	 */
	public AisInfoPanel() {
		super();
	}
	
	/**
	 * Display a AIS info
	 * @param vessel
	 */
	public void showAisInfo(Vessel vessel) {
		String aisText = "<HTML>";
		if(vessel.getName() != "N/A")
			aisText += vessel.getName() + " ("+vessel.getMMSI() + ")";
		else
			aisText += vessel.getMMSI();
		aisText += "<BR/>COG "+vessel.getHeading()+"° SOG "+vessel.getSog()+" kn";
		aisText += "</HTML>";
		showText(aisText);
	}
}
