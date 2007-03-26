package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.TY.*;

public class DelegateExp extends UnaExp {

	public FuncDeclaration func;

	public DelegateExp(Expression e, FuncDeclaration func) {
		super(TOK.TOKdelegate, e);
		this.func = func;
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

}
