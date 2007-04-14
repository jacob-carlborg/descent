package descent.internal.compiler.parser;

import java.util.List;


public class AsmStatement extends Statement {

	public List<Token> toklist;

	public AsmStatement(Loc loc, List<Token> toklist) {
		super(loc);
		this.toklist = toklist;		
	}
	
	@Override
	public int getNodeType() {
		return ASM_STATEMENT;
	}

}
