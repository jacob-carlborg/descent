package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.IASTVisitor;

public class GotoStatement extends Statement {

	public IdentifierExp ident;
	public LabelDsymbol label;
	public TryFinallyStatement tf;

	public GotoStatement(Loc loc, IdentifierExp ident) {
		super(loc);
		this.ident = ident;		
	}
	
	@Override
	public int getNodeType() {
		return GOTO_STATEMENT;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
		}
		visitor.endVisit(this);
	}


}
