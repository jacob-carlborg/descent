package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.TY.Tdelegate;
import static descent.internal.compiler.parser.TY.Tfunction;
import static descent.internal.compiler.parser.TY.Tpointer;
import static descent.internal.compiler.parser.TY.Tstruct;

// DMD 1.020
public class DelegateExp extends UnaExp {

	public FuncDeclaration func;

	public DelegateExp(Loc loc, Expression e, FuncDeclaration f) {
		super(loc, TOK.TOKdelegate, e);
		this.func = f;
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
	public Expression castTo(Scope sc, Type t, SemanticContext context) {
		Type tb;
		Expression e = this;

		tb = t.toBasetype(context);
		type = type.toBasetype(context);
		if (tb != type) {
			// Look for delegates to functions where the functions are
			// overloaded.
			FuncDeclaration f;

			if (type.ty == Tdelegate && type.next.ty == Tfunction
					&& tb.ty == Tdelegate && tb.next.ty == Tfunction) {
				if (func != null) {
					f = func.overloadExactMatch(tb.next, context);
					if (f != null) {
						int[] offset = { 0 };
						if (f.tintro != null
								&& f.tintro.next.isBaseOf(f.type.next, offset,
										context) && offset[0] != 0) {
							error("cannot form delegate due to covariant return type");
						}
						e = new DelegateExp(loc, e1, f);
						e.type = t;
						return e;
					}
					if (func.tintro != null) {
						error("cannot form delegate due to covariant return type");
					}
				}
			}
			e = super.castTo(sc, t, context);
		} else {
			int[] offset = { 0 };

			if (func.tintro != null
					&& func.tintro.next.isBaseOf(func.type.next, offset,
							context) && offset[0] != 0) {
				error("cannot form delegate due to covariant return type");
			}
		}
		e.type = t;
		return e;
	}

	@Override
	public int getNodeType() {
		return DELEGATE_EXP;
	}

	@Override
	public MATCH implicitConvTo(Type t, SemanticContext context) {
		MATCH result;

		result = type.implicitConvTo(t, context);

		if (result.ordinal() == 0) {
			// Look for pointers to functions where the functions are
			// overloaded.

			t = t.toBasetype(context);
			if (type.ty == Tdelegate && type.next.ty == Tfunction
					&& t.ty == Tdelegate && t.next.ty == Tfunction) {
				if (func != null
						&& func.overloadExactMatch(t.next, context) != null) {
					result = MATCH.MATCHexact;
				}
			}
		}
		return result;
	}

	@Override
	public int inlineCost(InlineCostState ics, SemanticContext context) {
		return COST_MAX;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		if (type == null) {
			e1 = e1.semantic(sc, context);
			type = new TypeDelegate(func.type);
			type = type.semantic(loc, sc, context);
			//	-----------------
			/* For func, we need to get the
			 * right 'this' pointer if func is in an outer class, but our
			 * existing 'this' pointer is in an inner class.
			 * This code is analogous to that used for variables
			 * in DotVarExp::semantic().
			 */
			AggregateDeclaration ad = func.toParent().isAggregateDeclaration();

			boolean loop = true;
			L10: while (loop) {
				loop = false;
				Type t = e1.type;
				if (func.needThis()
						&& ad != null
						&& !(t.ty == Tpointer && t.next.ty == Tstruct && ((TypeStruct) t.next).sym == ad)
						&& !(t.ty == Tstruct && ((TypeStruct) t).sym == ad)) {
					ClassDeclaration cd = ad.isClassDeclaration();
					ClassDeclaration tcd = t.isClassHandle();

					if (cd == null || tcd == null
							|| !(tcd == cd || cd.isBaseOf(tcd, null, context))) {
						if (tcd != null && tcd.isNested()) { // Try again with outer scope

							e1 = new DotVarExp(loc, e1, tcd.vthis);
							e1 = e1.semantic(sc, context);
							// goto L10;
							loop = true;
							continue L10;
						}
						error("this for %s needs to be type %s not type %s",
								func.toChars(context), ad.toChars(context), t
										.toChars(context));
					}
				}
			}
			//	-----------------
		}
		return this;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writeByte('&');
		if (!func.isNested()) {
			expToCBuffer(buf, hgs, e1, PREC.PREC_primary, context);
			buf.writeByte('.');
		}
		buf.writestring(func.toChars(context));
	}

}
