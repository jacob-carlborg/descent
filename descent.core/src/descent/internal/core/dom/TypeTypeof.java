package descent.internal.core.dom;

import descent.core.dom.IDElementVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.ITypeofType;

public class TypeTypeof extends TypeQualified implements ITypeofType {
	
	public Expression exp;

	public TypeTypeof(Loc loc, Expression exp) {
		super(TY.Ttypeof, loc);
		this.exp = exp;
	}
	
	@Override
	public int getTypeType() {
		return TYPE_TYPEOF;
	}
	
	public IExpression getExpression() {
		return exp;
	}
	
	@Override
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, exp);
		}
		visitor.endVisit(this);
	}

}
