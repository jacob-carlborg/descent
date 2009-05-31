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
	
	private final boolean fIgnoreFirstSemanticAnalysis;
	private boolean fDoneSemanticAnalysis;

	public CompileTimeFuncDeclaration(Loc loc, IdentifierExp ident,
			int storage_class, Type type) {
		this(loc, ident, storage_class, type, false);
	}
	
	public CompileTimeFuncDeclaration(Loc loc, IdentifierExp ident,
			int storage_class, Type type, boolean ignoreFirstSemanticAnalysis) {
		super(loc, ident, storage_class, type);
		
		this.fIgnoreFirstSemanticAnalysis = ignoreFirstSemanticAnalysis;
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		if (sc.parent instanceof FuncDeclaration) {
			super.semantic(sc, context);
			return;
		}
		
		if (fIgnoreFirstSemanticAnalysis && !fDoneSemanticAnalysis) {
			fDoneSemanticAnalysis = true;
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
			((CompileTimeSemanticContext) context).enterFunctionInterpret(this);
			
			return super.interpret(istate, arguments, context);
		} finally {
			((CompileTimeSemanticContext) context).exitFunctionInterpret(this);
		}
	}

}
