package descent.internal.compiler.parser;


public class Scope {
	
	public final static int CSXthis_ctor = 	0x0001;
	public final static int CSXsuper_ctor = 0x0002;
	public final static int CSXthis = 		0x0004;
	public final static int CSXsuper = 		0x0008;
	public final static int CSXlabel = 		0x0010;
	public final static int CSXreturn = 	0x0020;
	public final static int CSXany_ctor = 	0x0040;
	
	public Scope enclosing; 			// enclosing Scope
	public Module module; 				// Root module
	public ScopeDsymbol scopesym; 		// current symbol
	public ScopeDsymbol sd; 			// if in static if, and declaring new symbols,
	public Dsymbol parent; 				// parent to use
	public int callSuper; 				// primitive flow analysis for constructors
	public PROT protection;				// protection for class members
	public int explicitProtection;		// set if in an explicit protection attribute
    public int stc;						// storage class
    public boolean intypeof;			// in typeof(exp)
	
	public Scope() {
		
	}
	
	public Scope(Scope enclosing) {
		this.module = enclosing.module;
		this.parent = enclosing.parent;
		this.enclosing = enclosing;
		// TODO
	}

	public static Scope createGlobal(Module module, SemanticContext context) {
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
	    
	    m.addMember(null, sc.scopesym, 1, context);
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
