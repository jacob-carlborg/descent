package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class Pragma extends ASTDmdNode {

	@Override
	public int getNodeType() {
		return PRAGMA;
	}

	@Override
	protected void accept0(IASTVisitor visitor) {
	}

}
