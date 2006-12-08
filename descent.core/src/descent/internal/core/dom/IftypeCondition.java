package descent.internal.core.dom;

import descent.core.dom.IIftypeDeclaration;

public class IftypeCondition extends Condition {

	public DmdType targ;
	public Identifier ident;
	public TOK tok;
	public DmdType tspec;

	public IftypeCondition(DmdType targ, Identifier ident, TOK tok, DmdType tspec) {
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
