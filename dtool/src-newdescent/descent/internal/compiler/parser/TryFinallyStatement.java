package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.IASTVisitor;


public class TryFinallyStatement extends Statement {
	
	public Statement body;
	public Statement finalbody;
	public boolean isTryCatchFinally;
	
	public TryFinallyStatement(Loc loc, Statement body, Statement finalbody) {
		this(loc, body, finalbody, false);		
	}

	public TryFinallyStatement(Loc loc, Statement body, Statement finalbody, boolean isTryCatchFinally) {
		super(loc);
		this.body = body;
		this.finalbody = finalbody;
		this.isTryCatchFinally = isTryCatchFinally;		
	}

	@Override
	public int getNodeType() {
		return TRY_FINALLY_STATEMENT;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, body);
			TreeVisitor.acceptChildren(visitor, finalbody);
		}
		visitor.endVisit(this);
	}

}
