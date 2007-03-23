package descent.internal.compiler.parser;

public abstract class Statement extends ASTNode {
	
	public boolean incontract;
	
	public Statement semantic(Scope sc, SemanticContext context) {
		return this;
	}
	
	public boolean fallOffEnd() {
		return true;
	}

}
