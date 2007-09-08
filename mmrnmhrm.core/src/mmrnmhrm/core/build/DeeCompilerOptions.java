package mmrnmhrm.core.build;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

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
		artifactName = projname;
		outputDir = new Path(defaultOutputFolder());
		buildTool = "bud";
		extraOptions = "";
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
