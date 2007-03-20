package descent.internal.compiler.parser;

import java.util.List;

public class PragmaDeclaration extends AttribDeclaration {

	public List<Expression> args;

	public PragmaDeclaration(IdentifierExp ident, List<Expression> args, List<Dsymbol> decl) {
		super(decl);
		this.ident = ident;
		this.args = args;
	}
	
	@Override
	public int getNodeType() {
		return PRAGMA_DECLARATION;
	}

}
