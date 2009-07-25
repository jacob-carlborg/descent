package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.DsymbolTable;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.ScopeDsymbol;
import descent.internal.compiler.parser.SemanticContext;

/*
 * Since we are keeping the scope when doing semantic analysis for
 * some nodes, it may happend that some variables may be added
 * later to the scope (a variable that's defined further in the
 * same scope, for example). We don't want to suggest those variables,
 * so we make a copy of the scope as it was in the moment.
 * 
 * The scopesym of the scope becomes a ScopeDsymbol with a single member, which is
 * the original scopesym. This is useful to know if we are in a function,
 * module, etc.
 */
public class ScopeCopy {
	
	public static Scope copy(Scope a, SemanticContext context) {
		Scope b = new Scope(context);
		b.scopesym = new ScopeDsymbol();
		b.func = a.func;
		b.parent = a.parent;
		
		if (b.scopesym != null) {
			Dsymbols dsymbols = new Dsymbols(1);
			dsymbols.add(a.scopesym);
			b.scopesym.members = dsymbols;
		}
		
		if (a.scopesym != null && a.scopesym.symtab != null) {
			b.scopesym.symtab = new DsymbolTable(a.scopesym.symtab);
		} else {
			b.scopesym.symtab = new DsymbolTable();
		}
		if (a.enclosing != null) {
			b.enclosing = copy(a.enclosing, context);
		}
		return b;
	}

}
