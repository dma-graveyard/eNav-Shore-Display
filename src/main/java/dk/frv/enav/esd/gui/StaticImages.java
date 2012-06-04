package dk.frv.enav.esd.gui;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

public class StaticImages {

    Cursor dragCursorMouseClicked; 
    Cursor dragCursor; 

    public StaticImages(){
    	
    	
//      //Get the default toolkit  
      Toolkit toolkit = Toolkit.getDefaultToolkit();  
        
      //Load an image for the cursor  
      Image image = toolkit.getImage("images/toolbar/drag_mouse.png");
      dragCursor = toolkit.createCustomCursor(image, new Point(0,0), "Drag");
      
      Image image2 = toolkit.getImage("images/toolbar/drag_on_mouse.png");
      dragCursorMouseClicked = toolkit.createCustomCursor(image2, new Point(0,0), "Drag_on_mouse");  
      
    }

	public Cursor getDragCursorMouseClicked() {
		return dragCursorMouseClicked;
	}

	public void setDragCursorMouseClicked(Cursor dragCursorMouseClicked) {
		this.dragCursorMouseClicked = dragCursorMouseClicked;
	}

	public Cursor getDragCursor() {
		return dragCursor;
	}

	public void setDragCursor(Cursor dragCursor) {
		this.dragCursor = dragCursor;
	}
	
    
    
}
