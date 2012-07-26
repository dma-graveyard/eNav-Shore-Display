/*
 * Copyright 2011 Danish Maritime Authority. All rights reserved.
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
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Authority ``AS IS'' 
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
package dk.frv.enav.esd.gui.route;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import dk.frv.enav.esd.ESD;
import dk.frv.enav.esd.event.ToolbarMoveMouseListener;
import dk.frv.enav.esd.gui.settingtabs.GuiStyler;
import dk.frv.enav.esd.gui.utils.ComponentFrame;
import dk.frv.enav.esd.gui.views.MainFrame;
import dk.frv.enav.esd.route.Route;
import dk.frv.enav.esd.route.RouteLoadException;
import dk.frv.enav.esd.route.RouteLoader;
import dk.frv.enav.esd.route.RouteManager;
import dk.frv.enav.esd.route.RoutesUpdateEvent;

/**
 * Route manager dialog
 */
public class RouteManagerDialog extends ComponentFrame implements ActionListener, ListSelectionListener,
		TableModelListener, MouseListener {

	private static final long serialVersionUID = 1L;

//	private static final Logger LOG = Logger.getLogger(RouteManagerDialog.class);

	protected RouteManager routeManager;

//	private JButton propertiesBtn;
//	private JButton zoomToBtn;
//	private JButton reverseCopyBtn;
//	private JButton deleteBtn;
//	private JButton exportBtn;
//	private JButton importBtn;
//	private JButton closeBtn;
	private JLabel propertiesBtn;
	private JLabel zoomToBtn;
	private JLabel reverseCopyBtn;
	private JLabel deleteBtn;
	private JLabel exportBtn;
	private JLabel importBtn;
	private JLabel closeBtn;
	private JLabel exportAllBtn;
	private JLabel metocBtn;
	private JLabel copyBtn;

	private JScrollPane routeScrollPane;
	private JTable routeTable;
	private RoutesTableModel routesTableModel;
	private ListSelectionModel routeSelectionModel;

	
	JFrame parent;

	private JPanel topBar;
	private static int moveHandlerHeight = 18;
	private JLabel moveHandler;
	private JPanel masterPanel;
	private JPanel contentPanel;
	private Color backgroundColor = new Color(83, 83, 83);
	private MainFrame mainFrame;
	
	
	Border paddingLeft = BorderFactory.createMatteBorder(0, 8, 0, 0, new Color(65, 65, 65));
	Border paddingBottom = BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(83, 83, 83));
	Border notificationPadding = BorderFactory.createCompoundBorder(paddingBottom, paddingLeft);
	Border notificationsIndicatorImportant = BorderFactory.createMatteBorder(0, 0, 0, 10, new Color(206, 120, 120));
	Border paddingLeftPressed = BorderFactory.createMatteBorder(0, 8, 0, 0, new Color(45, 45, 45));
	Border notificationPaddingPressed = BorderFactory.createCompoundBorder(paddingBottom, paddingLeftPressed);
	
	
	public RouteManagerDialog(JFrame parent) {
		super("Route Manager", false, true, false, false);
		this.parent = parent;
		routeManager = ESD.getRouteManager();

		
		
		
		// Strip off window looks
		setRootPaneCheckingEnabled(false);
		((javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI()).setNorthPane(null);
		this.setBorder(null);

		// Map tools
		topBar = new JPanel(new GridLayout(1, 3));
		topBar.setPreferredSize(new Dimension(500, moveHandlerHeight));
		topBar.setOpaque(true);
		topBar.setBackground(Color.DARK_GRAY);
		topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(30, 30, 30)));

		// Placeholder - for now
		topBar.add(new JLabel());

		// Movehandler/Title dragable)
		moveHandler = new JLabel("Route Manager", JLabel.CENTER);
		moveHandler.setFont(new Font("Arial", Font.BOLD, 9));
		moveHandler.setForeground(new Color(200, 200, 200));
		// actions = moveHandler.getListeners(MouseMotionListener.class);
		topBar.add(moveHandler);

		// The tools (minimize, maximize and close)
		JPanel windowToolsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		windowToolsPanel.setOpaque(false);
		windowToolsPanel.setPreferredSize(new Dimension(60, 50));

		JLabel close = new JLabel(new ImageIcon("images/window/close.png"));
		close.addMouseListener(new MouseAdapter() {

			public void mouseReleased(MouseEvent e) {
				toggleVisibility();
			}
		});

		close.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 2));
		windowToolsPanel.add(close);
		topBar.add(windowToolsPanel);
		
		contentPanel = new JPanel();
		contentPanel.setPreferredSize(new Dimension(900, 600 - moveHandlerHeight));
		contentPanel.setSize(new Dimension(900, 600 - moveHandlerHeight));
		contentPanel.setBackground(backgroundColor);
		
		masterPanel = new JPanel(new BorderLayout());
		masterPanel.add(topBar, BorderLayout.NORTH);
		masterPanel.add(contentPanel, BorderLayout.CENTER);

		masterPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, new Color(30, 30, 30), new Color(
				45, 45, 45)));
		
		getContentPane().add(masterPanel);
		
		
		
		
		setSize(600, 400);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setLocation(10, 10);

		propertiesBtn = new JLabel("Properties");
		GuiStyler.styleButton(propertiesBtn);
//		propertiesBtn.addActionListener(this);
		zoomToBtn = new JLabel("Zoom to");
		GuiStyler.styleButton(zoomToBtn);
//		zoomToBtn.addActionListener(this);
		reverseCopyBtn = new JLabel("Reverse copy");
		GuiStyler.styleButton(reverseCopyBtn);
//		reverseCopyBtn.addActionListener(this);
		deleteBtn = new JLabel("Delete");
		GuiStyler.styleButton(deleteBtn);
//		deleteBtn.addActionListener(this);
		exportBtn = new JLabel("Export");
		GuiStyler.styleButton(exportBtn);
//		exportBtn.addActionListener(this);
		exportAllBtn = new JLabel("Export All");
		GuiStyler.styleButton(exportAllBtn);
//		exportAllBtn.addActionListener(this);
		importBtn = new JLabel("Import");
		GuiStyler.styleButton(importBtn);
//		importBtn.addActionListener(this);
		closeBtn = new JLabel("Close");
		GuiStyler.styleButton(closeBtn);
//		closeBtn.addActionListener(this);
		metocBtn = new JLabel("METOC");
		metocBtn.setEnabled(false);
		GuiStyler.styleButton(metocBtn);
//		metocBtn.addActionListener(this);
		copyBtn = new JLabel("Copy");
		GuiStyler.styleButton(copyBtn);
//		copyBtn.addActionListener(this);


		routeTable = new JTable();
		routesTableModel = new RoutesTableModel(routeManager);
		routesTableModel.addTableModelListener(this);
		routeTable.setShowHorizontalLines(false);
		routeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		routeScrollPane = new JScrollPane(routeTable);
		routeScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		routeScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		routeTable.setFillsViewportHeight(true);
		// TODO: Comment this line when using WindowBuilder
		routeTable.setModel(routesTableModel);
		for (int i = 0; i < 3; i++) {
			if (i == 2) {
				routeTable.getColumnModel().getColumn(i).setPreferredWidth(50);
			} else {
				routeTable.getColumnModel().getColumn(i).setPreferredWidth(175);
			}
		}
		routeSelectionModel = routeTable.getSelectionModel();
		routeSelectionModel.addListSelectionListener(this);
		routeTable.setSelectionModel(routeSelectionModel);
		routeTable.addMouseListener(this);

		GroupLayout groupLayout = new GroupLayout(contentPanel);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addGroup(
				groupLayout
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(routeScrollPane, GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(
								groupLayout
										.createParallelGroup(Alignment.LEADING)
										.addGroup(
												groupLayout
														.createParallelGroup(Alignment.LEADING, false)
														.addComponent(closeBtn, Alignment.TRAILING,
																GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(zoomToBtn, GroupLayout.DEFAULT_SIZE,
																GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addComponent(propertiesBtn, GroupLayout.DEFAULT_SIZE, 131,
																Short.MAX_VALUE)
														.addComponent(copyBtn, GroupLayout.DEFAULT_SIZE,
																GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
										.addGroup(
												groupLayout
														.createParallelGroup(Alignment.LEADING, false)
														.addComponent(exportBtn, GroupLayout.DEFAULT_SIZE,
																GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addComponent(deleteBtn, GroupLayout.DEFAULT_SIZE,
																GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addComponent(reverseCopyBtn, GroupLayout.DEFAULT_SIZE,
																GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addComponent(metocBtn, GroupLayout.DEFAULT_SIZE, 131,
																Short.MAX_VALUE)
														.addComponent(exportAllBtn, GroupLayout.DEFAULT_SIZE, 131,
																Short.MAX_VALUE)
														.addComponent(importBtn, GroupLayout.DEFAULT_SIZE, 131,
																Short.MAX_VALUE))).addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				groupLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								groupLayout
										.createParallelGroup(Alignment.LEADING)
										.addGroup(
												groupLayout.createSequentialGroup().addComponent(propertiesBtn)
														.addPreferredGap(ComponentPlacement.RELATED)

														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(zoomToBtn)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(copyBtn)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(reverseCopyBtn)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(deleteBtn)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(exportBtn).addGap(7).addComponent(metocBtn)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(exportAllBtn)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(importBtn))
										.addComponent(routeScrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE,
												289, Short.MAX_VALUE)).addGap(28).addComponent(closeBtn)
						.addContainerGap()));

		contentPanel.setLayout(groupLayout);

		int selectRow = routeManager.getActiveRouteIndex();
		if (selectRow < 0 && routeManager.getRouteCount() > 0) {
			selectRow = 0;
		}
		if (selectRow >= 0) {
			routeSelectionModel.setSelectionInterval(selectRow, selectRow);
		}

		updateTable();
		updateButtons();
		
		addMouseListeners();
		
	}

	private void updateButtons() {
		boolean routeSelected = (routeTable.getSelectedRow() >= 0);
		boolean activeSelected = routeManager.isActiveRoute(routeTable.getSelectedRow());

		// LOG.info("---------------------------------------");
		// LOG.info("routeSelected: " + routeSelected);
		// LOG.info("routeTable.getSelectedRow(): " +
		// routeTable.getSelectedRow());
		// LOG.info("activeSelected: " + activeSelected);
		// LOG.info("routeManager.isRouteActive(): " +
		// routeManager.isRouteActive());
		// LOG.info("activeRoute: " + routeManager.getActiveRouteIndex());
		// LOG.info("\n\n");

		propertiesBtn.setEnabled(routeSelected);
		zoomToBtn.setEnabled(routeSelected);
		reverseCopyBtn.setEnabled(routeSelected);
		copyBtn.setEnabled(routeSelected);
		deleteBtn.setEnabled(routeSelected && !activeSelected);
//		metocBtn.setEnabled(routeSelected);
		exportBtn.setEnabled(routeSelected);
	}

	private void updateTable() {
		int selectedRow = routeTable.getSelectedRow();
		// Update routeTable
		routesTableModel.fireTableDataChanged();
		// routeTable.doLayout();
		updateButtons();
		if (selectedRow >= 0 && selectedRow < routeTable.getRowCount()) {
			routeSelectionModel.setSelectionInterval(selectedRow, selectedRow);
		}
	}

	private void close() {
		this.setVisible(false);
	}

	private void zoomTo() {


		Route selectedroute = routeManager.getRoute(routeTable.getSelectedRow());

		if (ESD.getMainFrame().getActiveMapWindow() != null) {
			ESD.getMainFrame().getActiveMapWindow().getChartPanel()
					.zoomToPoint(selectedroute.getWaypoints().getFirst().getPos());
		} else if (ESD.getMainFrame().getMapWindows().size() > 0) {
			ESD.getMainFrame().getMapWindows().get(0).getChartPanel()
					.zoomToPoint(selectedroute.getWaypoints().getFirst().getPos());
		}
		// TODO ChartPanel should implement a method that given a route does the
		// following
		// TODO disable auto follow
		// TODO find minx, miny and maxx, maxy
		// TODO center and scale map to include whole route
		//
	}

	private void copy() {
		if (routeTable.getSelectedRow() >= 0) {
			routeManager.routeCopy(routeTable.getSelectedRow());
			updateTable();
		}
	}

	private void reverseCopy() {
		if (routeTable.getSelectedRow() >= 0) {
			routeManager.routeReverse(routeTable.getSelectedRow());
			updateTable();
		}
	}

	private void properties() {
		int i = routeTable.getSelectedRow();
		if (i >= 0) {
			RoutePropertiesDialog routePropertiesDialog = new RoutePropertiesDialog((Window) parent, routeManager, i);
			routePropertiesDialog.setVisible(true);
		}
	}

	private void metocProperties() {
		int i = routeTable.getSelectedRow();
		if (i >= 0) {
			RouteMetocDialog routeMetocDialog = new RouteMetocDialog((Window) parent, routeManager, i);
			routeMetocDialog.setVisible(true);
			routeManager.notifyListeners(RoutesUpdateEvent.METOC_SETTINGS_CHANGED);
		}
	}

	private void delete() {
		if (routeTable.getSelectedRow() >= 0) {
			routeManager.removeRoute(routeTable.getSelectedRow());
			updateTable();
		}
	}

	private void exportToFile() {
		exportToFile(routeTable.getSelectedRow());
	}

	private void exportToFile(int routeId) {
		if (routeId < 0) {
			return;
		}

		Route route = routeManager.getRoute(routeId);

		JFileChooser fc = new JFileChooser(System.getProperty("user.dir") + "/routes/");
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);

		fc.addChoosableFileFilter(new FileNameExtensionFilter("Simple route text format", "txt", "TXT"));
		fc.setAcceptAllFileFilterUsed(true);
		File f = new File(route.getName() + ".txt");
		fc.setSelectedFile(f);

		if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
			return;
		}
		File file = fc.getSelectedFile();

		if (!fc.getSelectedFile().toString().contains(".txt")) {
			file = new File(fc.getSelectedFile().getPath() + ".txt");
		}

		if (file.exists()) {
			if (JOptionPane.showConfirmDialog(this, "File exists. Overwrite?", "Overwrite?", JOptionPane.YES_NO_OPTION) != 0) {
				exportToFile(routeId);
				return;
			}
		}

		if (!RouteLoader.saveSimple(route, file)) {
			JOptionPane.showMessageDialog(ESD.getMainFrame(), "Route save error", "Route not saved",
					JOptionPane.ERROR_MESSAGE);
		}

	}

	private void importFromFile() {
		// Get filename from dialog
		JFileChooser fc = new JFileChooser(System.getProperty("user.dir") + "/routes");
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(true);
		fc.addChoosableFileFilter(new FileNameExtensionFilter("Simple route text format", "txt", "TXT"));
		fc.addChoosableFileFilter(new FileNameExtensionFilter("ECDIS900 V3 route", "rou", "ROU"));
		fc.addChoosableFileFilter(new FileNameExtensionFilter("Navisailor 3000 route", "rt3", "RT3"));
		fc.setAcceptAllFileFilterUsed(true);

		if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
			return;
		}

		for (File file : fc.getSelectedFiles()) {
			try {
				routeManager.loadFromFile(file);
			} catch (RouteLoadException e) {
				JOptionPane.showMessageDialog(this, e.getMessage() + ": " + file.getName(), "Route load error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		updateTable();
		routeSelectionModel.setSelectionInterval(routeTable.getRowCount() - 1, routeTable.getRowCount() - 1);
	}

	// Hackish method for now to get the routemanager
	public RouteManager getRouteManager() {
		return routeManager;
	}

	private void exportAllToFile() {
		for (int i = 0; i < routeTable.getRowCount(); i++) {
			exportToFile(i);
		}
	}
	
	public void addMouseListeners() {

		closeBtn.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				close();
			}
		});

		propertiesBtn.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				properties();
			}
		});

		
		zoomToBtn.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				zoomTo();
			}
		});

		
		copyBtn.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				copy();
			}
		});

		reverseCopyBtn.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				reverseCopy();
			}
		});	
		
		deleteBtn.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				delete();
			}
		});	
		
//		metocBtn.addMouseListener(new MouseAdapter() {
//			public void mouseReleased(MouseEvent e) {
//				metocProperties();
//			}
//		});	
		
		exportBtn.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				exportToFile();
			}
		});	
		
		exportAllBtn.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				exportAllToFile();
			}
		});	
		
		importBtn.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				importFromFile();
			}
		});	
		
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			properties();
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		updateButtons();
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		if (e.getColumn() == 2) {
			// Visibility has changed
			routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_VISIBILITY_CHANGED);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}
	
	@Override
	public void findAndInit(Object obj) {
		if (obj instanceof MainFrame) {
			mainFrame = (MainFrame) obj;
			ToolbarMoveMouseListener mml = new ToolbarMoveMouseListener(this, mainFrame);
			topBar.addMouseListener(mml);
			topBar.addMouseMotionListener(mml);
		}
	}

	/**
	 * Change the visiblity
	 */
	public void toggleVisibility() {
		setVisible(!this.isVisible());
	}
}
