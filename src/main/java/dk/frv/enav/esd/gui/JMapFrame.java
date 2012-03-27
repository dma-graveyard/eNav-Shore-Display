package dk.frv.enav.esd.gui;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class JMapFrame extends JInternalFrame implements MouseListener {
	
	
	private static final long serialVersionUID = 1L;	
	private ChartPanel chartPanel;
	boolean locked = false;
//	private JComponent northPanel;
	MouseMotionListener[] actions;
	
	public JMapFrame(int id) {
		super("New Window "+id, true, true, true, true);
		
		chartPanel = new ChartPanel();
		
		this.setContentPane(chartPanel);
		
		this.setSize(400, 300);
		this.setLocation(50, 50);
		this.setVisible(true);
	
		
		chartPanel.initChart();
		makeKeyBindings();
		
		((javax.swing.plaf.basic.BasicInternalFrameUI)this.getUI()).getNorthPane().addMouseListener(this);
		
//		northPanel = ((javax.swing.plaf.basic.BasicInternalFrameUI)this.getUI()).getNorthPane();
		
//		System.out.println(northPanel.getMouseListeners().length);
		
		actions = (MouseMotionListener[])((javax.swing.plaf.basic.BasicInternalFrameUI)this.getUI()).getNorthPane().getListeners(MouseMotionListener.class);
		
	}

	public void lockUnlockWindow(){
		
		if (locked){
			
//			for (int i = 0; i < actions.length; i++)
//				northPanel.addMouseMotionListener( actions[i] );
			

			this.setResizable(true);
			setRootPaneCheckingEnabled(true);
			this.updateUI();
			((javax.swing.plaf.basic.BasicInternalFrameUI)this.getUI()).getNorthPane().addMouseListener(this);
			
			
//			System.out.println(northPanel.getMouseListeners().length);


			locked = false;
		}else{
			
//			for (int i = 0; i < actions.length; i++)
//				northPanel.removeMouseMotionListener( actions[i] );

			this.setResizable(false);
			setRootPaneCheckingEnabled(false);
			((javax.swing.plaf.basic.BasicInternalFrameUI)this.getUI()).setNorthPane(null);
			this.setBorder(null);
//			this.updateUI();
//			this.updateUI();
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

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getClickCount() == 2){
			String title =
		        JOptionPane.showInputDialog(this, "Enter a new title:", this.getTitle());
			this.setTitle(title);	
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
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
