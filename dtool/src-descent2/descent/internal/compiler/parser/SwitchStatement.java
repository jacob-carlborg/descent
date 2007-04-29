package descent.internal.compiler.parser;

import java.util.List;

public class SwitchStatement extends Statement {

	public Expression condition;
	public Statement body;
	
	public DefaultStatement sdefault;

	public List gotoCases;		// array of unresolved GotoCaseStatement's
	public List cases;		// array of CaseStatement's
	public int hasNoDefault;		// !=0 if no default statement

	public SwitchStatement(Loc loc, Expression c, Statement b) {
		super(loc);
		this.condition = c;
		this.body = b;		
	}
	
	@Override
	public int getNodeType() {
		return SWITCH_STATEMENT;
	}

}
