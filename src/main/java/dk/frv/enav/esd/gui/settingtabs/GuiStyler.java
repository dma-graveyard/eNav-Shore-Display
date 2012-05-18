package dk.frv.enav.esd.gui.settingtabs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;

public class GuiStyler {
	
	public static Font defaultFont = new Font("Arial", Font.PLAIN, 11);
	public static Color textColor = new Color(237, 237, 237);
	public static Color backgroundColor = new Color(83, 83, 83);
	public static Border border = new MatteBorder(1, 1, 1, 1, (Color) new Color(70, 70, 70));
	
	public static void styleTabButton(JLabel label){
		label.setPreferredSize(new Dimension(125, 25));
//		generalSettings.setSize(new Dimension(76, 30));
		label.setFont(defaultFont);
		label.setForeground(textColor);
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		label.setBackground(Color.DARK_GRAY);
		label.setOpaque(true);
	}
	
	public static void styleButton(JLabel label){
		label.setPreferredSize(new Dimension(125, 25));
//		generalSettings.setSize(new Dimension(76, 30));
		label.setFont(defaultFont);
		label.setForeground(textColor);
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		label.setBackground(Color.DARK_GRAY);
		label.setOpaque(true);
	}
	
	public static void styleUnderMenu(JLabel label){
		label.setPreferredSize(new Dimension(125, 25));
		label.setFont(defaultFont);
		label.setForeground(textColor);
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		label.setBackground(Color.gray);
		label.setOpaque(true);
	}
	
	public static void styleText(JLabel label){
		label.setFont(defaultFont);
		label.setForeground(textColor);
	}
	
	public static void styleSpinner(JSpinner spinner){
		spinner.setBorder(GuiStyler.border);
		
		JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor)spinner.getEditor();
        editor.getTextField().setBackground(GuiStyler.backgroundColor);
        editor.getTextField().setForeground(GuiStyler.textColor);
        editor.getTextField().setFont(GuiStyler.defaultFont);
        editor.getTextField().setCaretColor(GuiStyler.textColor);
	}
	
	public static void styleTextFields(JTextField jtextField){
		jtextField.setBackground(GuiStyler.backgroundColor);
		jtextField.setForeground(GuiStyler.textColor);
		jtextField.setFont(GuiStyler.defaultFont);
		jtextField.setCaretColor(GuiStyler.textColor);
	}
}
