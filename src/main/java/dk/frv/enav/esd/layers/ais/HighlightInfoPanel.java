package dk.frv.enav.esd.layers.ais;

import dk.frv.enav.esd.ESD;
import dk.frv.enav.esd.gui.utils.InfoPanel;

/**
 * MSI mouse over info
 */
public class HighlightInfoPanel extends InfoPanel {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor for creating an WMSInfoPanel that uses the loadingIMG
	 */
	public HighlightInfoPanel() {
		super(ESD.getStaticImages().getHighlightIcon());
	}

	/**
	 * Show the image
	 */
	public void displayHighlight(int x, int y){
		setPos(x, y);
		showImage();
	}

}
