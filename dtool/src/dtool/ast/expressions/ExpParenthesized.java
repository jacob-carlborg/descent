package dtool.ast.expressions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ParenExp;
import dtool.ast.IASTNeoVisitor;

public class ExpParenthesized extends Expression {

	public Resolvable exp;

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
