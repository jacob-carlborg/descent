package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Version;
import descent.internal.compiler.parser.VersionSymbol;

public class CompileTimeVersionSymbol extends VersionSymbol {

	public CompileTimeVersionSymbol(char[] filename, int lineNumber, IdentifierExp ident, Version version) {
		super(filename, lineNumber, ident, version);
	}

	public CompileTimeVersionSymbol(char[] filename, int lineNumber, long level, Version version) {
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
