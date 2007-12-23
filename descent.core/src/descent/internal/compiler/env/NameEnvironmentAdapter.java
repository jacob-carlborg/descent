package descent.internal.compiler.env;

import descent.internal.compiler.parser.IModule;

public class NameEnvironmentAdapter implements INameEnvironment {

	public void cleanup() {
	}

	public IModule findModule(char[][] compoundName) {
		return null;
	}

	public NameEnvironmentAnswer findType(char[][] compoundTypeName) {
		return null;
	}

	public NameEnvironmentAnswer findType(char[] typeName, char[][] packageName) {
		return null;
	}

	public boolean isPackage(char[][] parentPackageName, char[] packageName) {
		return false;
	}

}
