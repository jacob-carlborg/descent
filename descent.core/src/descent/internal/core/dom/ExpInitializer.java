package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IExpressionInitializer;

public class ExpInitializer extends Initializer implements IExpressionInitializer {

	public Expression e;

	public ExpInitializer(Expression e) {
		this.e = e;
		if (e != null)
		{		
			this.startPosition = e.startPosition;
			this.length = e.length;
		}	
	}
	
	public IExpression getExpression() {
		return e;
	}
	
	public int getNodeType0() {
		return EXPRESSION_INITIALIZER;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, e);
		}
		visitor.endVisit(this);
	}

}
