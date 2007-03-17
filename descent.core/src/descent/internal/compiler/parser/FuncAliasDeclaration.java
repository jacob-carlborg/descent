package descent.internal.compiler.parser;

public class FuncAliasDeclaration extends FuncDeclaration {
	
	public FuncDeclaration funcalias;
	
	public FuncAliasDeclaration(FuncDeclaration funcalias) {
		this.funcalias = funcalias;
	}
	
	@Override
	public FuncAliasDeclaration isFuncAliasDeclaration() {
		return this;
	}

}
