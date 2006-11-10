package descent.ui.builders;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DCompileCommand {
	
	private String dmdPath;
	private boolean compileOnly;
	private boolean enableWarnings;
	private boolean generateDInterfaceFiles;
	private String outputDirectory;
	private String diOutputDirectory;
	private List<String> files;
	private List<String> importPaths;
	
	public void setDmdPath(String dmdPath) {
		this.dmdPath = dmdPath;
	}
	
	public void setCompileOnly(boolean flag) {
		this.compileOnly = flag;
	}
	
	public void setEnableWarnings(boolean enableWarnings) {
		this.enableWarnings = enableWarnings;
	}
	
	public void setGenerateDInterfaceFiles(boolean generateDInterfaceFiles) {
		this.generateDInterfaceFiles = generateDInterfaceFiles;
	}
	
	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}
	
	public void setDInterfaceFileOutputDirectory(String diOutputDirectory) {
		this.diOutputDirectory = diOutputDirectory;
	}
	
	public void addFile(String file) {
		if (files == null) files = new ArrayList<String>();
		this.files.add(file);
	}
	
	public void addImportPath(String path) {
		if (importPaths == null) importPaths = new ArrayList<String>();
		this.importPaths.add(path);
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		if (dmdPath != null && dmdPath.trim().length() > 0) {
			s.append(dmdPath);
			s.append(File.separator);
			s.append("dmd");
			s.append(File.separator);
			s.append("bin");
			s.append(File.separator);
		}
		s.append("dmd");
		if (compileOnly) {
			s.append(" -c");
		}
		if (enableWarnings) {
			s.append(" -w");
		}
		if (generateDInterfaceFiles) {
			s.append(" -H");
			if (diOutputDirectory != null) {
				s.append(" -Hd\"");
				s.append(diOutputDirectory);
				s.append("\"");
			}
		}
		if (outputDirectory != null) {
			s.append(" -od\"");
			s.append(outputDirectory);
			s.append("\"");
		}
		if (importPaths != null) {
			for(String importPath : importPaths) {
				s.append(" -I\"");
				s.append(importPath);
				s.append("\"");
			}
		}
		if (files != null) {
			for(String file : files) {
				s.append(" \"");
				s.append(file);
				s.append("\"");
			}
		}
		
		return s.toString();
	}

}
