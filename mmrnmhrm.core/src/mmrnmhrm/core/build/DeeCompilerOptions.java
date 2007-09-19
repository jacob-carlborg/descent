package mmrnmhrm.core.build;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

public class DeeCompilerOptions {

	public static enum EBuildTypes {
		EXECUTABLE,
		LIB_STATIC,
		LIB_DYNAMIC
	}

	public EBuildTypes buildType;
	public String artifactName;
	/** Project relative path */
	public IPath outputDir;
	public String buildTool;
	public String extraOptions;
	
	public DeeCompilerOptions(String projname) {
		buildType = EBuildTypes.EXECUTABLE;
		artifactName = projname + getOSExtension();
		outputDir = new Path(defaultOutputFolder());
		buildTool = "bud";
		extraOptions = "";
	}

	private static String getOSExtension() {
		if(Platform.getOS().equals(Platform.OS_WIN32))
			return ".exe";
		return "";
	}

	private String defaultOutputFolder() {
		return "bin";
	}
	
	@Override
	public DeeCompilerOptions clone() {
		DeeCompilerOptions options = new DeeCompilerOptions(artifactName);
		options.buildType = buildType;
		options.artifactName = artifactName;
		options.outputDir = outputDir;
		options.buildTool = buildTool;
		options.extraOptions = extraOptions;
		return options;
	}
}
