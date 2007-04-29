package descent.internal.compiler.parser;

import java.util.List;

public class UnrolledLoopStatement extends Statement {
	
	public List<Statement> statements;

    public UnrolledLoopStatement(Loc loc, List<Statement> statements) {
    	super(loc);
    	this.statements = statements;
    }

	@Override
	public int getNodeType() {
		return UNROLLED_LOOP_STATEMENT;
	}

}
