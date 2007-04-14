package descent.internal.compiler.parser;

import org.eclipse.core.runtime.Assert;

public class UAddExp extends UnaExp {

	public UAddExp(Loc loc, Expression e1) {
		super(loc, TOK.TOKuadd, e1);
	}

	@Override
	public int getNodeType() {
		return UADD_EXP;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		Expression e;

		Assert.isTrue(type == null);
		super.semantic(sc, context);
		e1 = resolveProperties(sc, e1, context);
		e = op_overload(sc);
		if (e != null) {
			return e;
		}
		e1.checkNoBool(context);
		e1.checkArithmetic(context);
		return e1;
	}

}
