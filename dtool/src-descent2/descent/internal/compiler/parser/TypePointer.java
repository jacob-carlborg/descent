package descent.internal.compiler.parser;

public class TypePointer extends Type {
	
	public TypePointer(Type next) {
		super(TY.Tpointer, next);
	}
	
	@Override
	public Type semantic(Loc loc, Scope sc, SemanticContext context) {
	    Type n = next.semantic(loc, sc, context);
	    if (n != next) {
	    	deco = null;
	    }
	    next = n;
	    return merge(context);
	}
	
	@Override
	public Expression defaultInit(SemanticContext context) {
		Expression e;
	    e = new NullExp(Loc.ZERO);
	    e.type = this;
	    return e;
	}
	
	@Override
	public int getNodeType() {
		return TYPE_POINTER;
	}
	
	@Override
	public String toString() {
		return next + "*";
	}

}
