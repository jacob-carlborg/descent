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
	
	private boolean fNotifySemanticAnalaysis;
	private boolean fAlreadyNotified;
	
	public CompileTimeFuncDeclaration(Loc loc, IdentifierExp ident,
			int storage_class, Type type) {
		this(loc, ident, storage_class, type, true);
	}
	
	public CompileTimeFuncDeclaration(Loc loc, IdentifierExp ident,
			int storage_class, Type type, boolean notifySemanticAnalaysis) {
		super(loc, ident, storage_class, type);
		
		fNotifySemanticAnalaysis = notifySemanticAnalaysis;
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		if (!fNotifySemanticAnalaysis && !fAlreadyNotified) {
			fAlreadyNotified = true;
			
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
			((CompileTimeSemanticContext) context).enterFunctionInterpret();
			
			return super.interpret(istate, arguments, context);
		} finally {
			((CompileTimeSemanticContext) context).exitFunctionInterpret();
		}
	}

}
