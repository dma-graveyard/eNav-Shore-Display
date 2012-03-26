package dk.frv.enav.esd.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class JMapFrame extends JInternalFrame{
	
	private ChartPanel chartPanel;
	private static final long serialVersionUID = 1L;
	
	boolean locked = false;
	private JComponent northPanel;
	public JMapFrame(int id) {
		super("New Window "+id, true, true, true);
		
		chartPanel = new ChartPanel();
		this.setContentPane(chartPanel);
		this.setSize(400, 300);
		this.setLocation(50, 50);
		this.setVisible(true);

		chartPanel.initChart();
		makeKeyBindings();
		
		javax.swing.plaf.InternalFrameUI ifu= this.getUI();
		northPanel = ((javax.swing.plaf.basic.BasicInternalFrameUI)ifu).getNorthPane();
	}

	public void lockUnlockWindow(){
		
		if (locked){
			setRootPaneCheckingEnabled(true);
			javax.swing.plaf.InternalFrameUI ifu= this.getUI();
			((javax.swing.plaf.basic.BasicInternalFrameUI)ifu).setNorthPane(northPanel);
			this.setResizable(true);
			this.repaint();
			
			locked = false;
		}else{
			setRootPaneCheckingEnabled(false);
			javax.swing.plaf.InternalFrameUI ifu= this.getUI();
			((javax.swing.plaf.basic.BasicInternalFrameUI)ifu).setNorthPane(null);
			this.setResizable(false);
			this.repaint();
			locked = true;
		}
	}
	
	private void makeKeyBindings(){
	      JPanel content = (JPanel) getContentPane();
	      InputMap inputMap = content.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		
	    @SuppressWarnings("serial")
		Action zoomIn = new AbstractAction() {
	        public void actionPerformed(ActionEvent actionEvent) {
	        	chartPanel.doZoom(0.5f);
	        }
	      };
	      
		@SuppressWarnings("serial")
		Action zoomOut = new AbstractAction() {
		    public void actionPerformed(ActionEvent actionEvent) {
		    	chartPanel.doZoom(2f);
		        }
		      };	      
			
		@SuppressWarnings("serial")
		Action panUp = new AbstractAction() {
			public void actionPerformed(ActionEvent actionEvent) {
				chartPanel.pan(1);
				}
			};
		@SuppressWarnings("serial")
		Action panDown = new AbstractAction() {
			public void actionPerformed(ActionEvent actionEvent) {
				chartPanel.pan(2);
				}
			};			
			
		@SuppressWarnings("serial")
		Action panLeft = new AbstractAction() {
			public void actionPerformed(ActionEvent actionEvent) {
				chartPanel.pan(3);
				}
			};
		@SuppressWarnings("serial")
		Action panRight = new AbstractAction() {
			public void actionPerformed(ActionEvent actionEvent) {
				chartPanel.pan(4);
				}
			};			
	   			
	      inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ADD, 0), "ZoomIn");
	      inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_PLUS, 0), "ZoomIn");
	      inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SUBTRACT, 0), "ZoomOut");
	      inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_MINUS, 0), "ZoomOut");
	      inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_UP, 0), "panUp");
	      inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DOWN, 0), "panDown");
	      inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_LEFT, 0), "panLeft");
	      inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_RIGHT, 0), "panRight");
	      inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_KP_UP, 0), "panUp");
	      inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_KP_DOWN, 0), "panDown");
	      inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_KP_LEFT, 0), "panLeft");
	      inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_KP_RIGHT, 0), "panRight");	      
   
	      
	      content.getActionMap().put("ZoomOut", zoomOut);
	      content.getActionMap().put("ZoomIn", zoomIn);
	      content.getActionMap().put("panUp", panUp);	
	      content.getActionMap().put("panDown", panDown);	
	      content.getActionMap().put("panLeft", panLeft);	
	      content.getActionMap().put("panRight", panRight);	
	      
	}
	
	

}
