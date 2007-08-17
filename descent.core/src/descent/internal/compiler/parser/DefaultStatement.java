package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class DefaultStatement extends Statement {

	public Statement statement;

	public DefaultStatement(Loc loc, Statement s) {
		super(loc);
		this.statement = s;		
	}
	
	@Override
	public int getNodeType() {
		return DEFAULT_STATEMENT;
	}
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, statement);
		}
		visitor.endVisit(this);
	}

}
