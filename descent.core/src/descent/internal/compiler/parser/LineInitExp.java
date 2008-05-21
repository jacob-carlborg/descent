package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;

public class LineInitExp extends Expression {

	public LineInitExp(Loc loc) {
		super(loc, TOK.TOKline);
	}

	@Override
	public int getNodeType() {
		return LINE_INIT_EXP;
	}

	@Override
	protected void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

}
