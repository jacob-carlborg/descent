package descent.internal.compiler.parser;

public abstract class Statement extends ASTNode {
	
	public Statement semantic(Scope sc, SemanticContext context) {
		return this;
	}

}
