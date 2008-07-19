package descent.internal.compiler.lookup;

import descent.core.IParent;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.DsymbolTable;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;

public interface ILazyAggregate extends ILazy {

	Dsymbol super_search(Loc loc, char[] ident, int flags, SemanticContext context);

	DsymbolTable symtab();

	ModuleBuilder builder();

	IParent getJavaElement();

	void members(Dsymbols dsymbols);

	Dsymbols members();

	Scope semanticScope();
	
	Scope semantic2Scope();
	
	Scope semantic3Scope();

	void symtab(DsymbolTable table);

	boolean isUnlazy();

}
