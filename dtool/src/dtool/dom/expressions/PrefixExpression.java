package dtool.dom.expressions;

import util.tree.TreeVisitor;
import descent.internal.core.dom.BinaryExpression;
import descent.internal.core.dom.UnaryExpression;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;

public class PrefixExpression extends Expression {
	
	public interface Type {
		
		int ADDRESS = 1;
		int PRE_INCREMENT = 2;
		int PRE_DECREMENT = 3;
		int POINTER = 4;
		int NEGATIVE = 5;
		int POSITIVE = 6;
		int NOT = 7;
		int INVERT = 8;
	}
	
	public Expression exp;

	public int kind;


	public PrefixExpression(UnaryExpression elem, int kind) {
		setSourceRange((ASTNode) elem);
		this.exp = (Expression) DescentASTConverter.convertElem(elem.exp);
		this.kind = kind;
	}

	public PrefixExpression(BinaryExpression elem, int kind) {
		setSourceRange((ASTNode) elem);
		this.exp = (Expression) DescentASTConverter.convertElem(elem.e1);
		this.kind = kind;
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
