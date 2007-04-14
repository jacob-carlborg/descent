package descent.internal.compiler.parser;

import java.util.List;

public class TryCatchStatement extends Statement {
	
	public Statement body;
	public List<Catch> catches;

	public TryCatchStatement(Loc loc, Statement body, List<Catch> catches) {
		super(loc);
		this.body = body;
		this.catches = catches;
	}
	
	@Override
	public int getNodeType() {
		return TRY_CATCH_STATEMENT;
	}

}
