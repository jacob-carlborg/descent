package descent.internal.core.dom;


public class IftypeCondition extends Condition {

	public Type targ;
	public Identifier ident;
	public TOK tok;
	public Type tspec;

	public IftypeCondition(Type targ, Identifier ident, TOK tok, Type tspec) {
		this.targ = targ;
		this.ident = ident;
		this.tok = tok;
		this.tspec = tspec;
	}
	
	public IftypeDeclaration.Kind getKind() {
		if (tok == TOK.TOKreserved) return IftypeDeclaration.Kind.NONE;
		if (tok == TOK.TOKcolon) return IftypeDeclaration.Kind.EXTENDS;
		return IftypeDeclaration.Kind.EQUALS;
	}
	
	@Override
	public int getConditionType() {
		return IFTYPE;
	}

}
