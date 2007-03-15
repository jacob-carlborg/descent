package descent.internal.compiler.parser;

import descent.core.IProblemRequestor;

public class Scope {
	
	public Scope enclosing; // enclosing Scope
	public Module module; // Root module
	public ScopeDsymbol scopesym; // current symbol
	public ScopeDsymbol sd; // if in static if, and declaring new symbols,
	public Dsymbol parent; // parent to use
	public int callSuper; // primitive flow analysis for constructors
	
	public Scope() {
		
	}
	
	public Scope(Scope enclosing) {
		this.module = enclosing.module;
		this.parent = enclosing.parent;
		this.enclosing = enclosing;
		// TODO
	}

	public static Scope createGlobal(Module module, IProblemRequestor problemRequestor) {
		Scope sc;

	    sc = new Scope();
	    sc.module = module;
	    sc.scopesym = new ScopeDsymbol();
	    sc.scopesym.symtab = new DsymbolTable();

	    // Add top level package as member of this global scope
	    Dsymbol m = module;
	    while (m.parent != null) {
	    	m = m.parent;
	    }
	    
	    m.addMember(null, sc.scopesym, 1, problemRequestor);
	    m.parent = null;			// got changed by addMember()

	    // Create the module scope underneath the global scope
	    sc = sc.push(module);
	    sc.parent = module;
	    return sc;
	}
	
	public Scope push() {
		Scope s = new Scope(this);
	    assert(this != s);
	    return s;
	}

	public Scope push(ScopeDsymbol ss) {
		Scope s = push();
	    s.scopesym = ss;
	    return s;
	}
	
	public Scope pop() {
		Scope enc = enclosing;

	    if (enclosing != null) {
	    	enclosing.callSuper |= callSuper;
	    }

	    return enc;
	}

}
