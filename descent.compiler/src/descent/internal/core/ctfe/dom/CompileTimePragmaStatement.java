package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.Expressions;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.PragmaStatement;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Statement;

public class CompileTimePragmaStatement extends PragmaStatement {
	
	private Expression fMessage;

	public CompileTimePragmaStatement(char[] filename, int lineNumber, IdentifierExp ident, Expressions args, Statement body) {
		super(filename, lineNumber, ident, args, body);
	}
	
	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		try {
			((CompileTimeSemanticContext) context).stepBegin(this, istate);
			
			if (fMessage != null) {
				((CompileTimeSemanticContext) context).message(fMessage.toString());
			}
			
			return null;
		} finally {
			((CompileTimeSemanticContext) context).stepEnd(this, istate);
		}
	}
	
	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		super.semantic(sc, context);
		return this;
	}
	
	@Override
	protected void message(Expression e) {
		this.fMessage = e;
	}

}
