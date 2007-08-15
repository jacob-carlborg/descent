package dtool.dom.expressions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.CallExp;
import dtool.dom.ast.IASTNeoVisitor;

public class ExpCall extends Expression {

	public Expression callee;
	public Expression[] args;
	
	public ExpCall(CallExp elem) {
		convertNode(elem);
		this.callee = Expression.convert(elem.e1); 
		this.args = Expression.convertMany(elem.arguments);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, callee);
			TreeVisitor.acceptChildren(visitor, args);
		}
		visitor.endVisit(this);
	}

}
