package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.TY.*;
import static descent.internal.compiler.parser.MATCH.*;

import org.eclipse.core.runtime.Assert;

public class SymOffExp extends Expression {

	public Declaration var;
	public int offset;

	public SymOffExp(Declaration var, int offset, SemanticContext context) {
		super(TOK.TOKsymoff);
		Assert.isNotNull(var);
		this.var = var;
		this.offset = offset;
		VarDeclaration v = var.isVarDeclaration();
		if (v != null && v.needThis()) {
			error("need 'this' for address of %s", v.toChars());
		}
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
			FuncDeclaration f;

			if (type.ty == Tpointer && type.next.ty == Tfunction
					&& tb.ty == Tpointer && tb.next.ty == Tfunction) {
				f = var.isFuncDeclaration();
				if (f != null) {
					f = f.overloadExactMatch(tb.next, context);
					if (f != null) {
						e = new SymOffExp(f, 0, context);
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
				error("escaping reference to local %s", v.toChars());
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
			v.checkNestedReference(sc, context);
		}
		return this;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		if (offset != 0) {
			buf.printf("(& " + var.toChars() + "+" + offset + ")");
		} else {
			buf.printf("& " + var.toChars());
		}
	}

}
