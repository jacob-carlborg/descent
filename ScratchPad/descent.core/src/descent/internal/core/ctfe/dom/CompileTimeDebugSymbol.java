package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.DebugSymbol;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Version;

public class CompileTimeDebugSymbol extends DebugSymbol {

	public CompileTimeDebugSymbol(char[] filename, int lineNumber, IdentifierExp ident, Version version) {
		super(filename, lineNumber, ident, version);
	}

	public CompileTimeDebugSymbol(char[] filename, int lineNumber, long level, Version version) {
		super(filename, lineNumber, level, version);
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		try {
			((CompileTimeSemanticContext) context).stepBegin(this, sc);
			
			super.semantic(sc, context);
		} finally {
			((CompileTimeSemanticContext) context).stepEnd(this, sc);
		}
	}

}
