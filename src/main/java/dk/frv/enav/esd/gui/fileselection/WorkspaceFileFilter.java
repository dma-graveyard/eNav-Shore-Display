package dk.frv.enav.esd.gui.fileselection;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class WorkspaceFileFilter extends FileFilter {
	// Type of file that should be display in JFileChooser will be set here
	// We choose to display only directory and text file

	@Override
	public boolean accept(File f) {
		return f.isDirectory() || f.getName().toLowerCase().endsWith(".properties");
	}

	// Set description for the type of file that should be display
	public String getDescription() {
		return "Workspace files";
	}

}