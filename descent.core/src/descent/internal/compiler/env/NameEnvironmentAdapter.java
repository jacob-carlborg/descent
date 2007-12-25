package descent.internal.compiler.env;


public class NameEnvironmentAdapter implements INameEnvironment {

	public void cleanup() {
	}

	public descent.core.ICompilationUnit findCompilationUnit(char[][] compoundName) {
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
