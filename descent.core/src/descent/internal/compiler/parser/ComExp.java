package descent.internal.compiler.parser;

public class ComExp extends UnaExp {

	public ComExp(Loc loc, Expression e1) {
		super(loc, TOK.TOKtilde, e1);
	}

	@Override
	public int getNodeType() {
		return COM_EXP;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		Expression e;

		if (type == null) {
			super.semantic(sc, context);
			e1 = resolveProperties(sc, e1, context);
			e = op_overload(sc);
			if (e != null) {
				return e;
			}

			e1.checkNoBool(context);
			e1 = e1.checkIntegral(context);
			type = e1.type;
		}
		return this;
	}

}
