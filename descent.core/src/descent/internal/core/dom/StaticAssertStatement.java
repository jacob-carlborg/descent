package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IStaticAssertStatement;

public class StaticAssertStatement extends Statement implements IStaticAssertStatement {

	public StaticAssert staticAssert;

	public StaticAssertStatement(StaticAssert staticAssert) {
		this.staticAssert = staticAssert;
	}

	public IExpression getExpression() {
		return staticAssert.getExpression();
	}

	public IExpression getMessage() {
		return staticAssert.getMessage();
	}
	
	public int getNodeType0() {
		return STATIC_ASSERT_STATEMENT;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, staticAssert.getExpression());
			acceptChild(visitor, staticAssert.getMessage());
		}
		visitor.endVisit(this);
	}

}
