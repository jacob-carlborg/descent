package descent.internal.compiler.parser;

import java.util.List;

public class TryCatchStatement extends Statement {
	
	public Statement body;
	public List<Catch> catches;

	public TryCatchStatement(Statement body, List<Catch> catches) {
		this.body = body;
		this.catches = catches;
	}
	
	@Override
	public int getNodeType() {
		return TRY_CATCH_STATEMENT;
	}

}
