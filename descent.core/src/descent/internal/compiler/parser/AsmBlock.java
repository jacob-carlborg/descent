package descent.internal.compiler.parser;

import java.util.List;

public class AsmBlock extends CompoundStatement {

	public AsmBlock(List<Statement> statements) {
		super(statements);
	}
	
	@Override
	public int getNodeType() {
		return ASM_BLOCK;
	}

}
