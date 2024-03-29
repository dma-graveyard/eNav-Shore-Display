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

import java.awt.AlphaComposite;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Properties;

import javax.swing.JPanel;
import javax.swing.border.Border;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.event.PanMouseMode;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.util.PropUtils;

import dk.frv.enav.esd.ESD;
import dk.frv.enav.esd.gui.views.JMapFrame;

/**
 * Mouse mode for dragging
 */
public class DragMouseMode extends AbstractCoordMouseMode {

	private static final long serialVersionUID = 1L;
	
	/**
     * Mouse Mode identifier, which is "Drag".
     */
    public final static transient String modeID = "Drag";    
    
    
    public final static String OpaquenessProperty = "opaqueness";
    public final static String LeaveShadowProperty = "leaveShadow";
    public final static String UseCursorProperty = "useCursor";
    public final static float DEFAULT_OPAQUENESS = 0.0f;
    
    private boolean isPanning = false;
    private BufferedImage bufferedMapImage = null;
    private BufferedImage bufferedRenderingImage = null;
    private int beanBufferWidth = 0;
    private int beanBufferHeight = 0;
    private int oX, oY;
    private float opaqueness;
    private boolean leaveShadow;
    private boolean useCursor;
    private JPanel glassFrame;
    
    
    
    Cursor dragCursorMouseClicked; 
    Cursor dragCursor; 

    /**
     * Construct a Drag Mouse Mode. Sets the ID of the mode to the modeID, the
     * consume mode to true, and the cursor to the crosshair.
     */
    public DragMouseMode() {
        this(true);
    }

    /**
     * Construct a DragMouseMode. Lets you set the consume mode. If the events
     * are consumed, then a MouseEvent is sent only to the first
     * MapMouseListener that successfully processes the event. If they are not
     * consumed, then all of the listeners get a chance to act on the event.
     * 
     * @param shouldConsumeEvents the mode setting.
     */
    public DragMouseMode(boolean shouldConsumeEvents) {
//        super(modeID, shouldConsumeEvents);
        super(modeID, true);
        setUseCursor(true);
        setLeaveShadow(true);
        setOpaqueness(DEFAULT_OPAQUENESS);
        // override the default cursor
//        
//        //Get the default toolkit  
        
//        Toolkit toolkit = Toolkit.getDefaultToolkit();  
//          
//        //Load an image for the cursor  
//        Image image = toolkit.getImage("images/toolbar/drag_mouse.png");
//        dragCursor = toolkit.createCustomCursor(image, new Point(0,0), "Drag");
//        
//        Image image2 = toolkit.getImage("images/toolbar/drag_on_mouse.png");
//        dragCursorMouseClicked = toolkit.createCustomCursor(image2, new Point(0,0), "Drag_on_mouse");  
        
//        setModeCursor(dragCursor);
        
//        setModeCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
    }

    private void setCursors(){
    	dragCursor = ESD.getStaticImages().getDragCursor(); 
    	dragCursorMouseClicked = ESD.getStaticImages().getDragCursorMouseClicked();
    }
    
    /**
     * Instantiates new image buffers if needed.<br>
     * This method is synchronized to avoid creating the images multiple times
     * if width and height doesn't change.
     * 
     * @param w mapBean's width.
     * @param h mapBean's height.
     */
    public synchronized void createBuffers(int w, int h) {
        if (w > 0 && h > 0 && (w != beanBufferWidth || h != beanBufferHeight)) {
            beanBufferWidth = w;
            beanBufferHeight = h;
            createBuffersImpl(w, h);
        }
    }

    /**
     * Instantiates new image buffers.
     * 
     * @param w Non-zero mapBean's width.
     * @param h Non-zero mapBean's height.
     */
    protected void createBuffersImpl(int w, int h) {
        // Release system resources used by previous images...
        if (bufferedMapImage != null) {
            bufferedMapImage.flush();
        }
        if (bufferedRenderingImage != null) {
            bufferedRenderingImage.flush();
        }
        // New images...
        bufferedMapImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        bufferedRenderingImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    }

	/**
	 * Find and init bean function used in initializing other classes
	 */
    public void findAndInit(Object someObj) {
    	if (someObj instanceof JMapFrame) {
    		setCursors();
    		glassFrame = ((JMapFrame) someObj).getGlassPanel();
    		glassFrame.setCursor(dragCursor);
    	}
    	
    	super.findAndInit(someObj);
    }

    /**
     * Return opaqueness
     * @return
     */
    public float getOpaqueness() {
        return opaqueness;
    }

    /**
     * Get oX
     * @return
     */
    public int getOX() {
        return oX;
    }

    /**
     * Get oY
     * @return
     */
    public int getOY() {
        return oY;
    }

    /**
     * Get properties
     */
    public Properties getProperties(Properties props) {
        props = super.getProperties(props);
        String prefix = PropUtils.getScopedPropertyPrefix(this);
        props.put(prefix + OpaquenessProperty, Float.toString(getOpaqueness()));
        props.put(prefix + LeaveShadowProperty,
                Boolean.toString(isLeaveShadow()));
        props.put(prefix + UseCursorProperty, Boolean.toString(isUseCursor()));
        return props;
    }
    
    /**
     * Get property info
     */
    public Properties getPropertyInfo(Properties props) {
        props = super.getPropertyInfo(props);

        PropUtils.setI18NPropertyInfo(i18n,
                props,
                PanMouseMode.class,
                OpaquenessProperty,
                "Transparency",
                "Transparency level for moving map, between 0 (clear) and 1 (opaque).",
                null);
        PropUtils.setI18NPropertyInfo(i18n,
                props,
                PanMouseMode.class,
                LeaveShadowProperty,
                "Leave Shadow",
                "Display current map in background while panning.",
                "com.bbn.openmap.util.propertyEditor.YesNoPropertyEditor");

        PropUtils.setI18NPropertyInfo(i18n,
                props,
                PanMouseMode.class,
                UseCursorProperty,
                "Use Cursor",
                "Use hand cursor for mouse mode.",
                "com.bbn.openmap.util.propertyEditor.YesNoPropertyEditor");

        return props;
    }

    /**
     * Return if it should leave a shadow
     * @return
     */
    public boolean isLeaveShadow() {
        return leaveShadow;
    }
    
    /**
     * Return if it is in panning mode
     * @return
     */
    public boolean isPanning() {
        return isPanning;
    }

    /**
     * @return Returns the useCursor.
     */
    public boolean isUseCursor() {
        return useCursor;
    }

    /**
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     *      The first click for drag, the image is generated. This image is
     *      redrawing when the mouse is move, but, I need to repain the original
     *      image.
     */
    public void mouseDragged(MouseEvent arg0) {
//    	chartPanel.getMap().setCursor(dragCursorMouseClicked);
    	
        MapBean mb = ((MapBean) arg0.getSource());
        Point2D pnt = mb.getNonRotatedLocation(arg0);
        int x = (int) pnt.getX();
        int y = (int) pnt.getY();

        if (!isPanning) {
            int w = mb.getWidth();
            int h = mb.getHeight();

            /*
             * Making the image
             */

            if (bufferedMapImage == null || bufferedRenderingImage == null) {
                createBuffers(w, h);
            }

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Graphics2D g = ge.createGraphics(bufferedMapImage);
            g.setClip(0, 0, w, h);
            Border border = mb.getBorder();
            mb.setBorder(null);
            if (mb.getRotation() != 0.0) {
                double angle = mb.getRotation();
                mb.setRotation(0.0);
                mb.paintAll(g);
                mb.setRotation(angle);
            } else {
                mb.paintAll(g);
            }
            mb.setBorder(border);

            oX = x;
            oY = y;

            isPanning = true;

        } else {
            if (bufferedMapImage != null && bufferedRenderingImage != null) {
                Graphics2D gr2d = (Graphics2D) bufferedRenderingImage.getGraphics();
                /*
                 * Drawing original image without transparence and in the
                 * initial position
                 */
                if (leaveShadow) {
                    gr2d.drawImage(bufferedMapImage, 0, 0, null);
                } else {
                    gr2d.setPaint(mb.getBckgrnd());
                    gr2d.fillRect(0, 0, mb.getWidth(), mb.getHeight());
                }

                /*
                 * Drawing image with transparence and in the mouse position
                 * minus origianl mouse click position
                 */
                gr2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                        opaqueness));
                gr2d.drawImage(bufferedMapImage, x - oX, y - oY, null);

//                ((Graphics2D) mb.getGraphics(true)).drawImage(bufferedRenderingImage,
//                        0,
//                        0,
//                        null);
            }
        }
        super.mouseDragged(arg0);
    }

    /**
     * Event on mouse pressed
     */
    public void mousePressed(MouseEvent arg0){
    	glassFrame.setCursor(dragCursorMouseClicked);
//    	chartPanel.getMap().setCursor(dragCursorMouseClicked);
    	}

    /**
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     *      Make Pan event for the map.
     */
    public void mouseReleased(MouseEvent arg0) {
        if (isPanning && arg0.getSource() instanceof MapBean) {
            MapBean mb = (MapBean) arg0.getSource();
            Projection proj = mb.getProjection();
            Point2D center = proj.forward(proj.getCenter());
            
            Point2D pnt = mb.getNonRotatedLocation(arg0);
            int x = (int) pnt.getX();
            int y = (int) pnt.getY();
            
            center.setLocation(center.getX() - x + oX, center.getY()
                    - y + oY);
            mb.setCenter(proj.inverse(center));
            isPanning = false;
            // bufferedMapImage = null; //clean up when not active...
        }
        super.mouseReleased(arg0);
        glassFrame.setCursor(dragCursor);
    }

    /**
     * Projection (the map) has changed
     * @param e
     */
    public void projectionChanged(ProjectionEvent e) {
        Object obj = e.getSource();
        if (obj instanceof MapBean) {
            MapBean mb = (MapBean) obj;
            int w = mb.getWidth();
            int h = mb.getHeight();
            createBuffers(w, h);
        }
    }

    /**
     * Set as active mouse mode
     */
    public void setActive(boolean val) {
        if (!val) {
            if (bufferedMapImage != null) {
                bufferedMapImage.flush();
            }
            if (bufferedRenderingImage != null) {
                bufferedRenderingImage.flush();
            }
            beanBufferWidth = 0;
            beanBufferHeight = 0;
            bufferedMapImage = null;
            bufferedRenderingImage = null;
        }
    }

    /**
     * Set leave shadow
     * @param leaveShadow
     */
    public void setLeaveShadow(boolean leaveShadow) {
        this.leaveShadow = leaveShadow;
    }

    /**
     * Set opaqueness
     * @param opaqueness
     */
    public void setOpaqueness(float opaqueness) {
        this.opaqueness = opaqueness;
    }

    /**
     * Set properties
     */
    public void setProperties(String prefix, Properties props) {
        super.setProperties(prefix, props);
        prefix = PropUtils.getScopedPropertyPrefix(prefix);

        opaqueness = PropUtils.floatFromProperties(props, prefix
                + OpaquenessProperty, opaqueness);
        leaveShadow = PropUtils.booleanFromProperties(props, prefix
                + LeaveShadowProperty, leaveShadow);

        setUseCursor(PropUtils.booleanFromProperties(props, prefix
                + UseCursorProperty, isUseCursor()));

    }
    
    /**
     * @param useCursor The useCursor to set.
     */
    public void setUseCursor(boolean useCursor) {
        this.useCursor = useCursor;
        if (useCursor) {
            /*
             * For who like make his CustomCursor
             */
            try {
//                Toolkit tk = Toolkit.getDefaultToolkit();
//                ImageIcon pointer = new ImageIcon(getClass().getResource("pan.gif"));
//                Dimension bestSize = tk.getBestCursorSize(pointer.getIconWidth(),
//                        pointer.getIconHeight());
//                Image pointerImage = ImageScaler.getOptimalScalingImage(pointer.getImage(),
//                        (int) bestSize.getWidth(),
//                        (int) bestSize.getHeight());
//                Cursor cursor = tk.createCustomCursor(pointerImage,
//                        new Point(0, 0),
//                        "PP");
            	glassFrame.setCursor(dragCursor);
                
                return;
            } catch (Exception e) {
                // Problem finding image probably, just move on.
            }
        }
    }
    
    public void mouseEntered(MouseEvent e) {
    	glassFrame.setCursor(dragCursor);
        super.mouseEntered(e);
    }
}
