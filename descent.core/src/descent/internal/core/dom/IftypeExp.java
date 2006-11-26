package descent.internal.core.dom;

import descent.core.dom.IDElementVisitor;
import descent.core.dom.IIftypeExpression;
import descent.core.dom.IName;
import descent.core.dom.IStrongType;
import descent.core.dom.IType;

public class IftypeExp extends Expression implements IIftypeExpression {

	private Type targ;
	private Type tspec;
	private Identifier ident;
	private StrongType strongType;

	public IftypeExp(Loc loc, Type targ, Identifier ident, TOK tok, Type tspec, Token token2) {
		this.targ = targ;
		this.ident = ident;
		this.tspec = tspec;
		if (token2.value != TOK.TOKreserved) {
			strongType = new StrongType(token2);
		}
	}
	
	public IName getIdentifier() {
		return ident;
	}
	
	public IType getType() {
		return targ;
	}
	
	public IType getSpecialization() {
		return tspec;
	}
	
	public IStrongType getStrongType() {
		return strongType;
	}
	
	public int getElementType() {
		return IFTYPE_EXPRESSION;
	}
	
	@Override
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, targ);
			acceptChild(visitor, targ);
			acceptChild(visitor, ident);
			acceptChild(visitor, strongType);
		}
		visitor.endVisit(this);
	}

}
