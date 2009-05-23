package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.DebugSymbol;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Version;

public class CompileTimeDebugSymbol extends DebugSymbol {

	public CompileTimeDebugSymbol(Loc loc, IdentifierExp ident, Version version) {
		super(loc, ident, version);
	}

	public CompileTimeDebugSymbol(Loc loc, long level, Version version) {
		super(loc, level, version);
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
