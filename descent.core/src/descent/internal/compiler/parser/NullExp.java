package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.TY.*;
import static descent.internal.compiler.parser.MATCH.*;

public class NullExp extends Expression {
	
	public boolean committed;	// !=0 if type is committed
	
	public NullExp() {
		super(TOK.TOKnull);
	}
	
	@Override
	public int getNodeType() {
		return NULL_EXP;
	}
	
	@Override
	public MATCH implicitConvTo(Type t, SemanticContext context) {
		if (this.type.equals(t)) {
			return MATCHexact;
		}
		// NULL implicitly converts to any pointer type or dynamic array
		if (type.ty == Tpointer && type.next.ty == Tvoid) {
			if (t.ty == Ttypedef) {
				t = ((TypeTypedef) t).sym.basetype;
			}
			if (t.ty == Tpointer || t.ty == Tarray || t.ty == Taarray
					|| t.ty == Tclass || t.ty == Tdelegate) {
				return committed ? MATCHconvert : MATCHexact;
			}
		}
		return super.implicitConvTo(t, context);
	}
	
	@Override
	public boolean isBool(boolean result) {
		return !result;
	}
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		 // NULL is the same as (void *)0
	    if (type == null) {
			type = Type.tvoid.pointerTo(context);
		}
	    return this;
	}

}
