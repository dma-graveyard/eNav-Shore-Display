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
package dk.frv.enav.esd.settings;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.bbn.openmap.util.PropUtils;

import dk.frv.enav.esd.gui.JMapFrame;
import dk.frv.enav.esd.settings.AisSettings;
import dk.frv.enav.esd.settings.EnavSettings;
import dk.frv.enav.esd.settings.NavSettings;
import dk.frv.enav.esd.settings.SensorSettings;

/**
 * Settings class
 */
public class Settings implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(Settings.class);
	
	private String settingsFile = "settings.properties";
	private String defaultWorkSpace = "workspaces/default.workspace";
	private String workspaceFile = "";
	
	private GuiSettings guiSettings = new GuiSettings();
	
	
	private MapSettings mapSettings = new MapSettings();
	private SensorSettings sensorSettings = new SensorSettings();
	private NavSettings navSettings = new NavSettings();
	private AisSettings aisSettings = new AisSettings();
	private EnavSettings enavSettings = new EnavSettings();
	private Workspace workspace = new Workspace();
	
	
	public Settings() {
		
	}
	
	public Settings(String settingsFile) {
		this.settingsFile = settingsFile;
	}
		
	/**
	 * Load the settings files as well as the workspace files
	 */
	public void loadFromFile() {
		// Open properties file
		Properties props = new Properties();
		if (!PropUtils.loadProperties(props, ".", settingsFile)) {
			LOG.info("No settings file found");
			return;
		}
		aisSettings.readProperties(props);
//		enavSettings.readProperties(props);
		guiSettings.readProperties(props);
		mapSettings.readProperties(props);
//		navSettings.readProperties(props);
		sensorSettings.readProperties(props);
		
		
		workspaceFile = guiSettings.getWorkspace();
		
		if (workspaceFile != null){
		
		//Load default workspace - will ALWAYS load from workspaces folder
		Properties workspaceProp = new Properties();
		if (!PropUtils.loadProperties(workspaceProp, ".", workspaceFile)) {
			LOG.info("No workspace file found - reverting to default");
			System.out.println("No workspace file found - reverting to default - " + workspaceFile + " was invalid");
			PropUtils.loadProperties(workspaceProp, ".", defaultWorkSpace);
			guiSettings.setWorkspace(defaultWorkSpace);
		}		
		workspace.readProperties(workspaceProp);
		}
	}

	/**
	 * Load a workspace
	 * @param parent
	 * @param filename
	 * @return
	 */
	public Workspace loadWorkspace(String parent, String filename){
		Properties workspaceProp = new Properties();
		if (!PropUtils.loadProperties(workspaceProp, parent, filename)) {
			LOG.info("No workspace file found - reverting to default");
			System.out.println("No workspace file found - reverting to default - " + parent + filename + " was invalid");
			PropUtils.loadProperties(workspaceProp, ".", defaultWorkSpace);
		}		
		guiSettings.setWorkspace("workspaces/" + filename);
		workspace = new Workspace();
		workspace.readProperties(workspaceProp);
		return workspace;
	}
	
	/**
	 * Save the settings to the files
	 */
	public void saveToFile() {
		Properties props = new Properties();
//		aisSettings.setProperties(props);
//		enavSettings.setProperties(props);
		guiSettings.setProperties(props);
		mapSettings.setProperties(props);

//		navSettings.setProperties(props);
//		sensorSettings.setProperties(props);
		
		try {
			FileWriter outFile = new FileWriter(settingsFile);
			PrintWriter out = new PrintWriter(outFile);
			out.println("# esd settings saved: " + new Date());
			TreeSet<String> keys = new TreeSet<String>();
			for (Object key : props.keySet()) {
				keys.add((String)key);
			}
			for (String key : keys) {
				out.println(key + "=" + props.getProperty(key));
			}						
			out.close();
		} catch (IOException e) {
			LOG.error("Failed to save settings file");
		}
	}
	
	
	/**
	 * Save the current workspace
	 * @param mapWindows
	 * @param filename
	 */
	public void saveCurrentWorkspace(List<JMapFrame> mapWindows, String filename){
		Properties props = new Properties();
		workspace.setProperties(props, mapWindows);
		try {
			filename = "workspaces/" + filename; 
//			System.out.println("Trying to save to: " + filename);
			FileWriter outFile = new FileWriter(filename);
			PrintWriter out = new PrintWriter(outFile);
			out.println("# workspace settings saved: " + new Date());
			TreeSet<String> keys = new TreeSet<String>();
			for (Object key : props.keySet()) {
				keys.add((String)key);
			}
			for (String key : keys) {
				out.println(key + "=" + props.getProperty(key));
			}						
			out.close();
			guiSettings.setWorkspace(filename);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			LOG.error("Failed to save settings file");
		}
	}
	
	public GuiSettings getGuiSettings() {
		return guiSettings;
	}
	
	public MapSettings getMapSettings() {
		return mapSettings;
	}
	
	public String getSettingsFile() {
		return settingsFile;
	}

	public SensorSettings getSensorSettings() {
		return sensorSettings;
	}
	
	public AisSettings getAisSettings() {
		return aisSettings;
	}

	public Workspace getWorkspace(){
		return workspace;
	}
	
	public NavSettings getNavSettings() {
		return navSettings;
	}
	
	public EnavSettings getEnavSettings() {
		return enavSettings;
	}
}
