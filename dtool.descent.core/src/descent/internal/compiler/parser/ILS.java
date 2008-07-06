package descent.internal.compiler.parser;

public enum ILS {
	ILSuninitialized,	// not computed yet
    ILSno,		// cannot inline
    ILSyes,		// can inline
}
