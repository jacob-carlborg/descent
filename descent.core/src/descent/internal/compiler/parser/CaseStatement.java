package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class CaseStatement extends Statement {

	public Expression exp;
	public Statement statement;

	public CaseStatement(Loc loc, Expression exp, Statement s) {
		super(loc);
		this.exp = exp;
		this.statement = s;		
	}
	
	@Override
	public int getNodeType() {
		return CASE_STATEMENT;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, statement);
		}
		visitor.endVisit(this);
	}


}
