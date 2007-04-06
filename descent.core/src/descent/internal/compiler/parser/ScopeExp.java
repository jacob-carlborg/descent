package descent.internal.compiler.parser;

public class ScopeExp extends Expression {

	public ScopeDsymbol sds;

	public ScopeExp(ScopeDsymbol pkg) {
		super(TOK.TOKimport);
		this.sds = pkg;
	}

	@Override
	public int getNodeType() {
		return SCOPE_EXP;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		TemplateInstance ti;
		ScopeDsymbol sds2;

		boolean loop = true;
		Lagain: while (loop) {
			loop = false;
			ti = sds.isTemplateInstance();
			if (ti != null && context.global.errors == 0) {
				Dsymbol s;
				if (!ti.semanticdone) {
					ti.semantic(sc, context);
				}
				s = ti.inst.toAlias(context);
				sds2 = s.isScopeDsymbol();
				if (sds2 == null) {
					Expression e;

					if (ti.withsym != null) {
						// Same as wthis.s
						e = new VarExp(ti.withsym.withstate.wthis);
						e = new DotVarExp(e, s.isDeclaration());
					} else {
						e = new DsymbolExp(s);
					}
					e = e.semantic(sc, context);
					return e;
				}
				if (sds2 != sds) {
					sds = sds2;
					// goto Lagain;
					loop = true;
					continue Lagain;
				}
			} else {
				sds.semantic(sc, context);
			}
		}

		type = Type.tvoid;
		return this;
	}

	@Override
	public Expression syntaxCopy() {
		ScopeExp se = new ScopeExp((ScopeDsymbol) sds.syntaxCopy(null));
		return se;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		if (sds.isTemplateInstance() != null) {
			sds.toCBuffer(buf, hgs, context);
		} else {
			buf.writestring(sds.kind());
			buf.writestring(" ");
			buf.writestring(sds.toChars());
		}
	}

}
