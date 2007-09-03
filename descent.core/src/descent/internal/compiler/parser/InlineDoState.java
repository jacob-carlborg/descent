package descent.internal.compiler.parser;

import java.util.List;

// DMD 1.020
public class InlineDoState {

	public VarDeclaration vthis;
	public List<ASTDmdNode> from; // old Dsymbols
	public List<ASTDmdNode> to; // parallel array of new Dsymbols
	public Dsymbol parent; // new parent

}
