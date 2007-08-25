package mmrnmhrm.core.build;

// TODO: implement
public class DeeCompilerOptions {

	public static enum EBuildTypes {
		EXECUTABLE,
		LIB_STATIC,
		LIB_DYNAMIC
	}

	public EBuildTypes buildType;

	public IDeeCE compiler;

	public String extraOptions;
	
	public DeeCompilerOptions() {
		buildType = EBuildTypes.EXECUTABLE;
		compiler = DeeCEManager.getDefaultCompiler();
		extraOptions = "";
	}

}
