package descent.internal.compiler.parser;

import java.util.List;

import descent.internal.compiler.parser.ast.IASTVisitor;

public class AsmStatement extends Statement {
	
	private final static char[] EAX = { 'E', 'A', 'X' };

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
	public Statement syntaxCopy(SemanticContext context) {
		return new AsmStatement(loc, toklist);
	}
	
	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		// Descent: for now, don't do full ASM semantic.
		// Clear the error "function must return a result of type ...".
		// TODO Descent semantic do this better?
		FuncDeclaration fd = (FuncDeclaration) sc.parent.isFuncDeclaration();
		if (fd != null) {
			fd.inlineAsm = true;
			fd.hasReturnExp = 1;
		}
		
		return super.semantic(sc, context);
	}


}
