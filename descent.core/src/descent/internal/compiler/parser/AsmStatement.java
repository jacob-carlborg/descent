package descent.internal.compiler.parser;

import java.util.List;

import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
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
	
	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	@Override
	public Statement syntaxCopy() {
		return new AsmStatement(loc, toklist);
	}


}
