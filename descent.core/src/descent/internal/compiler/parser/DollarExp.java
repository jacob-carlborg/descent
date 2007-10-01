package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class DollarExp extends IdentifierExp {

	public DollarExp(Loc loc) {
		super(loc, Id.dollar);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	@Override
	public int getNodeType() {
		return DOLLAR_EXP;
	}

}
