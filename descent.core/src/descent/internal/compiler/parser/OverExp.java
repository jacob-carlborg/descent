package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;

public class OverExp extends Expression {
	
	public OverloadSet vars;

	public OverExp(OverloadSet s) {
		super(Loc.ZERO, TOK.TOKoverloadset);
		this.vars = s;
	}

	@Override
	public int getNodeType() {
		return OVER_EXP;
	}

	@Override
	protected void accept0(IASTVisitor visitor) {
	}
	
	@Override
	public Expression toLvalue(Scope sc, Expression e, SemanticContext context) {
		return this;
	}

}
