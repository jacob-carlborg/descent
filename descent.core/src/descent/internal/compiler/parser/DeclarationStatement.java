package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class DeclarationStatement extends ExpStatement {

	public DeclarationStatement(Loc loc, Expression exp) {
		super(loc, exp);
	}
	
	public DeclarationStatement(Loc loc, Dsymbol s) {
		super(loc, new DeclarationExp(loc, s));
	}
	
	@Override
	public int getNodeType() {
		return DECLARATION_STATEMENT;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ((DeclarationExp) exp).declaration);
		}
		visitor.endVisit(this);
	}

}
