package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.Assert;
import descent.core.compiler.CharOperation;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.TY.*;
import static descent.internal.compiler.parser.TOK.*;

public class TypeClass extends Type {

	public ClassDeclaration sym;

	public TypeClass(ClassDeclaration sym) {
		super(TY.Tclass, null);
		this.sym = sym;
	}

	public void accept0(IASTVisitor visitor) {
		Assert.fail("Accept0 on fake class");
	}

	@Override
	public boolean isauto() {
		return sym.isauto;
	}

	@Override
	public Expression defaultInit(SemanticContext context) {
		Expression e;
		e = new NullExp(Loc.ZERO);
		e.type = this;
		return e;
	}

	@Override
	public int getNodeType() {
		return TYPE_CLASS;
	}

	@Override
	public Expression dotExp(Scope sc, Expression e, IdentifierExp ident,
			SemanticContext context) {
		int offset;

		Expression b;
		VarDeclaration v;
		Dsymbol s;
		DotVarExp de;
		Declaration d;

		if (e.op == TOKdotexp) {
			DotExp de_ = (DotExp) e;

			if (de_.e1.op == TOKimport) {
				ScopeExp se = (ScopeExp) de_.e1;

				s = se.sds.search(e.loc, ident, 0, context);
				e = de_.e1;
				//goto L1;
			} else {
				s = sym.search(e.loc, ident, 0, context);
			}
		} else {
			s = sym.search(e.loc, ident, 0, context);
		}

		if (CharOperation.equals(ident.ident, Id.tupleof)) {
			/* Create a TupleExp
			 */
			List<Expression> exps = new ArrayList<Expression>(sym.fields.size());
			for (VarDeclaration v_ : sym.fields) {
				Expression fe = new DotVarExp(e.loc, e, v_);
				exps.add(fe);
			}
			e = new TupleExp(e.loc, exps);
			e = e.semantic(sc, context);
			return e;
		}

		//L1:
		if (null == s) {
			// See if it's a base class
			ClassDeclaration cbase;
			for (cbase = sym.baseClass; null != cbase; cbase = cbase.baseClass) {
				if (CharOperation.equals(ident.ident, cbase.ident.ident)) {
					e = new DotTypeExp(Loc.ZERO, e, cbase);
					return e;
				}
			}

			if (CharOperation.equals(ident.ident, Id.classinfo)) {
				Type t;

				if (context.classinfo == null) {
					throw new IllegalStateException(
							"assert(ClassDeclaration.classinfo);");
				}
				t = context.classinfo.type;
				if (e.op == TOKtype || e.op == TOKdottype) {
					if (sym.vclassinfo == null) {
						sym.vclassinfo = new ClassInfoDeclaration(sym, context);
					}
					e = new VarExp(e.loc, sym.vclassinfo);
					e = e.addressOf(sc, context);
					e.type = t; // do this so we don't get redundant dereference
				} else {
					e = new PtrExp(e.loc, e);
					e.type = t.pointerTo(context);
					if (sym.isInterfaceDeclaration() != null) {
						if (sym.isCOMclass())
							error("no .classinfo for COM interface objects");
						e.type = e.type.pointerTo(context);
						e = new PtrExp(e.loc, e);
						e.type = t.pointerTo(context);
					}
					e = new PtrExp(e.loc, e, t);
				}
				return e;
			}

			else if (CharOperation.equals(ident.ident, Id.typeinfo)) {
				if (!context.global.params.useDeprecated) {
					context.acceptProblem(Problem.newSemanticTypeError(IProblem.DeprecatedProperty, 0, ident.start, ident.length, new String[] { "typeinfo", ".typeid(type)" }));
				}
				return getTypeInfo(sc, context);
			}

			else if (CharOperation.equals(ident.ident, Id.outer)
					&& null != sym.vthis) {
				s = sym.vthis;
			}

			else {
				//return getProperty(e.loc, ident);
				return super.dotExp(sc, e, ident, context);
			}
		}

		s = s.toAlias(context);
		v = s.isVarDeclaration();
		if (null != v && v.isConst()) {
			ExpInitializer ei = v.getExpInitializer(context);

			if (null != ei) {
				e = ei.exp.copy(); // need to copy it if it's a StringExp
				e = e.semantic(sc, context);
				return e;
			}
		}

		if (null != s.getType()) {
			return new TypeExp(e.loc, s.getType());
		}

		EnumMember em = s.isEnumMember();
		if (null != em) {
			assert (null != em.value);
			return em.value.copy();
		}

		TemplateMixin tm = s.isTemplateMixin();
		if (null != tm) {
			Expression de_;

			de_ = new DotExp(e.loc, e, new ScopeExp(e.loc, tm));
			de_.type = e.type;
			return de_;
		}

		TemplateDeclaration td = s.isTemplateDeclaration();
		if (null != td) {
			e = new DotTemplateExp(e.loc, e, td);
			e.semantic(sc, context);
			return e;
		}

		d = s.isDeclaration();
		if (null == d) {
			e.error("%s.%s is not a declaration", e.toChars(context), ident
					.toChars());
			return new IntegerExp(e.loc, 1, Type.tint32);
		}

		if (e.op == TOKtype) {
			VarExp ve;

			if (d.needThis()
					&& (null != hasThis(sc) || null == d.isFuncDeclaration())) {
				if (null != sc.func) {
					ClassDeclaration thiscd;
					thiscd = sc.func.toParent().isClassDeclaration();

					if (null != thiscd) {
						ClassDeclaration cd = e.type.isClassHandle();

						if (cd == thiscd) {
							e = new ThisExp(e.loc);
							e = new DotTypeExp(e.loc, e, cd);
							de = new DotVarExp(e.loc, e, d);
							e = de.semantic(sc, context);
							return e;
						} else if ((null == cd || !cd.isBaseOf(thiscd, null,
								context))
								&& null == d.isFuncDeclaration())
							e
									.error(
											"'this' is required, but %s is not a base class of %s",
											e.type.toChars(context), thiscd
													.toChars(context));
					}
				}

				de = new DotVarExp(e.loc, new ThisExp(e.loc), d);
				e = de.semantic(sc, context);
				return e;
			} else if (null != d.isTupleDeclaration()) {
				e = new TupleExp(e.loc, d.isTupleDeclaration(), context);
				;
				e = e.semantic(sc, context);
				return e;
			} else
				ve = new VarExp(e.loc, d);
			return ve;
		}

		if (d.isDataseg(context)) {
			// (e, d)
			VarExp ve;

			accessCheck(sc, e, d, context);
			ve = new VarExp(e.loc, d);
			e = new CommaExp(e.loc, e, ve);
			e.type = d.type;
			return e;
		}

		if (null != d.parent && null != d.toParent().isModule()) {
			// (e, d)
			VarExp ve;

			ve = new VarExp(e.loc, d);
			e = new CommaExp(e.loc, e, ve);
			e.type = d.type;
			return e;
		}

		de = new DotVarExp(e.loc, e, d);
		return de.semantic(sc, context);
	}

	@Override
	public boolean isBaseOf(Type type, int[] poffset, SemanticContext context) {
		if (type.ty == Tclass) {
			ClassDeclaration cd = ((TypeClass) type).sym;
			if (sym.isBaseOf(cd, poffset, context))
				return true;
		}
		return false;
	}

	@Override
	public String toChars(SemanticContext context) {
		return sym.toPrettyChars(context);
	}

	@Override
	public void toCBuffer2(OutBuffer buf, IdentifierExp ident, HdrGenState hgs,
			SemanticContext context) {
		buf.prependstring(sym.toChars(context));
		if (ident != null) {
			buf.writeByte(' ');
			buf.writestring(ident.toChars());
		}
	}

}
