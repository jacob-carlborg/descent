package descent.internal.compiler.lookup;

import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.ScopeDsymbol;
import descent.internal.compiler.parser.SemanticContext;

public interface ILazy {
	
	Module getModule();
	
	Scope getSemanticScope();
	
	void runMissingSemantic(Dsymbol sym, SemanticContext context);
	
	ScopeDsymbol asScopeDsymbol();
	
	IdentifierExp getIdent();
	
	boolean isRunningSemantic();

}
