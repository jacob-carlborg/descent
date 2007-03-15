package descent.internal.compiler.parser;

import java.util.List;

import descent.core.IProblemRequestor;
import descent.core.compiler.IProblem;
import descent.core.dom.AST;
import descent.core.dom.Comment;
import descent.core.dom.Pragma;
import descent.core.dom.PublicScanner;

public class Module extends Package {
	
	public AST ast;
	public ModuleDeclaration md;
	public List<IProblem> problems;
	public Comment[] comments;
	public Pragma[] pragmas;
	public int[] lineEnds;
	public PublicScanner scanner;
	public int semanticstarted;	// has semantic() been started?
	public int semanticdone; // has semantic() been done?
	
	public void semantic(IProblemRequestor problemRequestor) {
		semantic(null, problemRequestor);
	}
	
	@Override
	public void semantic(Scope scope, IProblemRequestor problemRequestor) {
		if (semanticstarted != 0)
			return;
		
		semanticstarted = 1;
		
		// Note that modules get their own scope, from scratch.
	    // This is so regardless of where in the syntax a module
	    // gets imported, it is unaffected by context.
		Scope sc = Scope.createGlobal(this, problemRequestor);
		
		/* TODO
		// Add import of "object" if this module isn't "object"
	    if (ident != Id::object)
	    {
		Import *im = new Import(0, NULL, Id::object, NULL, 0);
		members->shift(im);
	    }
	    */
		
	    symtab = new DsymbolTable();
	    if (members != null) {
	    	
	    	// Add all symbols into module's symbol table
	    	for(Dsymbol s : members) {
	    		s.addMember(null, sc.scopesym, 1, problemRequestor);
	    	}
	    	
	    	// Pass 1 semantic routines: do public side of the definition
	    	for(Dsymbol s : members) {
    			s.semantic(sc, problemRequestor);
	    	}
	    	/* TODO
	    	runDeferredSemantic();
	    	 */
	    }

	    sc = sc.pop();
	    sc.pop();
	    
	    semanticdone = semanticstarted;
	}
	
	@Override
	public int kind() {
		return MODULE;
	}

}
