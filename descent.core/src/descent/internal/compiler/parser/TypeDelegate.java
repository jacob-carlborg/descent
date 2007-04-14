package descent.internal.compiler.parser;

public class TypeDelegate extends Type {
	
	public TypeDelegate(Type next) {
		super(TY.Tdelegate, next);
	}
	
	@Override
	public Type semantic(Loc loc, Scope sc, SemanticContext context) {
		if (deco != null) {			// if semantic() already run
			return this;
	    }
	    next = next.semantic(loc, sc, context);
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
		return TYPE_DELEGATE;
	}

}
