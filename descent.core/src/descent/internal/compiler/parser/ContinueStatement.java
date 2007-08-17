package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class ContinueStatement extends Statement {

	public IdentifierExp ident;

	public ContinueStatement(Loc loc, IdentifierExp ident) {
		super(loc);
		this.ident = ident;		
	}
	
	@Override
	public int getNodeType() {
		return CONTINUE_STATEMENT;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
		}
		visitor.endVisit(this);
	}


}
