package descent.internal.compiler.parser;

import melnorme.miscutil.Assert;
import descent.internal.compiler.parser.ast.IASTVisitor;

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
		Assert.fail("accept0 on a fake node");
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
