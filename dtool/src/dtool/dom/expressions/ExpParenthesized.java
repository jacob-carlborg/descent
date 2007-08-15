package dtool.dom.expressions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ParenExp;
import dtool.dom.ast.IASTNeoVisitor;

public class ExpParenthesized extends Expression {

	public Expression exp;

	public ExpParenthesized(ParenExp elem) {
		convertNode(elem);
		this.exp = Expression.convert(elem.e1); 
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
		}
		visitor.endVisit(this);	
	}

}
