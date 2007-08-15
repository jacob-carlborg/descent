package dtool.dom.expressions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.CondExp;
import dtool.dom.ast.IASTNeoVisitor;

public class ExpCond extends Expression {

	public Expression predExp;
	public Expression trueExp;
	public Expression falseExp;

	public ExpCond(CondExp elem) {
		convertNode(elem);
		this.predExp = Expression.convert(elem.econd); 
		this.trueExp = Expression.convert(elem.e1);
		this.falseExp = Expression.convert(elem.e2); 
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, predExp);
			TreeVisitor.acceptChildren(visitor, trueExp);
			TreeVisitor.acceptChildren(visitor, falseExp);
		}
		visitor.endVisit(this);
	}

}
