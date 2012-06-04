package dk.frv.enav.esd.gui;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

public class StaticImages {

    Cursor dragCursorMouseClicked; 
    Cursor dragCursor;
    Cursor navCursorMouseClicked; 
    Cursor navCursor; 

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

	
    
}
