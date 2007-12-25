package descent.internal.compiler.parser;

public class Comparisons {
	
	public static boolean equals(IClassDeclaration c1, IClassDeclaration c2) {
		return c1.type().getSignature().equals(c2.type().getSignature());
	}

}
