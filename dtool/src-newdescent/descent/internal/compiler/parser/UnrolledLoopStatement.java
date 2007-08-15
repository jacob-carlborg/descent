package descent.internal.compiler.parser;

import java.util.List;

import melnorme.miscutil.Assert;
import descent.core.domX.IASTVisitor;

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

	public void accept0(IASTVisitor visitor) {
		Assert.fail("Accept0 on a fake node");
	}
}
