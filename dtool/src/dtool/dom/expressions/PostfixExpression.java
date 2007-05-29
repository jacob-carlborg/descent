package dtool.dom.expressions;

import util.tree.TreeVisitor;
import descent.internal.core.dom.BinaryExpression;
import descent.internal.core.dom.UnaryExpression;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;

public class PostfixExpression extends Expression {
	
	public interface Type {
		int POST_INCREMENT = 9;
		int POST_DECREMENT = 10;
	}
	
	public Expression exp;
	
	public int kind;

	
	public PostfixExpression(UnaryExpression elem, int kind) {
		convertNode(elem);
		this.exp = (Expression) DescentASTConverter.convertElem(elem.exp);
		this.kind = kind;
	}

	public PostfixExpression(BinaryExpression elem) {
		setSourceRange((ASTNode) elem);
		this.exp = (Expression) DescentASTConverter.convertElem(elem.e1);
	}

	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, exp);
		}
		visitor.endVisit(this);
	}

}
