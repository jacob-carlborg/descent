package descent.internal.core.ctfe;

import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.Expressions;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Type;

public class CompileTimeFuncDeclaration extends FuncDeclaration {

	public CompileTimeFuncDeclaration(Loc loc, IdentifierExp ident,
			int storage_class, Type type) {
		super(loc, ident, storage_class, type);
	}
	
	@Override
	public Expression interpret(InterState istate, Expressions arguments,
			SemanticContext context) {
		try {
			((CompileTimeSemanticContext) context).enterFunctionInterpret();
			
			return super.interpret(istate, arguments, context);
		} finally {
			((CompileTimeSemanticContext) context).exitFunctionInterpret();
		}
	}

}
