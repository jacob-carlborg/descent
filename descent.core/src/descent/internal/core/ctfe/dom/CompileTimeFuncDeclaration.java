package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.Expressions;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Type;

public class CompileTimeFuncDeclaration extends FuncDeclaration {
	
	public CompileTimeFuncDeclaration(Loc loc, IdentifierExp ident,
			int storage_class, Type type) {
		super(loc, ident, storage_class, type);
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		if (sc.parent instanceof FuncDeclaration) {
			super.semantic(sc, context);
			return;
		}
		
		try {
			((CompileTimeSemanticContext) context).stepBegin(this, sc);
			
			super.semantic(sc, context);
		} finally {
			((CompileTimeSemanticContext) context).stepEnd(this, sc);
		}
	}
	
	@Override
	public Expression interpret(InterState istate, Expressions arguments,
			SemanticContext context) {
		try {
			if (javaElement == null) {
				((CompileTimeSemanticContext) context).enterFunctionInterpret();
			}
			
			return super.interpret(istate, arguments, context);
		} finally {
			if (javaElement == null) {
				((CompileTimeSemanticContext) context).exitFunctionInterpret();
			}
		}
	}

}
