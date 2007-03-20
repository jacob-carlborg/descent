package descent.internal.compiler.parser;

public class TypePointer extends Type {
	
	public TypePointer(Type next) {
		super(TY.Tpointer, next);
	}
	
	@Override
	public Type semantic(Scope sc, SemanticContext context) {
	    Type n = next.semantic(sc, context);
	    if (n != next) {
	    	deco = null;
	    }
	    next = n;
	    return merge(context);
	}
	
	@Override
	public int getNodeType() {
		return TYPE_POINTER;
	}

}
