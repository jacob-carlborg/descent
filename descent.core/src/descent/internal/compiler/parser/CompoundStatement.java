package descent.internal.compiler.parser;

import java.util.List;

public class CompoundStatement extends Statement {
	
	public List<Statement> statements;

	public CompoundStatement(List<Statement> statements) {
		this.statements = statements;
	}
	
	@Override
	public int kind() {
		return COMPOUND_STATEMENT;
	}

}
