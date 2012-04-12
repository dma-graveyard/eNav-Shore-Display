package dk.frv.enav.esd.gui;



import java.awt.Dimension;
import java.awt.Insets;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

public class JMainDesktopManager extends DefaultDesktopManager {
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JMainDesktopPane desktop;
	private HashMap<Integer, JInternalFrame> toFront;

	  public JMainDesktopManager(JMainDesktopPane desktop) {
	    this.desktop = desktop;
	    toFront = new HashMap<Integer, JInternalFrame>();
	  }
	  

	  public void endResizingFrame(JComponent f) {
	    super.endResizingFrame(f);
	    resizeDesktop();
	  }

	  public void endDraggingFrame(JComponent f) {
	    super.endDraggingFrame(f);
	    resizeDesktop();
	  }

	  public void activateFrame(JInternalFrame f) {
		  JInternalFrame[] allFrames = desktop.getAllFrames();
//		  System.out.println("Activated frame id " +  ((JMapFrame) f).getId());
		  if (toFront.size() == 0){
//			  System.out.println("Still zero");
			  super.activateFrame(f);
		  }else{
			  if (toFront.containsKey(((JMapFrame) f).getId())){
//				  System.out.println("It's added");
				  super.activateFrame(f);
			  }else{
				  
//				 System.out.println("Not in toFront");
				 Map.Entry<Integer, JInternalFrame> entry = (Entry<Integer, JInternalFrame>) toFront.entrySet().iterator().next();

				 super.activateFrame(f);
				 
				 
				 
				    Iterator it = toFront.entrySet().iterator();
				    while (it.hasNext()) {
				        Map.Entry pairs = (Map.Entry)it.next();
				        super.activateFrame((JInternalFrame) pairs.getValue());
//				        System.out.println(pairs.getKey() + " = " + pairs.getValue());
//				        it.remove(); // avoids a ConcurrentModificationException
				    }
				 
				 
				 
				 
				 
				 
//				 super.activateFrame(entry.getValue());
//				 try {
//					f.setSelected(false);
//				} catch (PropertyVetoException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
////				 super.activateFrame(allFrames[0]);


			  }
		  }
		  


		  
		  //Find out if it intersects with any other frame
		  //Get list of all intersecting frames that's not itself.
		  //
//		  
//		  if (((JMapFrame) f).getId() == 1){
////			  super.activateFrame(f);
//		  }else{
			  
//			  super.activateFrame(allFrames[0]);

//		  }
		  
//		  super.activateFrame(f);
//			  System.out.println("Activated frame " + (((JMapFrame) f).getId() ));

//			  System.out.println(allFrames.length);
//			  System.out.println( ((JMapFrame) allFrames[0]).getId()       );
//			  allFrames[0].toFront();
//			  allFrames[0].updateUI();
//			  f.repaint();
//			  allFrames[0].repaint();
//			  ((JMapFrame) allFrames[0]).toFront();
//			  allFrames[0].getId();
//			  desktop.getMainFrame().getMapWindows()
			  //Find out if current overlap with any.
//			  System.out.println(    ((JMainDesktopManager) f.getParent()).get               .getMapWindows().size()	);
			  
	  }
	  
	  
	  public void addToFront(int id, JInternalFrame f){
		  if (toFront.containsKey(id)){
			  toFront.remove(id);
		  }else{
		  toFront.put(id, f);
		  }
	  }
	  
	  
	  public void setNormalSize() {
	    JScrollPane scrollPane = getScrollPane();
	    int x = 0;
	    int y = 0;
	    Insets scrollInsets = getScrollPaneInsets();

	    if (scrollPane != null) {
	      Dimension d = scrollPane.getVisibleRect().getSize();
	      if (scrollPane.getBorder() != null) {
	        d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right, d.getHeight()
	            - scrollInsets.top - scrollInsets.bottom);
	      }

	      d.setSize(d.getWidth() - 20, d.getHeight() - 20);
	      desktop.setAllSize(x, y);
	      scrollPane.invalidate();
	      scrollPane.validate();
	    }
	  }

	  private Insets getScrollPaneInsets() {
	    JScrollPane scrollPane = getScrollPane();
	    if (scrollPane == null)
	      return new Insets(0, 0, 0, 0);
	    else
	      return getScrollPane().getBorder().getBorderInsets(scrollPane);
	  }

	  private JScrollPane getScrollPane() {
	    if (desktop.getParent() instanceof JViewport) {
	      JViewport viewPort = (JViewport) desktop.getParent();
	      if (viewPort.getParent() instanceof JScrollPane)
	        return (JScrollPane) viewPort.getParent();
	    }
	    return null;
	  }

	  protected void resizeDesktop() {
	    int x = 0;
	    int y = 0;
	    JScrollPane scrollPane = getScrollPane();
	    Insets scrollInsets = getScrollPaneInsets();

	    if (scrollPane != null) {
	      JInternalFrame allFrames[] = desktop.getAllFrames();
	      for (int i = 0; i < allFrames.length; i++) {
	        if (allFrames[i].getX() + allFrames[i].getWidth() > x) {
	          x = allFrames[i].getX() + allFrames[i].getWidth();
	        }
	        if (allFrames[i].getY() + allFrames[i].getHeight() > y) {
	          y = allFrames[i].getY() + allFrames[i].getHeight();
	        }
	      }
	      Dimension d = scrollPane.getVisibleRect().getSize();
	      if (scrollPane.getBorder() != null) {
	        d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right, d.getHeight()
	            - scrollInsets.top - scrollInsets.bottom);
	      }

	      if (x <= d.getWidth())
	        x = ((int) d.getWidth()) - 20;
	      if (y <= d.getHeight())
	        y = ((int) d.getHeight()) - 20;
	      desktop.setAllSize(x, y);
	      scrollPane.invalidate();
	      scrollPane.validate();
	    }
	  }
	}