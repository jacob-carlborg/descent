package dtool.dom.expressions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ArrayExp;
import dtool.dom.ast.IASTNeoVisitor;

public class ExpArrayIndex extends Expression {

	public Expression array;
	public Expression[] args;
	
	public ExpArrayIndex(ArrayExp elem) {
		convertNode(elem);
		this.array = Expression.convert(elem.e1);
		this.args = Expression.convertMany(elem.arguments);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, array);
			TreeVisitor.acceptChildren(visitor, args);
		}
		visitor.endVisit(this);	 
	}

}
