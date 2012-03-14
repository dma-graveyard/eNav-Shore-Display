package dk.frv.enav.test;

import java.util.List;

import com.bbn.openmap.MapHandlerChild;

import dk.frv.enav.esd.ESD;
import dk.frv.enav.esd.ais.AisHandler.AisMessageExtended;
import dk.frv.enav.esd.ais.VesselAisHandler;

public class TestHandler extends MapHandlerChild implements Runnable {

	private static VesselAisHandler aisHandler;

	public TestHandler() {
		ESD.startThread(this, "TestHandler");
	}

	@Override
	public void run() {
		ESD.sleep(2000);

		while (true) {
			ESD.sleep(10000);
			// Update status on targets

			// vesselTargets

			// Iterator iterator = vesselTargets.keySet().iterator();

			List<AisMessageExtended> shipList = aisHandler.getShipList();

			System.out.println("Recieving AIS:");
			for (int i = 0; i < shipList.size(); i++) {
				System.out.println("ID " + shipList.get(i).MMSI + " : "
						+ shipList.get(i).name);
			}
			System.out.println("AIS Recieved");

			// while (iterator.hasNext()) {
			// String key = iterator.next().toString();
			// VesselTarget vessel = vesselTargets.get(key);
			//
			// if (vessel == null){
			// System.out.println("is null");
			// }
			//
			// // String value = vesselTargets.get(key).getStatus().toString();
			//
			// // System.out.println(key + " " + value);
			// System.out.println(key);
			// }

		}

	}

	@Override
	public void findAndInit(Object obj) {
		if (obj instanceof VesselAisHandler) {
			aisHandler = (VesselAisHandler) obj;
		}

	}

}
