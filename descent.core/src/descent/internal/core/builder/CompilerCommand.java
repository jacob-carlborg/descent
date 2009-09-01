package descent.internal.core.builder;

import java.util.ArrayList;
import java.util.List;

public class CompilerCommand {
	
	private String command;
	private boolean compile;
	private String outputDir;
	private String outputFile;
	private List<String> inFiles;
	private boolean findDependencies;
	private boolean debug;
	
	public CompilerCommand() {
		this.inFiles = new ArrayList<String>();
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}
	
	public void setCompile(boolean compile) {
		this.compile = compile;
	}
	
	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}
	
	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}
	
	public void addInputFile(String file) {
		this.inFiles.add(file);
	}
	
	public void setFindDependencies(boolean findDependencies) {
		this.findDependencies = findDependencies;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(command);
		for(String inFile : inFiles) {
			sb.append(" ").append(inFile);
		}
		if (compile) {
			sb.append(" -c");
		}
		if (outputFile != null) {
			sb.append(" -of\"").append(outputFile).append("\"");
		}
		if (outputDir != null) {
			sb.append(" -od\"").append(outputDir).append("\"");
		}
		if (findDependencies) {
			sb.append(" -v");
		}
		if (debug) {
			sb.append(" -g");
		}
		return sb.toString();
	}

}
