package descent.internal.compiler.parser;

public abstract class Initializer extends ASTNode {
	
	public Expression toExpression() {
		return null;
	}
	
	public ExpInitializer isExpInitializer() {
		return null;
	}
	
	public VoidInitializer isVoidInitializer() {
		return null;
	}

}
