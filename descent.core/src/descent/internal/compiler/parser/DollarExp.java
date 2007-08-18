package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;

public class DollarExp extends Expression {

	public DollarExp(Loc loc) {
		super(loc, TOK.TOKdollar);
	}
	
	@Override
	public int getNodeType() {
		return DOLLAR_EXP;
	}
	
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

}
