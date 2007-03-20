package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

public class CompoundStatement extends Statement {
	
	public List<Statement> statements;

	public CompoundStatement(List<Statement> statements) {
		this.statements = statements;
	}
	
	public CompoundStatement(Statement s1, Statement s2) {
		this.statements = new ArrayList<Statement>(2);
		this.statements.add(s1);
		this.statements.add(s2);
	}
	
	@Override
	public int getNodeType() {
		return COMPOUND_STATEMENT;
	}

}
