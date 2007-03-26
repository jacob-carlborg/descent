package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.TY.*;
import static descent.internal.compiler.parser.MATCH.*;
import static descent.internal.compiler.parser.TOK.*;

public class AddrExp extends UnaExp {

	public AddrExp(Expression e) {
		super(TOK.TOKaddress, e);
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

}
