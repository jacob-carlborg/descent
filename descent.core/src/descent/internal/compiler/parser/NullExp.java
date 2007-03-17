package descent.internal.compiler.parser;

public class NullExp extends Expression {
	
	public NullExp() {
		super(TOK.TOKnull);
	}
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		 // NULL is the same as (void *)0
	    if (type == null)
	    	type = Type.tvoid.pointerTo(context);
	    return this;
	}
	
	@Override
	public int kind() {
		return NULL_EXP;
	}

}
