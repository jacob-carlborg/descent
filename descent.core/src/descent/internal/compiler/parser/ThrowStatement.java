package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class ThrowStatement extends Statement {

	public Expression exp;

	public ThrowStatement(Loc loc, Expression exp) {
		super(loc);
		this.exp = exp;		
	}
	
	@Override
	public int getNodeType() {
		return THROW_STATEMENT;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
		}
		visitor.endVisit(this);
	}

}
