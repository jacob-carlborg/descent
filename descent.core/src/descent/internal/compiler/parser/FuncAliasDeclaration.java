package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class FuncAliasDeclaration extends FuncDeclaration {

	public FuncDeclaration funcalias;

	public FuncAliasDeclaration(Loc loc, FuncDeclaration funcalias) {
		super(funcalias.loc, funcalias.ident, funcalias.storage_class,
				funcalias.type);
		if (funcalias == this) {
			throw new IllegalStateException("assert(funcalias != this);");
		}
		this.funcalias = funcalias;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	@Override
	public FuncAliasDeclaration isFuncAliasDeclaration() {
		return this;
	}

	@Override
	public String kind() {
		return "function alias";
	}

}
