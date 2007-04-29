package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IExpression;
import descent.core.domX.IASTVisitor;

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
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, exp);
		}
		visitor.endVisit(this);
	}

}
