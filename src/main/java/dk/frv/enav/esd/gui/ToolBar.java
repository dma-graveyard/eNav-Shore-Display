package dk.frv.enav.esd.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ToolBar extends JInternalFrame {
	
	private static final long serialVersionUID = 1L;	
	private Boolean locked;
	private JLabel moveHandler;
	private JPanel masterPanel;
	private JPanel buttonPanel;
	private static int moveHandlerHeight = 12;
	private static int buttonSize = 35;
	private static int numberOfButtons = 0;
	private static int buttonColumns = 2;

	public ToolBar(MainFrame mainFrame) {
		
		this.locked = false;
		
		//this.setSize(75, 150);
		this.setLocation(0, 20);
		this.setVisible(true);
		
		this.setResizable(false);
		setRootPaneCheckingEnabled(false);
		((javax.swing.plaf.basic.BasicInternalFrameUI)this.getUI()).setNorthPane(null);
		this.setBorder(null);
		
        //JPanel toolsPanel = new JPanel();
        //toolsPanel.setPreferredSize(new Dimension(75, 150));
        
        moveHandler = new JLabel("Toolbar", JLabel.CENTER);
        moveHandler.setForeground(Color.WHITE);
        moveHandler.setOpaque(true);
        moveHandler.setBackground(Color.DARK_GRAY);
        moveHandler.setPreferredSize(new Dimension((buttonSize * buttonColumns), moveHandlerHeight));
        MoveMouseListener mml = new MoveMouseListener(this, mainFrame);
        moveHandler.addMouseListener(mml);
        moveHandler.addMouseMotionListener(mml);
		
		//FlowLayout experimentLayout = new FlowLayout();
		//toolsPanel.setLayout(experimentLayout);
		//toolsPanel.add(moveHandler);
		
		
		
		
		// Grid for buttons
        buttonPanel = new JPanel();
		//buttonPanel.setPreferredSize(new Dimension(75, 100));
		buttonPanel.setLayout(new GridLayout(0,2));
		buttonPanel.setBorder(BorderFactory.createLineBorder (Color.DARK_GRAY, 2));
	    buttonPanel.add(new JButton(new ImageIcon("images/toolbar/zoom.png")));
	    buttonPanel.add(new JButton(new ImageIcon("images/toolbar/zoom.png")));
	    buttonPanel.add(new JButton(new ImageIcon("images/toolbar/zoom.png")));
	    buttonPanel.add(new JButton(new ImageIcon("images/toolbar/zoom.png")));
	    buttonPanel.add(new JButton(new ImageIcon("images/toolbar/zoom.png")));
	    numberOfButtons = 5;

	    
	    masterPanel = new JPanel(new BorderLayout());
	    masterPanel.add(moveHandler, BorderLayout.NORTH);
	    masterPanel.add(buttonPanel, BorderLayout.SOUTH);
	    
	    this.getContentPane().add(masterPanel);
	 
	    repaintToolbar();
	}
	
	public void toggleToolbarLock() {
		if(locked) {
			masterPanel.add(moveHandler, BorderLayout.NORTH);
			locked = false;
			repaintToolbar();
			
			int newX = (int) (this.getLocation().getX());
			int newY = (int) (this.getLocation().getY());
			Point new_location = new Point(newX, (newY - moveHandlerHeight));
			this.setLocation(new_location);

		} else {
			masterPanel.remove(moveHandler);
			locked = true;
			repaintToolbar();
			
			int newX = (int) (this.getLocation().getX());
			int newY = (int) (this.getLocation().getY());
			Point new_location = new Point(newX, (newY + moveHandlerHeight));
			this.setLocation(new_location);
		}
	}
	
	public void addTool() {
		buttonPanel.add(new JButton(new ImageIcon("images/toolbar/zoom.png")));
		
		numberOfButtons++;
		repaintToolbar();
	}
	
	public void repaintToolbar() {
		
		double temp = (double) numberOfButtons / (double) buttonColumns;
		int width = buttonSize * buttonColumns;
		int innerHeight = (int) (Math.ceil(temp) * buttonSize);
		int height = innerHeight;
		
		if(!locked)
			height = innerHeight + moveHandlerHeight;
		
		this.setSize(width, height);
		buttonPanel.setSize(width, innerHeight);
		buttonPanel.setPreferredSize(new Dimension(width, innerHeight));
		this.revalidate();
		this.repaint();
		
	}
}

class MoveMouseListener implements MouseListener, MouseMotionListener {
	JInternalFrame target;
	MainFrame frame;
	Point start_drag;
	Point start_loc;

	public MoveMouseListener(JInternalFrame toolBar, MainFrame frame) {
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
		
		int frameWidth = frame.getSize().width;
		int frameHeight = frame.getSize().height;
		
		Point current = this.getScreenLocation(e);
		Point offset = new Point(
				(int) current.getX() - (int) start_drag.getX(),
				(int) current.getY() - (int) start_drag.getY());
		JInternalFrame frame = target;
		
		int newX = (int) (this.start_loc.getX() + offset.getX());
		int newY = (int) (this.start_loc.getY() + offset.getY());
		
		if(newX < 0) newX = 0;
		if((newX + target.getSize().width) > frameWidth) newX = frameWidth - target.getSize().width;
		if(newY < 0) newY = 0;
		if((newY + target.getSize().height) > frameHeight) newY = frameHeight - target.getSize().height;
		
		//if(newX >= 0 && newY >= 0) {
			Point new_location = new Point(newX, newY);
			frame.setLocation(new_location);
		//}
		
		//System.out.println(newX);
		//System.out.println(newY);
		
	}

	public void mouseMoved(MouseEvent e) {
	}
}
