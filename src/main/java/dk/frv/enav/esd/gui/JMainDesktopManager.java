package dk.frv.enav.esd.gui;

import java.awt.Dimension;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
	private ToolBar toolbar;
	private NotificationCenter notCenter;
	private NotificationArea notificationArea;
	private StatusArea statusArea;
	
	

	public void setNotificationArea(NotificationArea notificationArea) {
		this.notificationArea = notificationArea;
	}

	public void setStatusArea(StatusArea statusArea) {
		this.statusArea = statusArea;
	}

	public void setNotCenter(NotificationCenter notCenter) {
		this.notCenter = notCenter;
	}

	public void setToolbar(ToolBar toolbar) {
		this.toolbar = toolbar;
	}

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
		if (toFront.size() == 0) {
			super.activateFrame(f);
		} else {
			if (toFront.containsKey(((JMapFrame) f).getId())) {
				super.activateFrame(f);
			} else {
				super.activateFrame(f);
				Iterator<Map.Entry<Integer, JInternalFrame>> it = toFront.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<Integer, JInternalFrame> pairs = (Map.Entry<Integer, JInternalFrame>) it.next();
					super.activateFrame(pairs.getValue());
				}
			}
		}
		super.activateFrame(statusArea);
		super.activateFrame(notificationArea);
		super.activateFrame(toolbar);
		super.activateFrame(notCenter);
	}

	public void addToFront(int id, JInternalFrame f) {
		if (toFront.containsKey(id)) {
			toFront.remove(id);
		} else {
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
				d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right, d.getHeight() - scrollInsets.top
						- scrollInsets.bottom);
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
				d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right, d.getHeight() - scrollInsets.top
						- scrollInsets.bottom);
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