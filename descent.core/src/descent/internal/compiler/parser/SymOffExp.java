package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.TY.*;
import static descent.internal.compiler.parser.MATCH.*;

public class SymOffExp extends Expression {

	public Declaration var;
	public int offset;

	public SymOffExp(Declaration var, int offset) {
		super(TOK.TOKsymoff);
		this.var = var;
		this.offset = offset;
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
						e = new SymOffExp(f, 0);
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
		return SYM_OFF_EXP;
	}

	@Override
	public MATCH implicitConvTo(Type t, SemanticContext context) {
		MATCH result;

	    result = type.implicitConvTo(t, context);

	    if (result == MATCHnomatch)
	    {
		// Look for pointers to functions where the functions are overloaded.
		FuncDeclaration f;

		t = t.toBasetype(context);
		if (type.ty == Tpointer && type.next.ty == Tfunction &&
		    t.ty == Tpointer && t.next.ty == Tfunction)
		{
		    f = var.isFuncDeclaration();
		    if (f != null && f.overloadExactMatch(t.next, context) != null) {
				result = MATCHexact;
			}
		}
	    }
	    return result;
	}

}
