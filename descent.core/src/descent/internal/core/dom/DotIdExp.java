package descent.internal.core.dom;

import descent.core.dom.IDotIdentifierExpression;
import descent.core.dom.ASTVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.ISimpleName;

public class DotIdExp extends Expression implements IDotIdentifierExpression {

	private final Expression e;
	private final Identifier id;

	public DotIdExp(Expression e, Identifier id) {
		this.e = e;
		this.id = id;
	}
	
	public IExpression getExpression() {
		if (e.getNodeType0() == IExpression.SIMPLE_NAME) {
			// .id
			String s = e.toString();
			if (s.length() == 0) return null;
		}
		return e;
	}
	
	public ISimpleName getName() {
		return id;
	}
	
	public int getNodeType0() {
		return DOT_IDENTIFIER_EXPRESSION;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, e);
			acceptChild(visitor, id);
		}
		visitor.endVisit(this);
	}

}
