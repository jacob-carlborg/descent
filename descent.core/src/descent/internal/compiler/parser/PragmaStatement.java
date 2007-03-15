package descent.internal.compiler.parser;

import java.util.List;

public class PragmaStatement extends Statement {

	public IdentifierExp ident;
	public List<Expression> args;
	public Statement body;

	public PragmaStatement(IdentifierExp ident, List<Expression> args, Statement body) {
		this.ident = ident;
		this.args = args;
		this.body = body;
	}
	
	@Override
	public int kind() {
		return PRAGMA_STATEMENT;
	}

}
