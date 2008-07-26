package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;

public class LineInitExp extends DefaultInitExp {

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

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		type = Type.tint32;
		return this;
	}

	@Override
	public Expression resolve(Loc loc, Scope sc, SemanticContext context) {
		Expression e = new IntegerExp(loc, loc.linnum, Type.tint32);
		e = e.castTo(sc, type, context);
		return e;
	}

}
