package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.IASTVisitor;

public class WithStatement extends Statement {

	public Expression exp;
	public Statement body;
	public VarDeclaration wthis;

	public WithStatement(Loc loc, Expression exp, Statement body) {
		super(loc);
		this.exp = exp;
		this.body = body;
	}
	
	@Override
	public int getNodeType() {
		return WITH_STATEMENT;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}

}
