package dk.frv.enav.esd.layers.ais;

import javax.swing.ImageIcon;

import dk.frv.enav.esd.gui.InfoPanel;

/**
 * MSI mouse over info
 */
public class HighlightInfoPanel extends InfoPanel {

	private static final long serialVersionUID = 1L;
	static ImageIcon loadingImg = new ImageIcon("images/ais/highlight.png");
	
	/**
	 * Constructor for creating an WMSInfoPanel that uses the loadingIMG
	 */
	public HighlightInfoPanel() {
		super(loadingImg);
	}

	/**
	 * Show the image
	 */
	public void displayHighlight(int x, int y){
		setPos(x, y);
		showImage();
	}

}
