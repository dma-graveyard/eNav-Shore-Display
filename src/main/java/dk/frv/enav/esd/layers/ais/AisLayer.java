package dk.frv.enav.esd.layers.ais;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;

import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMPoly;
import com.bbn.openmap.omGraphics.OMText;
import com.bbn.openmap.omGraphics.labeled.LabeledOMGraphic;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.message.AisMessage;
import dk.frv.enav.esd.ESD;
import dk.frv.enav.esd.ais.AisHandler.AisMessageExtended;
import dk.frv.enav.esd.ais.VesselAisHandler;
import dk.frv.enav.esd.gui.ChartPanel;
import dk.frv.enav.esd.nmea.IVesselAisListener;
import dk.frv.enav.ins.ais.VesselPositionData;
import dk.frv.enav.ins.ais.VesselStaticData;
import dk.frv.enav.ins.ais.VesselTarget;

public class AisLayer extends OMGraphicHandlerLayer implements Runnable, IVesselAisListener {
	private OMGraphic graphic = new OMGraphicList();
	private ChartPanel chartPanel;
	private OMGraphicList list = new OMGraphicList();
	private Map<Long, OMPoly> targets = new HashMap<Long, OMPoly>();
	private static VesselAisHandler aisHandler;
	private List<AisMessageExtended> shipList;

	private VesselPositionData location;
	private OMPoly poly;
	private Font font = null;
	private OMText label = null;
	private int sizeOffset = 5;
	private String callSign;
	private String name;
	private String data[][];
	private Container pane;
	private JTable table;
	private JTextArea tf2 = new JTextArea();
	private JFrame jf;

	@Override
	public void run() {

		ESD.sleep(1000);
		shipInfoWindow();

		while (true) {
			ESD.sleep(1000);
			drawVessels();
		}
	}

	public AisLayer() {
		(new Thread(this)).start();
	}

	private void drawVessels() {
		if (aisHandler != null) {
			list.clear();

			// Size of triangle
			int[] xPos = { -sizeOffset, 0, sizeOffset, -sizeOffset };
			int[] yPos = { -sizeOffset, sizeOffset, -sizeOffset, -sizeOffset };

			shipList = aisHandler.getShipList();
			for (int i = 0; i < shipList.size(); i++) {
				if (aisHandler.getVesselTargets().containsKey(shipList.get(i).MMSI)) {
					AisMessageExtended vessel = shipList.get(i);
					VesselTarget vesselTarget = aisHandler.getVesselTargets().get(vessel.MMSI);
					VesselStaticData staticData = vesselTarget.getStaticData();
					font = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
					location = vesselTarget.getPositionData();
					double lat = location.getPos().getLatitude();
					double lon = location.getPos().getLongitude();

					// Draw vessel
					poly = new OMPoly(location.getPos().getLatitude(), location.getPos().getLongitude(), xPos, yPos, 0);
					poly.setFillPaint(new Color(0));
					list.add(poly);

					// Add MMSI sign
					label = new OMText(0, 0, 0, 0, Long.toString(shipList.get(i).MMSI), font, OMText.JUSTIFY_CENTER);
					label.setLat(lat);
					label.setLon(lon);
					label.setY(4 * sizeOffset);
					label.setData("ID: " + Long.toString(vesselTarget.getMmsi()));
					list.add(label);

					if (staticData != null) {
						callSign = staticData.getCallsign();
						name = staticData.getName();
					} else {
						callSign = "N/A";
						name = "N/A";
					}

					// Add call sign
					label = new OMText(0, 0, 0, 0, Long.toString(shipList.get(i).MMSI), font, OMText.JUSTIFY_RIGHT);
					label.setLat(lat);
					label.setLon(lon);
					label.setX(-2 * sizeOffset);
					label.setY(sizeOffset);
					label.setData("Call Sign: " + callSign);
					list.add(label);

					// Add call sign
					label = new OMText(0, 0, 0, 0, Long.toString(shipList.get(i).MMSI), font, OMText.JUSTIFY_LEFT);
					label.setLat(lat);
					label.setLon(lon);
					label.setX(2 * sizeOffset);
					label.setY(sizeOffset);
					label.setData("Name: " + name);
					list.add(label);

					// Add heading
					/*
					 * float trueHeading = location.getTrueHeading();
					 * System.out.println(trueHeading); boolean noHeading =
					 * false; if (trueHeading == 511) { trueHeading =
					 * location.getCog(); noHeading = true; }
					 */

				}
			}
			doPrepare();
		}
	}

	@Override
	public synchronized OMGraphicList prepare() {
		list.project(getProjection());
		return list;
	}

	@Override
	public void findAndInit(Object obj) {
		if (obj instanceof VesselAisHandler) {
			aisHandler = (VesselAisHandler) obj;
		}

	}

	@Override
	public void receive(AisMessage arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveOwnMessage(AisMessage aisMessage) {
		// TODO Auto-generated method stub

	}

	public void shipInfoWindow() {
		List<AisMessageExtended> shipList = aisHandler.getShipList();

		jf = new JFrame();
		jf.setTitle("My frame");
		jf.setSize(800, 600);
		pane = jf.getContentPane();
		pane.setLayout(new GridLayout(1, 3));

		String fields[] = { "MSI" };
		data = new String[shipList.size() + 1][1];
		for (int i = 0; i < shipList.size(); i++) {
			if (aisHandler.getVesselTargets().containsKey(shipList.get(i).MMSI)) {
				if(shipList.get(i).MMSI > 219652000 && shipList.get(i).MMSI < 219654000){
					System.out.println("YO!");
					data[i][0] = Long.toString(shipList.get(i).MMSI);
				}
			}
		}

		table = new JTable(data, fields);
		table.setBounds(0, 0, 100, 600);
		JScrollPane test = new JScrollPane(table);
		test.setBounds(0, 0, 100, 600);
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					updateRightPaneOfInfoWindow(e.getPoint());
				}
			}
		});
		pane.add(test, 0);
		tf2.setText("COMING..");
		tf2.setEditable(false);
		pane.add(tf2, 1);

		JButton reload = new JButton();
		reload.setText("Reload");
		reload.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				jf.setVisible(false);
				jf.dispose();
				shipInfoWindow();
			}
		});
		pane.add(reload, 2);

		jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE);
		jf.setVisible(true);
	}

	public void updateRightPaneOfInfoWindow(Point point) {
		Long MMSI = Long.parseLong(data[table.rowAtPoint(point)][0]);

		VesselTarget vesselTarget = aisHandler.getVesselTargets().get(MMSI);
		VesselStaticData staticData = vesselTarget.getStaticData();

		if (staticData != null) {
			callSign = staticData.getCallsign();
			name = staticData.getName();
		} else {
			callSign = "N/A";
			name = "N/A";
		}
		tf2.setText("MMSI: "+data[table.rowAtPoint(point)][0]+"\nCall sign: "+callSign+"\nName: "+name);
	}

}
