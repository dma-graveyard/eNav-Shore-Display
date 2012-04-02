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
import com.bbn.openmap.omGraphics.OMLine;
import com.bbn.openmap.omGraphics.OMPoly;
import com.bbn.openmap.omGraphics.OMText;
import com.bbn.openmap.omGraphics.labeled.LabeledOMGraphic;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.message.AisMessage;
import dk.frv.ais.message.ShipTypeCargo;
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
	private int h;
	private int w;
	private String callSign;
	private String name;
	private String dst;
	private int bow;
	private int port;
	private int starboard;
	private int stern;
	private float draught;
	private long eta;
	private long imo;
	private int postype;
	private ShipTypeCargo shiptype;
	private String data[][];
	private Container pane;
	private JTable table;
	private JTextArea tf2 = new JTextArea();
	private JFrame jf;
	private Boolean drawMMSI;
	private Boolean drawCallSign;
	private Boolean drawName;

	@Override
	public void run() {

		shipInfoWindow();

		// Import settings for each window
		Boolean settings = true;
		drawMMSI = true;
		drawCallSign = false;
		drawName = true;

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
					if (staticData != null) {
						bow = staticData.getDimBow();
						stern = staticData.getDimStern();
						port = staticData.getDimPort();
						starboard = staticData.getDimStarboard();
					} else {
						bow = 0;
						stern = 0;
						port = 0;
						starboard = 0;
					}
					if(bow>0 && stern>0 && port>0 && starboard>0){
						// We have dimensions and reference points
						h = dimToOffset((bow+stern)/2);
						w = dimToOffset((stern+port)/2);
						int f = (int) Math.ceil(h/2);
						int[] xPos = { -h, -h, h-f, h, h-f, -h };
						int[] yPos = { -w, w, w, 0, -w, -w };
						poly = new OMPoly(location.getPos().getLatitude(), location.getPos().getLongitude(), xPos, yPos, 0);
						poly.setFillPaint(new Color(255,0,0));
					} else if(bow>0 || stern>0 || port>0 || starboard>0){
						// We have only dimensions
						h = dimToOffset((bow+stern)/2);
						w = dimToOffset((stern+port)/2);
						int f = (int) Math.ceil(h/2);
						int[] xPos = { -h, -h, h-f, h, h-f, -h };
						int[] yPos = { -w, w, w, 0, -w, -w };
						poly = new OMPoly(location.getPos().getLatitude(), location.getPos().getLongitude(), xPos, yPos, 0);
						poly.setFillPaint(new Color(0,255,0));
					} else {
						// We don't have anything
						int[] xPos = { -sizeOffset, -sizeOffset, sizeOffset, 2*sizeOffset, sizeOffset, -sizeOffset };
						int[] yPos = { -sizeOffset, sizeOffset, sizeOffset, 0, -sizeOffset, -sizeOffset };
						poly = new OMPoly(location.getPos().getLatitude(), location.getPos().getLongitude(), xPos, yPos, 0);
						poly.setFillPaint(new Color(0,0,255));
					}
					list.add(poly);
					
					if(true){
						double trueHeading = location.getTrueHeading();
						double hdgR = trueHeading;
						int xoffset = (int) Math.floor(Math.cos(hdgR)*40);
						int yoffset = (int) Math.floor(Math.sin(hdgR)*40);
						OMLine line = new OMLine(lat, lon, 0, 0, xoffset, yoffset);
						list.add(line);
					}
					
					if (drawMMSI) {
						// Add MMSI sign
						label = new OMText(0, 0, 0, 0, Long.toString(shipList.get(i).MMSI), font, OMText.JUSTIFY_CENTER);
						label.setLat(lat);
						label.setLon(lon);
						label.setY(4 * sizeOffset);
						label.setData("ID: " + Long.toString(vesselTarget.getMmsi()));
						list.add(label);
					}

					if (staticData != null) {
						callSign = staticData.getCallsign();
						name = staticData.getName();
					} else {
						callSign = "N/A";
						name = "N/A";
					}

					if (drawCallSign) {
						// Add call sign
						label = new OMText(0, 0, 0, 0, Long.toString(shipList.get(i).MMSI), font, OMText.JUSTIFY_RIGHT);
						label.setLat(lat);
						label.setLon(lon);
						label.setX(-2 * sizeOffset);
						label.setY(sizeOffset);
						label.setData("Call Sign: " + callSign);
						list.add(label);
					}

					if (drawName) {
						// Add name
						label = new OMText(0, 0, 0, 0, Long.toString(shipList.get(i).MMSI), font, OMText.JUSTIFY_LEFT);
						label.setLat(lat);
						label.setLon(lon);
						label.setX(2 * sizeOffset);
						label.setY(sizeOffset);
						label.setData("Name: " + name);
						list.add(label);
					}
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
	
	public int dimToOffset(int m){
		int offset = (int) Math.ceil(m/15);
		if(offset<5)
			return 5;
		else if(offset > 10)
			return 10;
		else
			return offset;
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
		jf.setTitle("Ship Info Window");
		jf.setSize(800, 600);
		pane = jf.getContentPane();
		pane.setLayout(new GridLayout(1, 3));

		String fields[] = { "MSI" };
		data = new String[3][1];
		int index = 0;
		for (int i = 0; i < shipList.size(); i++) {
			if (aisHandler.getVesselTargets().containsKey(shipList.get(i).MMSI)) {
				if (shipList.get(i).MMSI == 219653000 || shipList.get(i).MMSI == 219282000 || shipList.get(i).MMSI == 219173000) {
					data[index][0] = Long.toString(shipList.get(i).MMSI);
					index++;
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
		tf2.setText("");
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
			dst = staticData.getDestination();
			bow = staticData.getDimBow();
			port = staticData.getDimPort();
			starboard = staticData.getDimStarboard();
			stern = staticData.getDimStern();
			draught = staticData.getDraught();
			eta = staticData.getEta();
			imo = staticData.getImo();
			postype = staticData.getPosType();
			shiptype = staticData.getShipType();
		} else {
			callSign = "N/A";
			name = "N/A";
			dst = "N/A";
			bow = 0;
			port = 0;
			starboard = 0;
			stern = 0;
			draught = 0;
			eta = 0;
			imo = 0;
			postype = 0;
			shiptype = null;
		}

		String out = "";
		out += "MMSI: " + data[table.rowAtPoint(point)][0] + "\n";
		out += "Call sign: " + callSign + "\n";
		out += "Name: " + name + "\n";
		out += "Destination: " + dst + "\n";
		out += "Bow: " + bow + "\n";
		out += "Port: " + port + "\n";
		out += "Starboard: " + starboard + "\n";
		out += "Stern: " + stern + "\n";
		out += "Draught: " + draught + "\n";
		out += "Eta: " + eta + "\n";
		out += "Imo: " + imo + "\n";
		out += "Pos-type: " + postype + "\n";
		if (shiptype != null)
			out += "Ship type: " + shiptype + "\n";
		else
			out += "Ship type: None\n";
		out += "AisRouteData: " + vesselTarget.checkAisRouteData() + "\n";
		out += "AisClass: " + vesselTarget.getAisClass().toString() + "\n";
		out += "Last Received: " + vesselTarget.getLastReceived().toString() + "\n";
		out += "Status: " + vesselTarget.getStatus().toString() + "\n";
		out += "Has Intented Route: " + vesselTarget.hasIntendedRoute() + "\n";
		out += "Cog: " + vesselTarget.getPositionData().getCog() + "\n";
		out += "NavStatus: " + vesselTarget.getPositionData().getNavStatus() + "\n";
		out += "PosAcc: " + vesselTarget.getPositionData().getPosAcc() + "\n";
		out += "Rot: " + vesselTarget.getPositionData().getRot() + "\n";
		out += "Sog: " + vesselTarget.getPositionData().getSog() + "\n";
		out += "True Heading: " + vesselTarget.getPositionData().getTrueHeading() + "\n";
		out += "Enum Nav Status: " + vesselTarget.getPositionData().getEnumNavStatus() + "\n";
		out += "Lat: " + vesselTarget.getPositionData().getPos().getLatitude() + "\n";
		out += "Long: " + vesselTarget.getPositionData().getPos().getLongitude() + "\n";
		out += "Has pos: " + vesselTarget.getPositionData().hasPos() + "\n";
		tf2.setText(out);
	}
}
