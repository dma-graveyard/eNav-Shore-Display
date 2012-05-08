package dk.frv.enav.esd.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.beans.PropertyVetoException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import javax.swing.border.EtchedBorder;

import dk.frv.enav.esd.event.ToolbarMoveMouseListener;

public class JMapFrame extends JInternalFrame implements MouseListener  {
	
	private static final long serialVersionUID = 1L;
	private ChartPanel chartPanel;
	boolean locked = false;
	boolean alwaysInFront = false;
	MouseMotionListener[] actions;
	private int id;
	private final MainFrame mainFrame;
	private JPanel glassPanel;
	private JLabel moveHandler;
	private JPanel mapPanel;
	private JPanel masterPanel;
	private static int moveHandlerHeight = 18;
	private boolean maximized = false;
	public int width;
	public int height;
	private static int chartPanelOffset = 6;
	JInternalFrame mapFrame = null;

	public JMapFrame(int id, MainFrame mainFrame) {
		super("New Window " + id, true, true, true, true);

		this.mainFrame = mainFrame;
		this.id = id;
		
		chartPanel = new ChartPanel(mainFrame, this);
		this.setContentPane(chartPanel);
		this.setVisible(true);

		initGlassPane();
		chartPanel.initChart();
		initGUI();
	}
	
	public JMapFrame(int id, MainFrame mainFrame, Point2D center, float scale) {
		super("New Window " + id, true, true, true, true);

		this.mainFrame = mainFrame;
		this.id = id;
		chartPanel = new ChartPanel(mainFrame, this);
		this.setContentPane(chartPanel);
		this.setVisible(true);

		initGlassPane();
		chartPanel.initChart(center, scale);
		initGUI();
	}
	
	public void initGUI(){
		makeKeyBindings();
		
		mapFrame = this;
		
		// Listen for resize
		mapFrame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				repaintMapWindow();
			}
		});
		
		//getDesktopPane().getDesktopManager()
		
		//((javax.swing.plaf.basic.BasicInternalFrameUI)this.getUI()).getNorthPane().addMouseListener(this);
		//actions = (MouseMotionListener[])((javax.swing.plaf.basic.BasicInternalFrameUI)this.getUI()).getNorthPane().getListeners(MouseMotionListener.class);
		
		// Strip off
		setRootPaneCheckingEnabled(false);
		((javax.swing.plaf.basic.BasicInternalFrameUI)this.getUI()).setNorthPane(null);
		this.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
		
		// Map tools
		mapPanel = new JPanel(new GridLayout(1,3));
		mapPanel.setPreferredSize(new Dimension(500, moveHandlerHeight));
		mapPanel.setOpaque(true);
		mapPanel.setBackground(Color.DARK_GRAY);
		mapPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(30, 30, 30)));
		
		ToolbarMoveMouseListener mml = new ToolbarMoveMouseListener(this, mainFrame);
		mapPanel.addMouseListener(mml);
		mapPanel.addMouseMotionListener(mml);
		
		// Placeholder - for now
		mapPanel.add(new JLabel());
		
        // Movehandler/Title dragable)
        moveHandler = new JLabel("New Window "+id, JLabel.CENTER);
        moveHandler.setFont(new Font("Arial", Font.BOLD, 9));
        moveHandler.setForeground(new Color(200, 200, 200));
        moveHandler.addMouseListener(this);
		actions = moveHandler.getListeners(MouseMotionListener.class);
        mapPanel.add(moveHandler);
        
        // The tools (minimize, maximize and close)
        JPanel mapToolsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        mapToolsPanel.setOpaque(false);
        mapToolsPanel.setPreferredSize(new Dimension(60, 50));
        
        JLabel minimize = new JLabel(new ImageIcon("images/window/minimize.png"));
        minimize.addMouseListener(new MouseAdapter() {  
		    public void mouseReleased(MouseEvent e) { 
		    	try {
		    		mapFrame.setIcon(true);
				} catch (PropertyVetoException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		    	System.out.println("Minimizing");
		    }
        });
        mapToolsPanel.add(minimize);
        
        final JLabel maximize = new JLabel(new ImageIcon("images/window/maximize.png"));
        maximize.addMouseListener(new MouseAdapter() {  
		    public void mouseReleased(MouseEvent e) { 
		    	try {
		    		if(maximized) {
		    			mapFrame.setMaximum(false);
		    			maximized = false;
		    			maximize.setIcon(new ImageIcon("images/window/maximize.png"));
		    		} else {
		    			mapFrame.setMaximum(true);
		    			maximized = true;
		    			maximize.setIcon(new ImageIcon("images/window/restore.png"));
		    		}
				} catch (PropertyVetoException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		    	System.out.println("Maximizing");
		    }
        });
        mapToolsPanel.add(maximize);
        
        JLabel close = new JLabel(new ImageIcon("images/window/close.png"));
        close.addMouseListener(new MouseAdapter() {  
		    public void mouseReleased(MouseEvent e) { 
		    	try {
		    		mapFrame.setClosed(true);
				} catch (PropertyVetoException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		    	System.out.println("Closing");
		    }
        });
        mapToolsPanel.add(close);
        
        mapPanel.add(mapToolsPanel);
        

        // Create the masterpanel for aligning
	    masterPanel = new JPanel(new BorderLayout());
	    masterPanel.add(mapPanel, BorderLayout.NORTH);
	    masterPanel.add(chartPanel, BorderLayout.SOUTH);
	    masterPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, new Color(30, 30, 30), new Color(45, 45, 45)));
	    //this.getContentPane().add(masterPanel);
        
	    this.setContentPane(masterPanel);

	    repaintMapWindow();
	}

	public void repaintMapWindow() {
		
		System.out.println("Repainting: " + mapFrame.getSize().width + " og " + mapFrame.getSize().height);
		
		width = mapFrame.getSize().width;
		int innerHeight = mapFrame.getSize().height - moveHandlerHeight - chartPanelOffset;
		height = mapFrame.getSize().height;
		
		//System.out.println("WHAT? "+ innerHeight +" - "+ chartPanelOffset +" - "+ moveHandlerHeight);
		
		if(locked)
			innerHeight = mapFrame.getSize().height - 4; // 1 for border
		
		// And finally set the size and repaint it
		chartPanel.setSize(width, innerHeight);
		chartPanel.setPreferredSize(new Dimension(width, innerHeight));
		this.setSize(width, height);
		this.revalidate();
		this.repaint();

		//((javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI()).getNorthPane().addMouseListener(this);
		//actions = (MouseMotionListener[]) ((javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI()).getNorthPane()
		//		.getListeners(MouseMotionListener.class);

	}
	
	private void initGlassPane() {
		glassPanel = (JPanel) getGlassPane();
		glassPanel.setLayout(null);
		glassPanel.setVisible(false);
	}

	public int getId() {
		return id;
	}
	
	public void lockUnlockWindow(){
		/*
		if (locked){
//			for (int i = 0; i < actions.length; i++)
//				northPanel.addMouseMotionListener( actions[i] );
=======

	public void lockUnlockWindow() {
		if (locked) {
			// for (int i = 0; i < actions.length; i++)
			// northPanel.addMouseMotionListener( actions[i] );
>>>>>>> 679ae7c2f498c106be433b77b59033967a6985dd
			this.setResizable(true);
			setRootPaneCheckingEnabled(true);
			this.updateUI();
			((javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI()).getNorthPane().addMouseListener(this);

			// System.out.println(northPanel.getMouseListeners().length);

			locked = false;
		} else {

			// for (int i = 0; i < actions.length; i++)
			// northPanel.removeMouseMotionListener( actions[i] );

			this.setResizable(false);
			setRootPaneCheckingEnabled(false);
			((javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI()).setNorthPane(null);
			this.setBorder(null);
			// this.updateUI();
			// this.updateUI();
			locked = true;
		}
		*/
		
		if(locked) {
			
			masterPanel.add(mapPanel, BorderLayout.NORTH);
			locked = false;
			mapFrame.setResizable(true);
			
		} else {
			
			masterPanel.remove(mapPanel);
			locked = true;
			mapFrame.setResizable(false);
			
		}
		
		
		repaintMapWindow();
	}

	public void alwaysFront() {
		if (alwaysInFront) {
			alwaysInFront = false;
		} else {
			alwaysInFront = true;
		}
		mainFrame.getDesktop().getManager().addToFront(id, this);
	}

	public boolean isLocked() {
		return locked;
	}

	public boolean isInFront() {
		return alwaysInFront;
	}

	public ChartPanel getChartPanel() {
		return chartPanel;
	}

	private void makeKeyBindings() {
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
	
	public void rename(){
		String title =
	        JOptionPane.showInputDialog(this, "Enter a new title:", this.getTitle());
		if (title != null){
			this.setTitle(title);
			mainFrame.renameMapWindow(this);
			moveHandler.setText(title);

		}
	}

	
	
	public JPanel getGlassPanel() {
		return glassPanel;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getClickCount() == 1){
			rename();
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
