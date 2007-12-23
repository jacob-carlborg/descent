package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class ScopeExp extends Expression {

	public IScopeDsymbol sds;

	public ScopeExp(Loc loc, IScopeDsymbol pkg) {
		super(loc, TOK.TOKimport);
		this.sds = pkg;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sds);
		}
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return SCOPE_EXP;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		TemplateInstance ti;
		IScopeDsymbol sds2;

		boolean loop = true;
		Lagain: while (loop) {
			loop = false;
			ti = sds.isTemplateInstance();
			if (ti != null && context.global.errors == 0) {
				IDsymbol s;
				if (0 == ti.semanticdone) {
					ti.semantic(sc, context);
				}
				s = ti.inst.toAlias(context);
				sds2 = s.isScopeDsymbol();
				if (sds2 == null) {
					Expression e;

					if (ti.withsym != null) {
						// Same as wthis.s
						e = new VarExp(loc, ti.withsym.withstate.wthis);
						e = new DotVarExp(loc, e, s.isDeclaration());
					} else {
						e = new DsymbolExp(loc, s);
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
	public Expression syntaxCopy(SemanticContext context) {
		ScopeExp se = new ScopeExp(loc, (ScopeDsymbol) sds.syntaxCopy(null, context));
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
			buf.writestring(sds.toChars(context));
		}
	}

}
