package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IIsExpression;
import descent.core.dom.IName;
import descent.core.dom.IType;
import descent.core.dom.ITypeSpecialization;
import descent.core.domX.ASTVisitor;

public class IftypeExp extends Expression implements IIsExpression {

	private Type targ;
	private Type tspec;
	private Identifier ident;
	private TypeSpecialization typeSpecialization;

	public IftypeExp(Type targ, Identifier ident, TOK tok, Type tspec, Token token2) {
		this.targ = targ;
		this.ident = ident;
		this.tspec = tspec;
		if (token2.value != TOK.TOKreserved) {
			typeSpecialization = new TypeSpecialization(token2);
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
	
	public ITypeSpecialization getTypeSpecialization() {
		return typeSpecialization;
	}
	
	public int getElementType() {
		return ElementTypes.IS_EXPRESSION;
	}
	
	@Override
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, targ);
			TreeVisitor.acceptChild(visitor, targ);
			TreeVisitor.acceptChild(visitor, ident);
			TreeVisitor.acceptChild(visitor, typeSpecialization);
		}
		visitor.endVisit(this);
	}

}
