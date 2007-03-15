package descent.internal.compiler.parser;

import java.util.List;

public class TryStatement extends Statement {

	public Statement body;
	public List<Catch> catches;
	public Statement finalbody;

	public TryStatement(Statement body, List<Catch> catches, Statement finalbody) {
		this.body = body;
		this.catches = catches;
		this.finalbody = finalbody;		
	}
	
	@Override
	public int kind() {
		return TRY_STATEMENT;
	}

}
