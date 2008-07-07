package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.TOK.TOKdotexp;
import static descent.internal.compiler.parser.TOK.TOKimport;
import static descent.internal.compiler.parser.TOK.TOKtype;

import static descent.internal.compiler.parser.TY.Tclass;
import static descent.internal.compiler.parser.TY.Tpointer;
import static descent.internal.compiler.parser.TY.Tstruct;

// DMD 1.020
public class DotTemplateInstanceExp extends UnaExp {

	public TemplateInstance ti;

	public DotTemplateInstanceExp(Loc loc, Expression e, TemplateInstance ti) {
		super(loc, TOK.TOKdotti, e);
		this.ti = ti;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, e1);
		}
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return DOT_TEMPLATE_INSTANCE_EXP;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		Dsymbol s;
		Dsymbol s2;
		TemplateDeclaration td;
		Expression e;
		char[] id;
		Type t1;
		Expression eleft = null;
		Expression eright;

		e1 = e1.semantic(sc, context);
		t1 = e1.type;
		if (t1 != null) {
			t1 = t1.toBasetype(context);
		}
		if (e1.op == TOKdotexp) {
			DotExp de = (DotExp) e1;
			eleft = de.e1;
			eright = de.e2;
		} else {
			eleft = null;
			eright = e1;
		}
		if (eright.op == TOKimport) {
			s = ((ScopeExp) eright).sds;
		} else if (e1.op == TOKtype) {
			s = t1.isClassHandle();
			if (s == null) {
				if (t1.ty == Tstruct) {
					s = ((TypeStruct) t1).sym;
				} else {
					// goto L1;
					error("template %s is not a member of %s", ti
							.toChars(context), e1.toChars(context));
					return new IntegerExp(loc, 0);
				}
			}
		} else if (t1 != null && (t1.ty == Tstruct || t1.ty == Tclass)) {
			s = t1.toDsymbol(sc, context);
			eleft = e1;
		} else if (t1 != null && t1.ty == Tpointer) {
			t1 = t1.next.toBasetype(context);
			if (t1.ty != Tstruct) {
				// goto L1;
				error("template %s is not a member of %s", ti.toChars(context),
						e1.toChars(context));
				return new IntegerExp(loc, 0);
			}
			s = t1.toDsymbol(sc, context);
			eleft = e1;
		} else {
			// L1:
			error("template %s is not a member of %s", ti.toChars(context), e1
					.toChars(context));
			// goto Lerr;
			return new IntegerExp(loc, 0);
		}

		Assert.isNotNull(s);
		id = ti.idents.get(0).ident;
		s2 = s.search(loc, id, 0, context);
		if (s2 == null) {
			error("template identifier %s is not a member of %s", id,
					s.ident.ident);
			// goto Lerr;
			return new IntegerExp(loc, 0);
		}
		s = s2;
		s.semantic(sc, context);
		s = s.toAlias(context);
		td = s.isTemplateDeclaration();
		if (td == null) {
			error("%s is not a template", id);
			// goto Lerr;
			return new IntegerExp(loc, 0);
		}
		if (context.global.errors > 0) {
			// goto Lerr;
			return new IntegerExp(loc, 0);
		}

		ti.tempdecl = td;

		if (eleft != null) {
			Declaration v;

			ti.semantic(sc, context);
			s = ti.inst.toAlias(context);
			v = s.isDeclaration();
			if (v != null) {
				e = new DotVarExp(loc, eleft, v);
				e = e.semantic(sc, context);
				return e;
			}
		}

		e = new ScopeExp(loc, ti);
		if (eleft != null) {
			e = new DotExp(loc, eleft, e);
		}
		e = e.semantic(sc, context);
		return e;

		// Lerr: return new IntegerExp(0);
	}

	@Override
	public Expression syntaxCopy() {
		DotTemplateInstanceExp de = new DotTemplateInstanceExp(loc, e1
				.syntaxCopy(), (TemplateInstance) ti.syntaxCopy(null));
		return de;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		expToCBuffer(buf, hgs, e1, PREC.PREC_primary, context);
		buf.writeByte('.');
		ti.toCBuffer(buf, hgs, context);
	}

}