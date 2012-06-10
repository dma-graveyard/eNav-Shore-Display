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
package dk.frv.enav.esd.event;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JInternalFrame;

import dk.frv.enav.esd.ESD;
import dk.frv.enav.esd.gui.MainFrame;

/**
 * Class for setting up a component to use for dragging the frame
 */
public class ToolbarMoveMouseListener implements MouseListener, MouseMotionListener {
	JInternalFrame target;
	MainFrame frame;
	Point start_drag;
	Point start_loc;

	/**
	 * Constructor for setting of the listener
	 * @param toolBar 	reference to the frame which will be dragged
	 * @param frame		reference to the mainframe
	 */
	public ToolbarMoveMouseListener(JInternalFrame toolBar, MainFrame frame) {
		this.target = toolBar;
		this.frame = frame;
	}
	/**
	 * Function for getting the current mouse location
	 * @param e
	 * @return location location of the mouse
	 */
	Point getScreenLocation(MouseEvent e) {
		Point cursor = e.getPoint();
		Point target_location = this.target.getLocationOnScreen();
		Point location = new Point((int) (target_location.getX() + cursor.getX()),
				(int) (target_location.getY() + cursor.getY()));
		return location;
	}

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	/**
	 * Function for dragging the frame accordingly to the mouse drag
	 */
	public void mouseDragged(MouseEvent e) {
		
		// This should be tested in multiple OS or be avoided.
		//int offset_x = 16;
		//int offset_y = 59;
		
		//int frameWidth = frame.getSize().width;
		//int frameHeight = frame.getSize().height;
		
		Point current = this.getScreenLocation(e);
		
		if(current != null) {
		
			Point offset = new Point(
					(int) current.getX() - (int) start_drag.getX(),
					(int) current.getY() - (int) start_drag.getY());
			JInternalFrame frame = target;
			
			int newX = (int) (this.start_loc.getX() + offset.getX());
			int newY = (int) (this.start_loc.getY() + offset.getY());
			
			/*
			if(newX < 0) newX = 0;
			if((newX + target.getSize().width + offset_x) > frameWidth) newX = frameWidth - target.getSize().width - offset_x;
			if(newY < 0) newY = 0;
			if((newY + target.getSize().height + offset_y) > frameHeight) newY = frameHeight - target.getSize().height - offset_y;
			*/
			
			Point new_location = new Point(newX, newY);
			frame.setLocation(new_location);
			
			if (ESD.getMainFrame() != null){
			ESD.getMainFrame().getDesktop().getManager().resizeDesktop();
			}
		}

	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	/**
	 * Function for saving the initial start position when mouse is being dragged
	 */
	public void mousePressed(MouseEvent e) {
		this.start_drag = this.getScreenLocation(e);
		this.start_loc = target.getLocation();
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
	}
}