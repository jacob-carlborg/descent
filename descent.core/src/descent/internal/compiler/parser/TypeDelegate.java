package descent.internal.compiler.parser;

public class TypeDelegate extends Type {
	
	public TypeDelegate(Type next) {
		super(TY.Tdelegate, next);
	}
	
	@Override
	public Type semantic(Scope sc, SemanticContext context) {
		if (deco != null) {			// if semantic() already run
			return this;
	    }
	    next = next.semantic(sc, context);
	    return merge(context);
	}
	
	@Override
	public int getNodeType() {
		return TYPE_DELEGATE;
	}

}
