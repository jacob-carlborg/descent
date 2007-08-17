package descent.internal.compiler.parser;

import java.util.List;

import descent.internal.compiler.parser.ast.IASTVisitor;


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
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		visitor.endVisit(this);
	}


}
