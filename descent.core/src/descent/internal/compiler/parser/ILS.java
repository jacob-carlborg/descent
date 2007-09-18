package descent.internal.compiler.parser;

// DMD 1.020
public enum ILS {
	ILSuninitialized,	// not computed yet
    ILSno,		// cannot inline
    ILSyes,		// can inline
}
