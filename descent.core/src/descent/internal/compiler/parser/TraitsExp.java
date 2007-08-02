package descent.internal.compiler.parser;

import java.util.List;

public class TraitsExp extends Expression {

	public IdentifierExp ident;
	public List<ASTNode> args;

	public TraitsExp(Loc loc, IdentifierExp ident, List<ASTNode> args) {
		super(loc, TOK.TOKtraits);
		this.ident = ident;
		this.args = args;
	}

	@Override
	public int getNodeType() {
		return TRAITS_EXP;
	}

}
