package descent.internal.compiler.parser;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class TryCatchStatement extends Statement {
	
	public Statement body;
	public List<Catch> catches;

	public TryCatchStatement(Loc loc, Statement body, List<Catch> catches) {
		super(loc);
		this.body = body;
		this.catches = catches;
	}
	
	@Override
	public int getNodeType() {
		return TRY_CATCH_STATEMENT;
	}

	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, body);
			TreeVisitor.acceptChildren(visitor, catches);
		}
		visitor.endVisit(this);
	}
}
