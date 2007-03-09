package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IStaticAssertStatement;
import descent.core.domX.ASTVisitor;

public class StaticAssertStatement extends Statement implements IStaticAssertStatement {

	public StaticAssert staticAssert;

	public StaticAssertStatement(StaticAssert staticAssert) {
		this.staticAssert = staticAssert;
	}

	public IExpression getExpression() {
		return staticAssert.exp;
	}

	public IExpression getMessage() {
		return staticAssert.msg;
	}
	
	public int getElementType() {
		return ElementTypes.STATIC_ASSERT_STATEMENT;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, staticAssert.exp);
			TreeVisitor.acceptChild(visitor, staticAssert.msg);
		}
		visitor.endVisit(this);
	}

}
