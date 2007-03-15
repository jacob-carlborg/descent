package descent.internal.compiler.parser;

import java.util.List;


public class AsmStatement extends Statement {

	public List<Token> toklist;

	public AsmStatement(List<Token> toklist) {
		this.toklist = toklist;		
	}
	
	@Override
	public int kind() {
		return ASM_STATEMENT;
	}

}
