/*
 * Copyright 2012 Danish Maritime Authority. All rights reserved.
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
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Safety Administration ``AS IS'' 
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
package dk.frv.enav.esd;

import java.beans.beancontext.BeanContextServicesSupport;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.bbn.openmap.PropertyConsumer;

import dk.frv.enav.esd.ais.AisHandler;
import dk.frv.enav.esd.ais.VesselAisHandler;
import dk.frv.enav.esd.gui.MainFrame;
import dk.frv.enav.esd.msi.MsiHandler;
import dk.frv.enav.esd.nmea.NmeaSensor;
import dk.frv.enav.esd.nmea.NmeaTcpSensor;
import dk.frv.enav.esd.services.shore.ShoreServices;
import dk.frv.enav.esd.settings.Settings;
import dk.frv.enav.esd.util.OneInstanceGuard;
import dk.frv.enav.ins.gps.GnssTime;
import dk.frv.enav.ins.gps.GpsHandler;
import dk.frv.enav.ins.nmea.SensorType;
import dk.frv.enav.ins.settings.SensorSettings;

/**
 * Main class with main method.
 * 
 * Starts up components, bean context and GUI.
 * 
 */
public class ESD {

	private static String VERSION;
	private static String MINORVERSION;
	private static Logger LOG;
	private static MainFrame mainFrame;

	private static BeanContextServicesSupport beanHandler;
	private static Settings settings;
	private static Properties properties = new Properties();

	// private static AisHandler aisHandler;
	private static VesselAisHandler aisHandler;
	private static MsiHandler msiHandler;
	private static NmeaSensor aisSensor;
	private static NmeaSensor gpsSensor;
	private static GpsHandler gpsHandler;
	private static ShoreServices shoreServices;

	private static ExceptionHandler exceptionHandler = new ExceptionHandler();

	/**
	 * Function called on shutdown
	 */
	public static void closeApp() {
		closeApp(false);
	}

	/**
	 * Close app routine with possibility for restart - not implemented
	 * @param restart - boolean value for program restart
	 */
	public static void closeApp(boolean restart) {
		// Shutdown routine

		// Chart panels

		// Window state

		// Window state has a
		// Name, Size, Location, Locked status, on top status
		// Chart panel has a zoom level, position

		// Main application

		mainFrame.saveSettings();
		settings.saveToFile();

		// GuiSettings
		// Handler settings
		// routeManager.saveToFile();
		// msiHandler.saveToFile();
		// aisHandler.saveView();

		LOG.info("Closing ESD");
		System.exit(restart ? 2 : 0);
	}

	/**
	 * Creates and shows the GUI
	 */
	private static void createAndShowGUI() {
		// Set the look and feel.
		initLookAndFeel();

		// Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);

		// Create and set up the main window
		mainFrame = new MainFrame();
		mainFrame.setVisible(true);

	}

	/**
	 * Create the plugin components and initialize the beanhandler
	 */
	private static void createPluginComponents() {
		Properties props = getProperties();
		String componentsValue = props.getProperty("esd.plugin_components");
		if (componentsValue == null) {
			return;
		}
		String[] componentNames = componentsValue.split(" ");
		for (String compName : componentNames) {
			String classProperty = compName + ".class";
			String className = props.getProperty(classProperty);
			if (className == null) {
				LOG.error("Failed to locate property " + classProperty);
				continue;
			}
			// Create it if you do...
			try {
				Object obj = java.beans.Beans.instantiate(null, className);
				if (obj instanceof PropertyConsumer) {
					PropertyConsumer propCons = (PropertyConsumer) obj;
					propCons.setProperties(compName, props);
				}
				beanHandler.add(obj);
			} catch (IOException e) {
				LOG.error("IO Exception instantiating class \"" + className + "\"");
			} catch (ClassNotFoundException e) {
				LOG.error("Component class not found: \"" + className + "\"");
			}
		}
	}

	/**
	 * Function used to measure time
	 * @param start - Startime
	 * @return - Elapsed time
	 */
	public static double elapsed(long start) {
		double elapsed = System.nanoTime() - start;
		return elapsed / 1000000.0;
	}

	/**
	 * Return the AisHandler
	 * @return - aisHandler
	 */
	public static AisHandler getAisHandler() {
		return aisHandler;
	}

	
	/**
	 * BeanHandler for program structure
	 * @return - beanHandler
	 */
	public static BeanContextServicesSupport getBeanHandler() {
		return beanHandler;
	}

	
	/**
	 * Return the GpsHandler
	 * @return - GpsHandler
	 */
	public static GpsHandler getGpsHandler() {
		return gpsHandler;
	}

	
	/**
	 * Return the mainFrame gui element
	 * @return - mainframe gui
	 */
	public static MainFrame getMainFrame() {
		return mainFrame;
	}

	/**
	 * Return minor version
	 * @return - minor version
	 */
	public static String getMinorVersion() {
		return MINORVERSION;
	}

	/**
	 * Return the msiHandker
	 * @return - MsiHandler
	 */
	public static MsiHandler getMsiHandler() {
		return msiHandler;
	}

	/**
	 * Returns the properties
	 * @return - properties
	 */
	public static Properties getProperties() {
		return properties;
	}

	/**
	 * Return the settings
	 * @return - settings
	 */
	public static Settings getSettings() {
		return settings;
	}

	/**
	 * Return the shoreService used in shore connections like MSI 
	 * @return - shoreServices
	 */
	public static ShoreServices getShoreServices() {
		return shoreServices;
	}

	/**
	 *  Returns the version
	 * @return - version
	 */
	public static String getVersion() {
		return VERSION;
	}

	/**
	 * Set the used theme using lookAndFeel
	 */
	private static void initLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			LOG.error("Failed to set look and feed: " + e.getMessage());
		}

		// Uncomment for fancy look and feel
		/**
		 * try { for (LookAndFeelInfo info :
		 * UIManager.getInstalledLookAndFeels()) { if
		 * ("Nimbus".equals(info.getName())) {
		 * UIManager.setLookAndFeel(info.getClassName()); break; } } } catch
		 * (Exception e) { // If Nimbus is not available, you can set the GUI to
		 * another look and feel. }
		 **/

	}

	/**
	 * Load the properties file
	 */
	private static void loadProperties() {
		InputStream in = ESD.class.getResourceAsStream("/esd.properties");
		try {
			if (in == null) {
				throw new IOException("Properties file not found");
			}
			properties.load(in);
			in.close();
		} catch (IOException e) {
			LOG.error("Failed to load resources: " + e.getMessage());
		}
	}

	/**
	 * Starts the program by initializing the various threads and spawning the main GUI
	 * @param args
	 */
	public static void main(String[] args) {
		// Set up log4j logging
		DOMConfigurator.configure("log4j.xml");
		LOG = Logger.getLogger(ESD.class);

		// Set default exception handler
		Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);

		VERSION = "2.0 Sprint 2";
		LOG.info("Starting ESD version " + VERSION);
		LOG.info("Copyright (C) 2012 Danish Maritime Safety Administration");
		LOG.info("This program comes with ABSOLUTELY NO WARRANTY.");
		LOG.info("This is free software, and you are welcome to redistribute it under certain conditions.");
		LOG.info("For details see LICENSE file.");

		// Load properties
		loadProperties();

		// Create the bean context (map handler)
		// mapHandler = new MapHandler();
		beanHandler = new BeanContextServicesSupport();

		// Enable GPS timer by adding it to bean context
		GnssTime.init();
		beanHandler.add(GnssTime.getInstance());

		// Start position handler and add to bean context
		gpsHandler = new GpsHandler();
		beanHandler.add(gpsHandler);

		// Create shore services
		shoreServices = new ShoreServices();
		beanHandler.add(shoreServices);
		
		// Create MSI handler
		msiHandler = new MsiHandler();
		beanHandler.add(msiHandler);

		// Load settings or get defaults and add to bean context
		if (args.length > 0) {
			settings = new Settings(args[0]);
		} else {
			settings = new Settings();
		}

		LOG.info("Using settings file: " + settings.getSettingsFile());
		settings.loadFromFile();
		beanHandler.add(settings);

		// Determine if instance already running and if that is allowed
		OneInstanceGuard guard = new OneInstanceGuard("esd.lock");
		if (guard.isAlreadyRunning()) {
			JOptionPane.showMessageDialog(null,
					"One application instance already running. Stop instance or restart computer.", "Error",
					JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}

		// Start sensors
		startSensors();

		// aisHandler = new AisHandler();
		aisHandler = new VesselAisHandler(settings);
		// aisHandler.loadView();
		beanHandler.add(aisHandler);


		// RoundRobinAisTcpReader reader = new RoundRobinAisTcpReader();
		// reader.setCommaseparatedHostPort("192.168.10.250:4001");

		// reader.setTimeout(getInt("ais_source_timeout." + name, "10"));
		// reader.setReconnectInterval(getInt("ais_source_reconnect_interval." +
		// name, "5") * 1000);

		// // Register proprietary handlers
		// reader.addProprietaryFactory(new GatehouseFactory());
		//
		// reader.registerHandler(aisHandler);
		//
		// Create plugin components
		createPluginComponents();

		// Create and show GUI
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				createAndShowGUI();
			}
		});

	}

	/**
	 * Function used to call sleep on a thread
	 * @param ms - time in ms of how long to sleep
	 */
	public static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
		}
	}

	/**
	 * Starts the needed sensors such as the AIS TCP connection
	 */
	private static void startSensors() {
		SensorSettings sensorSettings = settings.getSensorSettings();
		switch (sensorSettings.getAisConnectionType()) {
		case TCP:
			// aisSensor = new NmeaTcpSensor("192.168.10.250", 4001);
			aisSensor = new NmeaTcpSensor("localhost", 4001);
			break;
		default:
			LOG.error("Unknown sensor connection type: " + sensorSettings.getAisConnectionType());
		}

		if (aisSensor != null) {
			aisSensor.addSensorType(SensorType.AIS);
		}

		switch (sensorSettings.getGpsConnectionType()) {
		case TCP:
			gpsSensor = new NmeaTcpSensor(sensorSettings.getGpsHostOrSerialPort(), sensorSettings.getGpsTcpPort());
			break;
		default:
			LOG.error("Unknown sensor connection type: " + sensorSettings.getAisConnectionType());
		}

		if (gpsSensor != null) {
			gpsSensor.addSensorType(SensorType.GPS);
		}
		if (aisSensor != null) {
			aisSensor.setSimulateGps(sensorSettings.isSimulateGps());
			aisSensor.setSimulatedOwnShip(sensorSettings.getSimulatedOwnShip());
			aisSensor.start();
			// Add ais sensor to bean context
			beanHandler.add(aisSensor);
		}
		if (gpsSensor != null && gpsSensor != aisSensor) {
			gpsSensor.setSimulateGps(sensorSettings.isSimulateGps());
			gpsSensor.setSimulatedOwnShip(sensorSettings.getSimulatedOwnShip());
			gpsSensor.start();
			// Add gps sensor to bean context
			beanHandler.add(gpsSensor);
		}

	}

	/**
	 * Function used to create a thread
	 * @param t - class to create thread on
	 * @param name - Thread name
	 */
	public static void startThread(Runnable t, String name) {
		Thread thread = new Thread(t);
		thread.setName(name);
		thread.start();
	}

}
