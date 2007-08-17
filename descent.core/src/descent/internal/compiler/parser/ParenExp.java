package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class ParenExp extends UnaExp {

	public ParenExp(Loc loc, Expression e) {
		super(loc, TOK.TOKlparen, e);
	}
	
	@Override
	public int getNodeType() {
		return PAREN_EXP;
	}
	
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, e1);
		}
		visitor.endVisit(this);
	}

}
