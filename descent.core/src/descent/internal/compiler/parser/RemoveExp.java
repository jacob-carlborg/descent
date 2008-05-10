package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;


public class RemoveExp extends BinExp {

	public RemoveExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKremove, e1, e2);
	}

	@Override
	public int getNodeType() {
		return 0;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
}
