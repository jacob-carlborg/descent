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
