package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.ScopeStatement;
import descent.internal.compiler.parser.Statement;

public class CompileTimeScopeStatement extends ScopeStatement {

	public CompileTimeScopeStatement(Loc loc, Statement s) {
		super(loc, s);
	}
	
//	@Override
//	public Expression interpret(InterState istate, SemanticContext context) {
//		try {
//			((CompileTimeSemanticContext) context).stepBegin(this, istate);
//			
//			return super.interpret(istate, context);
//		} finally {
//			((CompileTimeSemanticContext) context).stepEnd(this, istate);
//		}
//	}

}