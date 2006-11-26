package descent.internal.core.dom;

import descent.core.dom.ElementVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.ITypeofType;

public class TypeTypeof extends TypeQualified implements ITypeofType {
	
	public Expression exp;

	public TypeTypeof(Loc loc, Expression exp) {
		super(TY.Ttypeof, loc);
		this.exp = exp;
	}
	
	public int getElementType() {
		return TYPEOF_TYPE;
	}
	
	public IExpression getExpression() {
		return exp;
	}
	
	@Override
	public void accept0(ElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, exp);
		}
		visitor.endVisit(this);
	}

}
