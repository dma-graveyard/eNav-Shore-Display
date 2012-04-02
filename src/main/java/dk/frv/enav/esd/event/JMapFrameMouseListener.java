package dk.frv.enav.esd.event;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import dk.frv.enav.esd.gui.JMapFrame;

public class JMapFrameMouseListener implements InternalFrameListener, MouseListener {

	@Override
	public void internalFrameActivated(InternalFrameEvent arg0) {
	
	}

	@Override
	public void internalFrameClosed(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameClosing(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameDeactivated(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameDeiconified(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameIconified(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameOpened(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		JMapFrame source = (JMapFrame) arg0.getSource();
//		source.getNameLabel().setVisible(true);
//		System.out.println("Entered " + source.getTitle());
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		JMapFrame source = (JMapFrame) arg0.getSource();
//		source.getNameLabel().setVisible(false);
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
