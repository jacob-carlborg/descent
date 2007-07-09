package descent.internal.core.dom;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IDescentStatement;
import descent.core.domX.IASTVisitor;

public class ThrowStatement extends Statement implements IDescentStatement {

	public final Expression exp;

	public ThrowStatement(Expression exp) {
		this.exp = exp;
	}
	
	public IExpression getExpression() {
		return exp;
	}
	
	public int getElementType() {
		return ElementTypes.THROW_STATEMENT;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, exp);
		}
		visitor.endVisit(this);
	}

}
