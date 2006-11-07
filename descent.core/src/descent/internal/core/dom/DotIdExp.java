package descent.internal.core.dom;

import descent.core.dom.IDotIdExpression;
import descent.core.dom.IDElementVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IName;

public class DotIdExp extends Expression implements IDotIdExpression {

	private final Expression e;
	private final Identifier id;

	public DotIdExp(Loc loc, Expression e, Identifier id) {
		this.e = e;
		this.id = id;
	}
	
	public IExpression getExpression() {
		if (e.getExpressionType() == IExpression.EXPRESSION_IDENTIFIER) {
			// .id
			String s = e.toString();
			if (s.length() == 0) return null;
		}
		return e;
	}
	
	public IName getName() {
		return id;
	}
	
	public int getExpressionType() {
		return EXPRESSION_DOT_ID;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, e);
			acceptChild(visitor, id);
		}
		visitor.endVisit(this);
	}

}
