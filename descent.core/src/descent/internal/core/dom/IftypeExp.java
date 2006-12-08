package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IIsExpression;
import descent.core.dom.ISimpleName;
import descent.core.dom.ITypeSpecialization;
import descent.core.dom.IType;

public class IftypeExp extends Expression implements IIsExpression {

	private DmdType targ;
	private DmdType tspec;
	private Identifier ident;
	private TypeSpecialization typeSpecialization;

	public IftypeExp(DmdType targ, Identifier ident, TOK tok, DmdType tspec, Token token2) {
		this.targ = targ;
		this.ident = ident;
		this.tspec = tspec;
		if (token2.value != TOK.TOKreserved) {
			typeSpecialization = new TypeSpecialization(token2);
		}
	}
	
	public ISimpleName getIdentifier() {
		return ident;
	}
	
	public IType getType() {
		return targ;
	}
	
	public IType getSpecialization() {
		return tspec;
	}
	
	public ITypeSpecialization getTypeSpecialization() {
		return typeSpecialization;
	}
	
	public int getNodeType0() {
		return IS_EXPRESSION;
	}
	
	@Override
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, targ);
			acceptChild(visitor, targ);
			acceptChild(visitor, ident);
			acceptChild(visitor, typeSpecialization);
		}
		visitor.endVisit(this);
	}

}
