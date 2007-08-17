package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class SynchronizedStatement extends Statement {

	public Expression exp;
	public Statement body;

	public SynchronizedStatement(Loc loc, Expression exp, Statement body) {
		super(loc);
		this.exp = exp;
		this.body = body;		
	}
	
	@Override
	public int getNodeType() {
		return SYNCHRONIZED_STATEMENT;
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
