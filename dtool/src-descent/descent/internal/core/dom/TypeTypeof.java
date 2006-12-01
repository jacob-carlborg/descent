package descent.internal.core.dom;

import descent.core.dom.IExpression;
import descent.core.dom.ITypeofType;
import descent.core.domX.ASTVisitor;

public class TypeTypeof extends TypeQualified implements ITypeofType {
	
	public Expression exp;

	public TypeTypeof(Expression exp) {
		super(TY.Ttypeof);
		this.exp = exp;
	}
	
	public int getElementType() {
		return TYPEOF_TYPE;
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