package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.CompoundDeclarationStatement;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Statements;

public class CompileTimeCompoundDeclarationStatement extends CompoundDeclarationStatement {

	public CompileTimeCompoundDeclarationStatement(Loc loc, Statements statements) {
		super(loc, statements);
	}
	
	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		try {
			((CompileTimeSemanticContext) context).stepBegin(this, istate);
			
			return super.interpret(istate, context);
		} finally {
			((CompileTimeSemanticContext) context).stepEnd(this, istate);
		}
	}

}
