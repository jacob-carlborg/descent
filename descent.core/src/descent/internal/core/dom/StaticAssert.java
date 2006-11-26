package descent.internal.core.dom;

import descent.core.dom.ElementVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IStaticAssertDeclaration;

public class StaticAssert extends Dsymbol implements IStaticAssertDeclaration {
	
	public Expression exp;
	public Expression msg;

	public StaticAssert(Loc loc, Expression exp, Expression msg) {
		this.exp = exp;
		this.msg = msg;
	}

	public int getElementType() {
		return STATIC_ASSERT;
	}
	
	public IExpression getExpression() {
		return exp;
	}

	public IExpression getMessage() {
		return msg;
	}
	
	public void accept0(ElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, exp);
			acceptChild(visitor, msg);
		}
		visitor.endVisit(this);
	}

	

}
