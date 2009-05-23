package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Version;
import descent.internal.compiler.parser.VersionSymbol;

public class CompileTimeVersionSymbol extends VersionSymbol {

	public CompileTimeVersionSymbol(Loc loc, IdentifierExp ident, Version version) {
		super(loc, ident, version);
	}

	public CompileTimeVersionSymbol(Loc loc, long level, Version version) {
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
