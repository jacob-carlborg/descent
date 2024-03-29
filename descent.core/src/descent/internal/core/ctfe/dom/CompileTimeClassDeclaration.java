package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.BaseClasses;
import descent.internal.compiler.parser.ClassDeclaration;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;

public class CompileTimeClassDeclaration extends ClassDeclaration {
	
	private final boolean fIgnoreFirstSemanticAnalysis;
	private boolean fDoneSemanticAnalysis;

	public CompileTimeClassDeclaration(char[] filename, int lineNumber, IdentifierExp id, BaseClasses baseclasses) {
		this(filename, lineNumber, id, baseclasses, false);
	}
	
	public CompileTimeClassDeclaration(char[] filename, int lineNumber, IdentifierExp id, BaseClasses baseclasses, boolean ignoreFirstSemanticAnalysis) {
		super(filename, lineNumber, id, baseclasses);
		
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

}
