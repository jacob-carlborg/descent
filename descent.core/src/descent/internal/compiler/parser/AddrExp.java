package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.MATCH.MATCHexact;
import static descent.internal.compiler.parser.MATCH.MATCHnomatch;
import static descent.internal.compiler.parser.TOK.TOKarray;
import static descent.internal.compiler.parser.TOK.TOKdotvar;
import static descent.internal.compiler.parser.TOK.TOKindex;
import static descent.internal.compiler.parser.TOK.TOKint64;
import static descent.internal.compiler.parser.TOK.TOKstar;
import static descent.internal.compiler.parser.TOK.TOKvar;
import static descent.internal.compiler.parser.TY.Tbit;
import static descent.internal.compiler.parser.TY.Tfunction;
import static descent.internal.compiler.parser.TY.Tpointer;
import static descent.internal.compiler.parser.TY.Tsarray;
import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020 
public class AddrExp extends UnaExp {

	public AddrExp(Loc loc, Expression e) {
		super(loc, TOK.TOKaddress, e);
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
			// Look for pointers to functions where the functions are
			// overloaded.
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
		MATCH result;

		result = type.implicitConvTo(t, context);

		if (result == MATCHnomatch) {
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
		}
		return result;
	}

	@Override
	public Expression optimize(int result, SemanticContext context) {
		Expression e;

		e1 = e1.optimize(result, context);
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
				e = new SymOffExp(loc, ve.var, 0, context);
				e.type = type;
				return e;
			}
		}
		if (e1.op == TOKindex) { // Convert &array[n] to &array+n
			IndexExp ae = (IndexExp) e1;

			if (ae.e2.op == TOKint64 && ae.e1.op == TOKvar) {
				integer_t index = ae.e2.toInteger(context);
				VarExp ve = (VarExp) ae.e1;
				if (ve.type.ty == Tsarray && ve.type.next.ty != Tbit
						&& !ve.var.isImportedSymbol()) {
					TypeSArray ts = (TypeSArray) ve.type;
					integer_t dim = ts.dim.toInteger(context);
					if (index.compareTo(0) < 0 || index.compareTo(dim) >= 0) {
						// PERHAPS test this error
						context.acceptProblem(Problem.newSemanticTypeError(
				    			IProblem.ArrayIndexOutOfBounds,
				    			0,
				    			start,
				    			length,
				    			new String[] { 
				    				String.valueOf(index),
				    				String.valueOf(dim),
				    			}));
					}
					e = new SymOffExp(loc, ve.var, index.multiply(ts.next
							.size(context)), context);
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
				error("cannot take address of %s", e1.toChars(context));
				type = Type.tint32;
				assignBinding();
				return this;
			}
			type = e1.type.pointerTo(context);

			// See if this should really be a delegate
			if (e1.op == TOKdotvar) {
				DotVarExp dve = (DotVarExp) e1;
				FuncDeclaration f = dve.var.isFuncDeclaration();

				if (f != null) {
					Expression e;

					e = new DelegateExp(loc, dve.e1, f);
					e = e.semantic(sc, context);
					assignBinding();					
					return e;
				}
			} else if (e1.op == TOKvar) {
				VarExp dve = (VarExp) e1;
				FuncDeclaration f = dve.var.isFuncDeclaration();

				if (f != null && f.isNested()) {
					Expression e;

					e = new DelegateExp(loc, e1, f);
					e = e.semantic(sc, context);
					assignBinding();
					return e;
				}
			} else if (e1.op == TOKarray) {
				if (e1.type.toBasetype(context).ty == Tbit) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.CannotTakeAddressOfBitInArray, 0, start,
							length));
				}
			}
			
			Expression opt = optimize(WANTvalue, context);
			assignBinding();
			return opt;
		}
		
		return this;
	}
	
	@Override
	public ASTDmdNode getBinding() {
		return type;
	}

}
