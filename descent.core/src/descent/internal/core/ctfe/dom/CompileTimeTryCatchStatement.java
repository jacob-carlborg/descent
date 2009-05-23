package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.Array;
import descent.internal.compiler.parser.Catch;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Statement;
import descent.internal.compiler.parser.TryCatchStatement;

public class CompileTimeTryCatchStatement extends TryCatchStatement {

	public CompileTimeTryCatchStatement(Loc loc, Statement body, Array<Catch> catches) {
		super(loc, body, catches);
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
