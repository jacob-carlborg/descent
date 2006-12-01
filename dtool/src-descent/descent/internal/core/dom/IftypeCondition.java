package descent.internal.core.dom;

import descent.core.dom.IIftypeDeclaration;

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
	
	public int getIftypeCondition() {
		if (tok == TOK.TOKreserved) return IIftypeDeclaration.IFTYPE_NONE;
		if (tok == TOK.TOKcolon) return IIftypeDeclaration.IFTYPE_EXTENDS;
		return IIftypeDeclaration.IFTYPE_EQUALS;
	}
	
	@Override
	public int getConditionType() {
		return IFTYPE;
	}

}
