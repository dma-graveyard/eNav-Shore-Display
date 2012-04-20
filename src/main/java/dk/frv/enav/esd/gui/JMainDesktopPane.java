package dk.frv.enav.esd.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyVetoException;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

public class JMainDesktopPane extends JDesktopPane {
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static int FRAME_OFFSET = 20;

	  private JMainDesktopManager manager;
	  private MainFrame mainFrame;


	public JMainDesktopPane(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	    manager = new JMainDesktopManager(this);
	    setDesktopManager(manager);
	    setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
	  }

//	  public void setBounds(int x, int y, int w, int h) {
//	    super.setBounds(x, y, w, h);
//	    checkDesktopSize();
//	  }

	  public JMainDesktopManager getManager() {
			return manager;
		}
	  
	  public Component add(JInternalFrame frame, boolean workspaceWindow) {
	    Component retval = super.add(frame);
//	    checkDesktopSize();


	    moveToFront(frame);
	    //frame.setVisible(true);
	    try {
	      frame.setSelected(true);
	    } catch (PropertyVetoException e) {
	      frame.toBack();
	    }
	    return retval;
	  }
	  
	  public Component add(JInternalFrame frame) {
		    JInternalFrame[] array = getAllFrames();
		    Point p;
		    int w;
		    int h;

		    Component retval = super.add(frame);
//		    checkDesktopSize();

		    if (array.length > 0) {
		      p = array[0].getLocation();
		      p.x = p.x + FRAME_OFFSET;
		      p.y = p.y + FRAME_OFFSET;
		    } else {
		      p = new Point(0, 0);
		    }
		    frame.setLocation(p.x, p.y);
		    if (frame.isResizable()) {
		    	
		      w = getWidth() - (getWidth() / 3);
		      h = getHeight() - (getHeight() / 3);
	//System.out.println(getWidth());
	//System.out.println(getHeight());
		      if (w < frame.getMinimumSize().getWidth())
		        w = (int) frame.getMinimumSize().getWidth();
		      if (h < frame.getMinimumSize().getHeight())
		        h = (int) frame.getMinimumSize().getHeight();
		      
		      if (w > 700){
		    	  w = 400;
		      }
		      if (h > 700){
		    	  h = 400;
		      }
		      
		      frame.setSize(w, h);
		      
		    }
		    moveToFront(frame);
		    frame.setVisible(true);
		    try {
		      frame.setSelected(true);
		    } catch (PropertyVetoException e) {
		      frame.toBack();
		    }
		    
		    
		    return retval;
		  }

	  public void remove(Component c) {

		  if (c instanceof JMapFrame){
			  mainFrame.removeMapWindow((JMapFrame) c);
//			  Thread(this)).start();
		   ((JMapFrame) c).getChartPanel().getAisLayer().stop();
		  }
		  
		  if (c instanceof NotificationCenter){
			  mainFrame.toggleNotificationCenter();
			  return;
		  }
		  
	    super.remove(c);

//	    manager.setFramesAlwaysOnTop();
	    
	  }

	  /**
	   * Cascade all internal frames
	   */
	  public void cascadeFrames() {
	    int x = 0;
	    int y = 0;
	    JInternalFrame allFrames[] = getAllFrames();

//	    manager.setNormalSize();
	    int frameHeight = (getBounds().height - 5) - allFrames.length * FRAME_OFFSET;
	    int frameWidth = (getBounds().width - 5) - allFrames.length * FRAME_OFFSET;
	    for (int i = allFrames.length - 1; i >= 0; i--) {
	      allFrames[i].setSize(frameWidth, frameHeight);
	      allFrames[i].setLocation(x, y);
	      x = x + FRAME_OFFSET;
	      y = y + FRAME_OFFSET;
	    }
	  }

	  /**
	   * Tile all internal frames
	   */
	  public void tileFrames() {
	    java.awt.Component allFrames[] = getAllFrames();
	    manager.setNormalSize();
	    
	    int jMapFramesCount = 0;
	    
	    for (int i = 0; i < allFrames.length; i++) {
			if (allFrames[i] instanceof JMapFrame){
				jMapFramesCount++;
			}
		}
	    
	    int frameWidth = getBounds().width / jMapFramesCount;
	    
	    int frameHeight = getBounds().height / jMapFramesCount;
	    int y = 0;
	    int x = 0;
	    
	    for (int i = 0; i < allFrames.length; i++) {
	    	if (allFrames[i] instanceof JMapFrame){
//	      allFrames[i].setSize(getBounds().width, frameHeight);
	    	allFrames[i].setSize(frameWidth, getBounds().height);
	      allFrames[i].setLocation(x, 0);
	      y = y + frameHeight;
	      x = x + frameWidth;
	    	}
	    }
	  }

	  /**
	   * Sets all component size properties ( maximum, minimum, preferred) to the
	   * given dimension.
	   */
	  public void setAllSize(Dimension d) {
	    setMinimumSize(d);
	    setMaximumSize(d);
	    setPreferredSize(d);
	  }

	  /**
	   * Sets all component size properties ( maximum, minimum, preferred) to the
	   * given width and height.
	   */
	  public void setAllSize(int width, int height) {
	    setAllSize(new Dimension(width, height));
	  }

//	  private void checkDesktopSize() {
//	    if (getParent() != null && isVisible())
//	      manager.resizeDesktop();
//	  }
	}