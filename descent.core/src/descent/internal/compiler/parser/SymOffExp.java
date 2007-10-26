package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.MATCH.MATCHexact;
import static descent.internal.compiler.parser.MATCH.MATCHnomatch;
import static descent.internal.compiler.parser.TY.Tfunction;
import static descent.internal.compiler.parser.TY.Tpointer;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class SymOffExp extends Expression {

	public Declaration var;
	public integer_t offset;
	
	public SymOffExp(Loc loc, Declaration var, integer_t offset, SemanticContext context) {
		super(loc, TOK.TOKsymoff);
		Assert.isNotNull(var);
		this.var = var;
		this.offset = offset;
		VarDeclaration v = var.isVarDeclaration();
		if (v != null && v.needThis()) {
			context.acceptProblem(Problem.newSemanticTypeError(IProblem.NeedThisForAddressOfSymbol, 0, start, length, new String[] { v.toChars(context) }));
		}
	}

	public SymOffExp(Loc loc, Declaration var, int offset, SemanticContext context) {
		this(loc, var, new integer_t(offset), context);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		melnorme.miscutil.Assert.fail("accept0 on a fake Node");
	}


	@Override
	public Expression castTo(Scope sc, Type t, SemanticContext context) {
		Type tb;

		Expression e = this;

		tb = t.toBasetype(context);
		type = type.toBasetype(context);
		if (tb.singleton != type.singleton) {
			// Look for pointers to functions where the functions are
			// overloaded.
			FuncDeclaration f;

			if (type.ty == Tpointer && type.next.ty == Tfunction
					&& tb.ty == Tpointer && tb.next.ty == Tfunction) {
				f = var.isFuncDeclaration();
				if (f != null) {
					f = f.overloadExactMatch(tb.next, context);
					if (f != null) {
						e = new SymOffExp(loc, f, 0, context);
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
	public void checkEscape(SemanticContext context) {
		VarDeclaration v = var.isVarDeclaration();
		if (v != null) {
			if (!v.isDataseg(context)) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.EscapingReferenceToLocalVariable, 0, start,
						length, new String[] { toChars(context) }));
			}
		}
	}

	@Override
	public int getNodeType() {
		return SYM_OFF_EXP;
	}

	@Override
	public MATCH implicitConvTo(Type t, SemanticContext context) {
		MATCH result;

		result = type.implicitConvTo(t, context);

		if (result == MATCHnomatch) {
			// Look for pointers to functions where the functions are overloaded.
			FuncDeclaration f;

			t = t.toBasetype(context);
			if (type.ty == Tpointer && type.next.ty == Tfunction
					&& t.ty == Tpointer && t.next.ty == Tfunction) {
				f = var.isFuncDeclaration();
				if (f != null && f.overloadExactMatch(t.next, context) != null) {
					result = MATCHexact;
				}
			}
		}
		return result;
	}

	@Override
	public boolean isBool(boolean result) {
		return result ? true : false;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		if (type == null) {
			type = var.type.pointerTo(context);
		}
		VarDeclaration v = var.isVarDeclaration();
		if (v != null) {
			v.checkNestedReference(sc, loc, context);
		}
		return this;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		if (!offset.equals(0)) {
			buf.writestring("(& ");
			buf.writestring(var.toChars(context));
			buf.writestring("+");
			buf.writestring(offset);
			buf.writestring(")");
		} else {
			buf.writestring("& " + var.toChars(context));
		}
	}
	
	@Override
	public boolean isConst() {
		return true;
	}

}
