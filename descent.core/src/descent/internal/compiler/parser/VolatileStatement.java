package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class VolatileStatement extends Statement {
	
	public Statement statement;
	
	public VolatileStatement(Loc loc, Statement statement) {
		super(loc);
		this.statement = statement;
	}
	
	@Override
	public int getNodeType() {
		return VOLATILE_STATEMENT;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, statement);
		}
		visitor.endVisit(this);
	}

}
