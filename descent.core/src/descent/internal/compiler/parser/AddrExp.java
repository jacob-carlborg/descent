package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.MATCH.MATCHexact;
import static descent.internal.compiler.parser.MATCH.MATCHnomatch;
import static descent.internal.compiler.parser.STC.STCmanifest;
import static descent.internal.compiler.parser.TOK.TOKarray;
import static descent.internal.compiler.parser.TOK.TOKcomma;
import static descent.internal.compiler.parser.TOK.TOKdotvar;
import static descent.internal.compiler.parser.TOK.TOKindex;
import static descent.internal.compiler.parser.TOK.TOKint64;
import static descent.internal.compiler.parser.TOK.TOKoverloadset;
import static descent.internal.compiler.parser.TOK.TOKstar;
import static descent.internal.compiler.parser.TOK.TOKvar;
import static descent.internal.compiler.parser.TY.Tbit;
import static descent.internal.compiler.parser.TY.Tdelegate;
import static descent.internal.compiler.parser.TY.Tfunction;
import static descent.internal.compiler.parser.TY.Tpointer;
import static descent.internal.compiler.parser.TY.Tsarray;
import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class AddrExp extends UnaExp {

	public AddrExp(Loc loc, Expression e) {
		super(loc, TOK.TOKaddress, e);
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceE1);
		}
		visitor.endVisit(this);
	}

	@Override
	public Expression castTo(Scope sc, Type t, SemanticContext context) {
		Type tb;

		Expression e = this;

		tb = t.toBasetype(context);
		type = type.toBasetype(context);
		if (same(tb, type, context)) {
			// Look for pointers to functions where the functions are
			// overloaded.
			if (context.isD1()) {
				VarExp ve;
				FuncDeclaration f;
	
				if (type.ty == Tpointer && type.next.ty == Tfunction
						&& tb.ty == Tpointer && tb.next.ty == Tfunction
						&& e1.op == TOKvar) {
					ve = (VarExp) e1;
					f = ve.var.isFuncDeclaration();
					if (f != null) {
						f = f.overloadExactMatch(tb.next, context);
						if (f != null) {
							e = new VarExp(loc, f);
							e.type = f.type;
							e = new AddrExp(loc, e);
							e.type = t;
							return e;
						}
					}
				}
			} else {
				if (e1.op == TOKoverloadset
						&& (t.ty == Tpointer || t.ty == Tdelegate)
						&& t.nextOf().ty == Tfunction) {
					OverExp eo = (OverExp) e1;
					FuncDeclaration f = null;
					for (int i = 0; i < size(eo.vars.a); i++) {
						Dsymbol s = (Dsymbol) eo.vars.a.get(i);
						FuncDeclaration f2 = s.isFuncDeclaration();
						assert (f2 != null);
						if (f2.overloadExactMatch(t.nextOf(), context) != null) {
							if (f != null)
								/*
								 * Error if match in more than one overload set,
								 * even if one is a 'better' match than the
								 * other.
								 */
								ScopeDsymbol.multiplyDefined(loc, f, f2,
										context);
							else
								f = f2;
						}
					}
					if (f != null) {
						f.tookAddressOf++;
						SymOffExp se = new SymOffExp(loc, f, integer_t.ZERO,
								false, context);
						se.semantic(sc, context);
						// Let SymOffExp::castTo() do the heavy lifting
						return se.castTo(sc, t, context);
					}
				}

				if (type.ty == Tpointer && type.nextOf().ty == Tfunction
						&& tb.ty == Tpointer && tb.nextOf().ty == Tfunction
						&& e1.op == TOKvar) {
					VarExp ve = (VarExp) e1;
					FuncDeclaration f = ve.var.isFuncDeclaration();
					if (f != null) {
						throw new IllegalStateException(); // should be
															// SymOffExp instead
//						f = f.overloadExactMatch(tb.nextOf(), context);
//						if (f != null) {
//							e = new VarExp(loc, f);
//							e.type = f.type;
//							e = new AddrExp(loc, e);
//							e.type = t;
//							return e;
//						}
					}
				}
			}
			e = super.castTo(sc, t, context);
		}
		e.type = t;
		return e;
	}

	@Override
	public int getNodeType() {
		return ADDR_EXP;
	}

	@Override
	public MATCH implicitConvTo(Type t, SemanticContext context) {
		MATCH result = type.implicitConvTo(t, context);
		if (result == MATCHnomatch) {
			if (context.isD1()) {
				// Look for pointers to functions where the functions are
				// overloaded.
				VarExp ve;
				FuncDeclaration f;
	
				t = t.toBasetype(context);
				if (type.ty == Tpointer && type.next.ty == Tfunction
						&& t.ty == Tpointer && t.next.ty == Tfunction
						&& e1.op == TOKvar) {
					ve = (VarExp) e1;
					f = ve.var.isFuncDeclaration();
					if (f != null && f.overloadExactMatch(t.next, context) != null) {
						result = MATCHexact;
					}
				}
			} else {
				// Look for pointers to functions where the functions are
				// overloaded.
				t = t.toBasetype(context);

				if (e1.op == TOKoverloadset
						&& (t.ty == Tpointer || t.ty == Tdelegate)
						&& t.nextOf().ty == Tfunction) {
					OverExp eo = (OverExp) e1;
					FuncDeclaration f = null;
					for (int i = 0; i < size(eo.vars.a); i++) {
						Dsymbol s = (Dsymbol) eo.vars.a.get(i);
						FuncDeclaration f2 = s.isFuncDeclaration();
						assert (f2 != null);
						if (f2.overloadExactMatch(t.nextOf(), context) != null) {
							if (f != null)
								/*
								 * Error if match in more than one overload set,
								 * even if one is a 'better' match than the
								 * other.
								 */
								ScopeDsymbol.multiplyDefined(loc, f, f2,
										context);
							else
								f = f2;
							result = MATCHexact;
						}
					}
				}

				if (type.ty == Tpointer && type.nextOf().ty == Tfunction
						&& t.ty == Tpointer && t.nextOf().ty == Tfunction
						&& e1.op == TOKvar) {
					/*
					 * I don't think this can ever happen - it should have been
					 * converted to a SymOffExp.
					 */
					throw new IllegalStateException();
//					VarExp ve = (VarExp) e1;
//					FuncDeclaration f = ve.var.isFuncDeclaration();
//					if (f != null && f.overloadExactMatch(t.nextOf(), context) != null)
//						result = MATCHexact;
				}
			}
		}
		return result;
	}

	@Override
	public Expression optimize(int result, SemanticContext context) {
		Expression e;

		if (context.isD2()) {
		    /* Rewrite &(a,b) as (a,&b)
			 */
			if (e1.op == TOKcomma) {
				CommaExp ce = (CommaExp) e1;
				AddrExp ae = new AddrExp(loc, ce.e2);
				ae.type = type;
				e = new CommaExp(ce.loc, ce.e1, ae);
				e.type = type;
				return e.optimize(result, context);
			}

			if (e1.op == TOKvar) {
				VarExp ve = (VarExp) e1;
				if ((ve.var.storage_class & STCmanifest) != 0) {
					e1 = e1.optimize(result, context);
				}
			} else {
				e1 = e1.optimize(result, context);
			}
		} else {
			e1 = e1.optimize(result, context);
		}
		
		// Convert &ex to ex
		if (e1.op == TOKstar) {
			Expression ex;

			ex = ((PtrExp) e1).e1;
			if (type.equals(ex.type)) {
				e = ex;
			} else {
				e = ex.copy();
				e.type = type;
			}
			return e;
		}
		if (e1.op == TOKvar) {
			VarExp ve = (VarExp) e1;
			if (!ve.var.isOut() && !ve.var.isRef()
					&& !ve.var.isImportedSymbol()) {
				if (context.isD2()) {
				    SymOffExp se = new SymOffExp(loc, ve.var, 0, ve.hasOverloads, context);
				    se.type = type;
				    return se;
				} else {
					e = new SymOffExp(loc, ve.var, 0, context);
					e.copySourceRange(ve);
					e.type = type;
					return e;
				}
			}
		}
		if (e1.op == TOKindex) { // Convert &array[n] to &array+n
			IndexExp ae = (IndexExp) e1;

			if (ae.e2.op == TOKint64 && ae.e1.op == TOKvar) {
				integer_t index = ae.e2.toInteger(context);
				VarExp ve = (VarExp) ae.e1;
				
				boolean condition;
				if (context.isD2()) {
					condition = ve.type.ty == Tsarray && !ve.var.isImportedSymbol();
				} else {
					condition = ve.type.ty == Tsarray && ve.type.next.ty != Tbit
					&& !ve.var.isImportedSymbol();
				}
				
				if (condition) {
					TypeSArray ts = (TypeSArray) ve.type;
					integer_t dim = ts.dim.toInteger(context);
					if ((index.compareTo(0) < 0 || index.compareTo(dim) >= 0)) {
						// PERHAPS test this error
						if (context.acceptsErrors()) {
							context.acceptProblem(Problem.newSemanticTypeError(
					    			IProblem.ArrayIndexOutOfBounds,
					    			this,
				    				String.valueOf(index),
				    				String.valueOf(dim)
					    			));
						}
					}
					if (context.isD2()) {
						e = new SymOffExp(loc, ve.var, index.multiply(ts.nextOf()
								.size(context)), context);
					} else {
						e = new SymOffExp(loc, ve.var, index.multiply(ts.next
								.size(context)), context);
					}
					e.type = type;
					return e;
				}
			}
		}
		return this;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		if (type == null) {
			super.semantic(sc, context);
			e1 = e1.toLvalue(sc, null, context);
			if (e1.type == null) {
				if (context.acceptsErrors()) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.CannotTakeAddressOf, e1, e1.toChars(context)));
				}
				type = Type.tint32;
				return this;
			}
			type = e1.type.pointerTo(context);

			// See if this should really be a delegate
			if (e1.op == TOKdotvar) {
				DotVarExp dve = (DotVarExp) e1;
				FuncDeclaration f = dve.var.isFuncDeclaration();

				if (f != null) {
					Expression e = new DelegateExp(loc, dve.e1, f);
					e = e.semantic(sc, context);	
					return e;
				}
			} else if (e1.op == TOKvar) {
				VarExp dve = (VarExp) e1;
				FuncDeclaration f = dve.var.isFuncDeclaration();

				if (f != null && f.isNested()) {
					Expression e;

					e = new DelegateExp(loc, e1, f);
					e = e.semantic(sc, context);
					return e;
				}
			} else if (e1.op == TOKarray) {
				if (e1.type.toBasetype(context).ty == Tbit) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.CannotTakeAddressOfBitInArray, this));
				}
			}
			
			Expression opt = optimize(WANTvalue, context);
			return opt;
		}
		
		return this;
	}

}
