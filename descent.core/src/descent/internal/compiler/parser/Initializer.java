package descent.internal.compiler.parser;


public abstract class Initializer extends ASTNode {
	
	public Expression toExpression(SemanticContext context) {
		return null;
	}
	
	public ExpInitializer isExpInitializer() {
		return null;
	}
	
	public VoidInitializer isVoidInitializer() {
		return null;
	}
	
	public Initializer semantic(Scope sc, Type t, SemanticContext context) {
		return this;
	}
	
	public Type inferType(Scope sc, SemanticContext context) {
		error("cannot infer type from initializer");
	    return Type.terror;
	}

}
