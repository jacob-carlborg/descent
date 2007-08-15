package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.IASTVisitor;


public class OnScopeStatement extends Statement {
	
	public TOK tok;
	public Statement statement;

	public OnScopeStatement(Loc loc, TOK tok, Statement statement) {
		super(loc);
		this.tok = tok;
		this.statement = statement;		
	}
	
	@Override
	public int getNodeType() {
		return ON_SCOPE_STATEMENT;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, statement);
		}
		visitor.endVisit(this);
	}


}
