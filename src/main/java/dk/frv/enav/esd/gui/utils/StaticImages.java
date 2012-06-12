package dk.frv.enav.esd.gui.utils;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.ImageIcon;

public class StaticImages {

    Cursor dragCursorMouseClicked; 
    Cursor dragCursor;
    Cursor navCursorMouseClicked; 
    Cursor navCursor; 
    ImageIcon highlightIcon;
    ImageIcon vesselWhite;
    ImageIcon vesselBlue;
    ImageIcon vesselLightgreen;
    ImageIcon vesselCyan;
    ImageIcon vesselRed;
    ImageIcon vesselWhite0;
	ImageIcon vesselBrown;
    ImageIcon vesselMagenta;
    ImageIcon vesselLightgray;
    public StaticImages(){
    	
    	
//      //Get the default toolkit  
      Toolkit toolkit = Toolkit.getDefaultToolkit();  
        
      //Load an image for the cursor  
      Image image = toolkit.getImage("images/toolbar/drag_mouse.png");
      dragCursor = toolkit.createCustomCursor(image, new Point(0,0), "Drag");
      
      Image image2 = toolkit.getImage("images/toolbar/drag_on_mouse.png");
      dragCursorMouseClicked = toolkit.createCustomCursor(image2, new Point(0,0), "Drag_on_mouse");
      
      Image image3 = toolkit.getImage("images/toolbar/zoom_mouse.png");
      navCursor = toolkit.createCustomCursor(image3, new Point(0,0), "Zoom");
      
      Image image4 = toolkit.getImage("images/toolbar/zoom_on_mouse.png");
      navCursorMouseClicked = toolkit.createCustomCursor(image4, new Point(0,0), "Zoom_on_mouse");  
      
      highlightIcon = new ImageIcon("images/ais/highlight.png");
      
      vesselWhite = new ImageIcon("images/vesselIcons/white1_90.png");   
      vesselBlue = new ImageIcon("images/vesselIcons/blue1_90.png");
      vesselLightgreen = new ImageIcon("images/vesselIcons/lightgreen1_90.png");
      vesselCyan = new ImageIcon("images/vesselIcons/cyan1_90.png");
      vesselRed = new ImageIcon("images/vesselIcons/red1_90.png");
      vesselWhite0 = new ImageIcon("images/vesselIcons/white0.png");
      vesselBrown = new ImageIcon("images/vesselIcons/brown1_90.png");
      vesselMagenta = new ImageIcon("images/vesselIcons/magenta1_90.png");
      vesselLightgray = new ImageIcon("images/vesselIcons/lightgray1_90.png");
      
    }

	public Cursor getDragCursorMouseClicked() {
		return dragCursorMouseClicked;
	}

	public Cursor getDragCursor() {
		return dragCursor;
	}

	public Cursor getNavCursorMouseClicked() {
		return navCursorMouseClicked;
	}

	public Cursor getNavCursor() {
		return navCursor;
	}

	public ImageIcon getHighlightIcon(){
		return highlightIcon;
	}
	
	public ImageIcon getVesselWhite() {
		return vesselWhite;
	}

	public ImageIcon getVesselBlue() {
		return vesselBlue;
	}

	public ImageIcon getVesselLightgreen() {
		return vesselLightgreen;
	}

	public ImageIcon getVesselCyan() {
		return vesselCyan;
	}

	public ImageIcon getVesselRed() {
		return vesselRed;
	}

	public ImageIcon getVesselWhite0() {
		return vesselWhite0;
	}

	public ImageIcon getVesselBrown() {
		return vesselBrown;
	}

	public ImageIcon getVesselMagenta() {
		return vesselMagenta;
	}

	public ImageIcon getVesselLightgray() {
		return vesselLightgray;
	}
	
    
}
