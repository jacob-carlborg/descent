package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class StaticIfCondition extends Condition {

	public Expression exp;

	public StaticIfCondition(Expression exp) {
		this.exp = exp;
	}
	
	@Override
	public int getConditionType() {
		return STATIC_IF;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		buf.writestring("static if(");
	    exp.toCBuffer(buf, hgs, context);
	    buf.writeByte(')');
	}

}
