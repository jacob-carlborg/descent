package descent.internal.core.dom;

import java.util.List;

import util.tree.TreeVisitor;

import descent.core.dom.IArrayLiteralExpression;
import descent.core.dom.IExpression;
import descent.core.domX.ASTVisitor;

public class ArrayLiteralExp extends Expression implements IArrayLiteralExpression {

	Expression[] args;

	public ArrayLiteralExp(List<Expression> elements) {
		this.args = elements.toArray(new Expression[elements.size()]);
	}
	
	public IExpression[] getArguments() {
		return args;
	}
	
	public int getElementType() {
		return ElementTypes.ARRAY_LITERAL_EXPRESSION;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, args);
		}
		visitor.endVisit(this);
	}

}
