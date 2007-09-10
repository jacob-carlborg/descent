package descent.internal.compiler.parser;

// DMD 1.020
public class InlineDoState {

	public VarDeclaration vthis;
	public Objects from; // old Dsymbols
	public Objects to; // parallel array of new Dsymbols
	public Dsymbol parent; // new parent

}
