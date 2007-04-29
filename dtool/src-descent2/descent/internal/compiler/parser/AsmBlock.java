package descent.internal.compiler.parser;

import java.util.List;

public class AsmBlock extends CompoundStatement {

	public AsmBlock(Loc loc, List<Statement> statements) {
		super(loc, statements);
	}
	
	@Override
	public int getNodeType() {
		return ASM_BLOCK;
	}

}
