package dk.frv.enav.esd.event;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JInternalFrame;

import dk.frv.enav.esd.gui.MainFrame;

public class ToolbarMoveMouseListener implements MouseListener, MouseMotionListener {
	JInternalFrame target;
	MainFrame frame;
	Point start_drag;
	Point start_loc;

	public ToolbarMoveMouseListener(JInternalFrame toolBar, MainFrame frame) {
		this.target = toolBar;
		this.frame = frame;
	}

	Point getScreenLocation(MouseEvent e) {
		Point cursor = e.getPoint();
		Point target_location = this.target.getLocationOnScreen();
		return new Point((int) (target_location.getX() + cursor.getX()),
				(int) (target_location.getY() + cursor.getY()));
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		this.start_drag = this.getScreenLocation(e);
		this.start_loc = target.getLocation();
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		
		// This should be tested in multiple OS or be avoided.
		int offset_x = 16;
		int offset_y = 59;
		
		int frameWidth = frame.getSize().width;
		int frameHeight = frame.getSize().height;
		
		Point current = this.getScreenLocation(e);
		Point offset = new Point(
				(int) current.getX() - (int) start_drag.getX(),
				(int) current.getY() - (int) start_drag.getY());
		JInternalFrame frame = target;
		
		int newX = (int) (this.start_loc.getX() + offset.getX());
		int newY = (int) (this.start_loc.getY() + offset.getY());
		
//		System.out.println("Toolbar: "+newX+"x"+newY);
//		System.out.println("Toolbar size: "+target.getSize().width+"x"+target.getSize().height);
//		System.out.println("Toolbar total: "+(newX + target.getSize().width)+"x"+(newY + target.getSize().height));
		
		if(newX < 0) newX = 0;
		if((newX + target.getSize().width + offset_x) > frameWidth) newX = frameWidth - target.getSize().width - offset_x;
		if(newY < 0) newY = 0;
		if((newY + target.getSize().height + offset_y) > frameHeight) newY = frameHeight - target.getSize().height - offset_y;
		
		//if(newX >= 0 && newY >= 0) {
			Point new_location = new Point(newX, newY);
			frame.setLocation(new_location);
		//}
		
		//System.out.println(newX);
		//System.out.println(newY);
//		System.out.println("Frame: "+frameWidth+"x"+frameHeight);
		//System.out.println("Toolbar: "+newX+"x"+newY);
		
	}

	public void mouseMoved(MouseEvent e) {
	}
}