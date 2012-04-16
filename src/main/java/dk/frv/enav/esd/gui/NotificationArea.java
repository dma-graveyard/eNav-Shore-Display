package dk.frv.enav.esd.gui;

import java.awt.event.MouseMotionListener;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;

/*
 import java.awt.Color;
 import javax.swing.JButton;
 import javax.swing.border.Border;
 import javax.swing.border.CompoundBorder;
 import javax.swing.border.EmptyBorder;
 import javax.swing.border.LineBorder;
 */
public class NotificationArea extends JInternalFrame {

	private static final long serialVersionUID = 1L;

	public NotificationArea() {
//		super("Meh");
//		this.mainFrame = mainFrame;
//		this.setSize(400, 300);
//		this.setLocation(50, 50);
//		this.setVisible(true);
		super("Notification Area");
		this.setSize(400, 300);
		this.setLocation(50, 50);
		this.setVisible(true);
		
		/*
		 * JButton button = new JButton("MMSI");
		 * button.setForeground(Color.BLACK); button.setBackground(Color.WHITE);
		 * Border line = new LineBorder(Color.BLACK); Border margin = new
		 * EmptyBorder(5, 15, 5, 15); Border compound = new CompoundBorder(line,
		 * margin); button.setBorder(compound);
		 */
	}
}
