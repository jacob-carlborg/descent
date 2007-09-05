package descent.internal.compiler.parser;

import melnorme.miscutil.Assert;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class DotExp extends BinExp {

	public DotExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKdot, e1, e2);
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		Assert.fail("accept0 on fake node");
	}

	@Override
	public int getNodeType() {
		return 0;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		e1 = e1.semantic(sc, context);
		e2 = e2.semantic(sc, context);

		if (e2.op == TOK.TOKimport) {
			ScopeExp se = (ScopeExp) e2;
			TemplateDeclaration td = se.sds.isTemplateDeclaration();
			if (null != td) {
				Expression e = new DotTemplateExp(loc, e1, td);
				e = e.semantic(sc, context);
				return e;
			}
		}

		if (null == type) {
			type = e2.type;
		}

		return this;
	}
}
