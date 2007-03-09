package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IStaticAssertDeclaration;
import descent.core.domX.ASTVisitor;

public class StaticAssert extends Dsymbol implements IStaticAssertDeclaration {
	
	public Expression exp;
	public Expression msg;

	public StaticAssert(Expression exp, Expression msg) {
		this.exp = exp;
		this.msg = msg;
	}

	public int getElementType() {
		return ElementTypes.STATIC_ASSERT;
	}
	
	public IExpression getExpression() {
		return exp;
	}

	public IExpression getMessage() {
		return msg;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, exp);
			TreeVisitor.acceptChild(visitor, msg);
		}
		visitor.endVisit(this);
	}

	

}
