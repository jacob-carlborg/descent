package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IArrayLiteralExpression;
import descent.core.dom.ASTVisitor;
import descent.core.dom.IExpression;

public class ArrayLiteralExp extends Expression implements IArrayLiteralExpression {

	Expression[] args;

	public ArrayLiteralExp(List<Expression> elements) {
		this.args = elements.toArray(new Expression[elements.size()]);
	}
	
	public IExpression[] getArguments() {
		return args;
	}
	
	public int getElementType() {
		return ARRAY_LITERAL_EXPRESSION;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChildren(visitor, args);
		}
		visitor.endVisit(this);
	}

}
