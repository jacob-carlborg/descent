package descent.internal.core.dom;


public class Scope {

	Scope enclosing;		// enclosing Scope

    Module module;		// Root module
    ScopeDsymbol scopesym;	// current symbol
    ScopeDsymbol sd;		// if in static if, and declaring new symbols,
				// sd gets the addMember()
    FuncDeclaration func;	// function we are in
    Dsymbol parent;		// parent to use
    LabelStatement slabel;	// enclosing labelled statement
    SwitchStatement sw;	// enclosing switch statement
    TryFinallyStatement tf;	// enclosing try finally statement
    Statement sbreak;		// enclosing statement that supports "break"
    Statement scontinue;	// enclosing statement that supports "continue"
    ForeachStatement fes;	// if nested function for ForeachStatement, this is it
    int offset;		    // next offset to use in aggregate
    int inunion;		// we're processing members of a union
    int incontract;		// we're inside contract code
    int nofree;			// set if shouldn't free it
    int noctor;			// set if constructor calls aren't allowed

    int callSuper;		// primitive flow analysis for constructors
    public final static int CSXthis_ctor =	1;	// called this()
    public final static int CSXsuper_ctor = 2;	// called super()
    public final static int CSXthis	= 4;	// referenced this
    public final static int CSXsuper = 8;	// referenced super
    public final static int CSXlabel = 0x10;	// seen a label
    public final static int CSXreturn = 0x20;	// seen a return statement
    public final static int CSXany_ctor = 0x40;	// either this() or super() was called

    int structalign;	// alignment for struct members
    LINK linkage;		// linkage for external functions

    PROT protection;	// protection for class members
    int explicitProtection;	// set if in an explicit protection attribute

    int stc;		// storage class

    int flags;
    public final static int SCOPEctor = 1;	// constructor type
    public final static int SCOPEstaticif = 2;	// inside static if

    // AnonymousAggregateDeclaration anonAgg;	// for temporary analysis

    //DocComment lastdc;		// documentation comment for last symbol at this scope
    int lastoffset;	// offset in docbuf of where to insert next dec
    OutBuffer docbuf;		// buffer for documentation output
	
	public Scope()
	{   // Create root scope

	    this.module = null;
	    this.scopesym = null;
	    this.sd = null;
	    this.enclosing = null;
	    this.parent = null;
	    this.sw = null;
	    this.tf = null;
	    this.sbreak = null;
	    this.scontinue = null;
	    this.fes = null;
	    this.structalign = global.structalign;
	    this.func = null;
	    this.slabel = null;
	    this.linkage = LINK.LINKd;
	    this.protection = PROT.PROTpublic;
	    this.explicitProtection = 0;
	    this.stc = 0;
	    this.offset = 0;
	    this.inunion = 0;
	    this.incontract = 0;
	    this.nofree = 0;
	    this.noctor = 0;
	    this.callSuper = 0;
	    this.flags = 0;
	    //this.anonAgg = null;
	    //this.lastdc = null;
	    this.lastoffset = 0;
	    this.docbuf = null;
	}
	
	public Scope(Scope enclosing)
	{
	    this.module = enclosing.module;
	    this.func   = enclosing.func;
	    this.parent = enclosing.parent;
	    this.scopesym = null;
	    this.sd = null;
	    this.sw = enclosing.sw;
	    this.tf = enclosing.tf;
	    this.sbreak = enclosing.sbreak;
	    this.scontinue = enclosing.scontinue;
	    this.fes = enclosing.fes;
	    this.structalign = enclosing.structalign;
	    this.enclosing = enclosing;
	    this.slabel = null;
	    this.linkage = enclosing.linkage;
	    this.protection = enclosing.protection;
	    this.explicitProtection = enclosing.explicitProtection;
	    this.stc = enclosing.stc;
	    this.offset = 0;
	    this.inunion = enclosing.inunion;
	    this.incontract = enclosing.incontract;
	    this.nofree = 0;
	    this.noctor = enclosing.noctor;
	    this.callSuper = enclosing.callSuper;
	    this.flags = 0;
	    //this.anonAgg = null;
	    //this.lastdc = null;
	    this.lastoffset = 0;
	    this.docbuf = enclosing.docbuf;
	}

	/*
	public static Scope createGlobal(Module module, IProblemCollector collector) {
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
	    m.addMember(null, sc.scopesym, 1, collector);
	    m.parent = null;			// got changed by addMember()

	    // Create the module scope underneath the global scope
	    sc = sc.push(module);
	    sc.parent = module;
	    return sc;
	}
	*/
	
	public Scope push() {
		Scope s;

		s = new Scope(this);
		return s;
	}

	public Scope push(ScopeDsymbol ss) {
		Scope s;

	    s = push();
	    s.scopesym = ss;
	    return s;
	}
	
	public Scope pop()
	{
	    Scope enc = enclosing;

	    if (enclosing != null)
	    	enclosing.callSuper |= callSuper;

	    return enc;
	}

}
