package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IDotIdentifierExpression;
import descent.core.dom.IExpression;
import descent.core.dom.IName;
import descent.core.domX.IASTVisitor;

public class DotIdExp extends Expression implements IDotIdentifierExpression {

	public final Expression e;
	public final Identifier id;

	public DotIdExp(Expression e, Identifier id) {
		this.e = e;
		this.id = id;
	}
	
	public IExpression getExpression() {
		if (e.getElementType() == ElementTypes.IDENTIFIER_EXPRESSION) {
			// .id
			String s = e.toString();
			if (s.length() == 0) return null;
		}
		return e;
	}
	
	public IName getName() {
		return id;
	}
	
	public int getElementType() {
		return ElementTypes.DOT_IDENTIFIER_EXPRESSION;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, e);
			TreeVisitor.acceptChild(visitor, id);
		}
		visitor.endVisit(this);
	}

}
