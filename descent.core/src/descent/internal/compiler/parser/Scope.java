package descent.internal.compiler.parser;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.CharOperation;


public class Scope {
	
	public final static int CSXthis_ctor = 	0x0001;
	public final static int CSXsuper_ctor = 0x0002;
	public final static int CSXthis = 		0x0004;
	public final static int CSXsuper = 		0x0008;
	public final static int CSXlabel = 		0x0010;
	public final static int CSXreturn = 	0x0020;
	public final static int CSXany_ctor = 	0x0040;
	
	public final static int SCOPEctor = 	0x0001; // constructor type
	public final static int SCOPEstaticif = 0x0002; // inside static if
	
	public Scope enclosing; 			// enclosing Scope
	public Module module; 				// Root module
	public ScopeDsymbol scopesym; 		// current symbol
	public ScopeDsymbol sd; 			// if in static if, and declaring new symbols,
	public FuncDeclaration func;		// function we are in
	public Dsymbol parent; 				// parent to use
	LabelStatement slabel;	// enclosing labelled statement
	public int callSuper; 				// primitive flow analysis for constructors
	public int structalign;				// alignment for struct members
	public int offset;		// next offset to use in aggregate
	public LINK linkage;
	public PROT protection;				// protection for class members
	public int explicitProtection;		// set if in an explicit protection attribute
    public int stc;						// storage class
    public int intypeof;			// in typeof(exp)
    public boolean inunion;		// we're processing members of a union
    public int incontract;		// we're inside contract code
    public boolean nofree;			// set if shouldn't free it
    public int noctor;			// set if constructor calls aren't allowed
    public int flags;
    public Statement sbreak;		// enclosing statement that supports "break"
    public Statement scontinue;	// enclosing statement that supports "continue"
    public SwitchStatement sw;	// enclosing switch statement
    public ForeachStatement fes;	// if nested function for ForeachStatement, this is it
    public TryFinallyStatement tf;	// enclosing try finally statement
    
    public AnonymousAggregateDeclaration anonAgg;	// for temporary analysis
	
	public Scope() {
		this.linkage = LINK.LINKd;
		this.protection = PROT.PROTpublic;
		this.stc = 0;
	}
	
	public Scope(Scope enclosing) {
		this();
		this.module = enclosing.module;
		this.parent = enclosing.parent;
		this.enclosing = enclosing;
	}

	public static Scope createGlobal(Module module, SemanticContext context) {
		Scope sc;

	    sc = new Scope();
	    sc.module = module;
	    sc.scopesym = new ScopeDsymbol(Loc.ZERO);
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
	
	public Dsymbol search(Loc loc, IdentifierExp ident, Dsymbol[] pscopesym,
			SemanticContext context) {
		Dsymbol s;
		Scope sc;

		//printf("Scope::search(%p, '%s')\n", this, ident.toChars());
		if (CharOperation.equals(ident.ident, Id.empty)) {
			// Look for module scope
			for (sc = this; sc != null; sc = sc.enclosing) {
				assert (sc != sc.enclosing);
				if (sc.scopesym != null) {
					s = sc.scopesym.isModule();
					if (s != null) {
						//printf("\tfound %s.%s\n", s.parent ? s.parent.toChars() : "", s.toChars());
						if (pscopesym != null)
							pscopesym[0] = sc.scopesym;
						return s;
					}
				}
			}
			return null;
		}

		for (sc = this; sc != null; sc = sc.enclosing) {
			assert (sc != sc.enclosing);
			if (sc.scopesym != null) {
				//printf("\tlooking in scopesym '%s', kind = '%s'\n", sc.scopesym.toChars(), sc.scopesym.kind());
				s = sc.scopesym.search(loc, ident, 0, context);
				if (s != null) {
					 if ((context.global.params.warnings || context.global.params.Dversion > 1)
							&& CharOperation.equals(ident.ident, Id.length)
							&& sc.scopesym.isArrayScopeSymbol() != null
							&& sc.enclosing != null
							&& sc.enclosing.search(loc, ident, null, context) != null) {
						/* TODO semantic
						if (context.global.params.warnings) {
							fprintf(stdmsg, "warning - ");
						}
						error("array 'length' hides other 'length' name in outer scope");
						*/
					}

					// printf("\tfound %s.%s, kind = '%s'\n", s.parent ?
					// s.parent.toChars() : "", s.toChars(), s.kind());
					if (pscopesym != null)
						pscopesym[0] = sc.scopesym;
					return s;
				}
			}
		}

		return null;
	}
	
	public Dsymbol insert(Dsymbol s) {
		Scope sc;

		for (sc = this; sc != null; sc = sc.enclosing) {
			if (sc.scopesym != null) {
				if (sc.scopesym.symtab == null)
					sc.scopesym.symtab = new DsymbolTable();
				return sc.scopesym.symtab.insert(s);
			}
		}
		Assert.isTrue(false);
		return null;
	}
	
	public void setNoFree() {
		Scope sc;
		for (sc = this; sc != null; sc = sc.enclosing) {
			sc.nofree = true;
		}
	}

	public void mergeCallSuper(int cs) {
		// This does a primitive flow analysis to support the restrictions
		// regarding when and how constructors can appear.
		// It merges the results of two paths.
		// The two paths are callSuper and cs; the result is merged into
		// callSuper.

		if (cs != callSuper) {
			boolean a;
			boolean b;

			callSuper |= cs & (CSXany_ctor | CSXlabel);
			if ((cs & CSXreturn) != 0) {
			} else if ((callSuper & CSXreturn) != 0) {
				callSuper = cs | (callSuper & (CSXany_ctor | CSXlabel));
			} else {
				a = (cs & (CSXthis_ctor | CSXsuper_ctor)) != 0;
				b = (callSuper & (CSXthis_ctor | CSXsuper_ctor)) != 0;
				if (a != b) {
					error("one path skips constructor");
				}
				callSuper |= cs;
			}
		}
	}
	    
	public void error(String s) {
		throw new IllegalStateException("Problem reporting not implemented");
	}

	public AggregateDeclaration getStructClassScope() {
		Scope sc;

		for (sc = this; sc != null; sc = sc.enclosing) {
			AggregateDeclaration ad;

			if (sc.scopesym != null) {
				ad = sc.scopesym.isClassDeclaration();
				if (ad != null)
					return ad;
				else {
					ad = sc.scopesym.isStructDeclaration();
					if (ad != null)
						return ad;
				}
			}
		}
		return null;
	}

}
