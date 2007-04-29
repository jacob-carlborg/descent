package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IExpression;
import descent.core.domX.IASTVisitor;

public class ExpInitializer extends Initializer {

	public Expression e;

	public ExpInitializer(Expression e) {
		this.e = e;
		if (e != null)
		{		
			this.startPos = e.startPos;
			this.length = e.length;
		}	
	}
	
	public IExpression getExpression() {
		return e;
	}
	
	public int getElementType() {
		return ElementTypes.EXPRESSION_INITIALIZER;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, e);
		}
		visitor.endVisit(this);
	}

}
