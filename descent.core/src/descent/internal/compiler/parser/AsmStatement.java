package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.BE.BEfallthru;
import static descent.internal.compiler.parser.BE.BEgoto;
import static descent.internal.compiler.parser.BE.BEhalt;
import static descent.internal.compiler.parser.BE.BEreturn;
import static descent.internal.compiler.parser.BE.BEthrow;

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
	
	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	@Override
	public int blockExit(SemanticContext context) {
	    return BEfallthru | BEthrow | BEreturn | BEgoto | BEhalt;
	}
	
	@Override
	public boolean comeFrom() {
		return true;
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
