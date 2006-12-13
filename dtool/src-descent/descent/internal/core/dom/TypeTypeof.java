package descent.internal.core.dom;

import descent.core.dom.IExpression;
import descent.core.dom.ITypeofType;
import descent.core.dom.IElement.ElementTypes;
import descent.core.domX.ASTVisitor;

public class TypeTypeof extends TypeQualified {
	
	public Expression exp;

	public TypeTypeof(Expression exp) {
		super(TY.Ttypeof);
		this.exp = exp;
	}
	
	public int getElementType() {
		return ElementTypes.TYPEOF_TYPE;
	}
	
	public IExpression getExpression() {
		return exp;
	}
	
	@Override
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, exp);
		}
		visitor.endVisit(this);
	}

}
