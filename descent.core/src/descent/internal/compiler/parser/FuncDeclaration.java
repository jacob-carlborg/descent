package descent.internal.compiler.parser;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;

public class FuncDeclaration extends Declaration {
	
	private final static boolean BREAKABI = true;
	
	public Statement fensure;
	public Statement frequire;
	public Statement fbody;
	public IdentifierExp outId;
	public int vtblIndex;			// for member functions, index into vtbl[]
	public VarDeclaration vthis;		// 'this' parameter (member and nested)
	public boolean introducing;			// !=0 if 'introducing' function
	public Type tintro;			// if !=NULL, then this is the type
								// of the 'introducing' function
								// this one is overriding
	public Declaration overnext;		// next in overload list
	public Scope scope;		// !=NULL means context to use
	public int semanticRun;			// !=0 if semantic3() had been run
	public DsymbolTable localsymtab;		// used to prevent symbols in different
											// scopes from having the same name
	public ForeachStatement fes;		// if foreach body, this is the foreach
	
	public FuncDeclaration(IdentifierExp ident, int storage_class, Type type) {
		super(ident);
		this.storage_class = storage_class;
		this.type = type;
	}
	
	@Override
	public FuncDeclaration isFuncDeclaration() {
		return this;
	}
	
	public boolean isNested() {
		return ((storage_class & STC.STCstatic) == 0) &&
		   (toParent2().isFuncDeclaration() != null);
	}
	
	@Override
	public AggregateDeclaration isThis() {
		AggregateDeclaration ad;

		ad = null;
		if ((storage_class & STC.STCstatic) == 0) {
			ad = isMember2();
		}
		return ad;
	}
	
	public AggregateDeclaration isMember2() {
		AggregateDeclaration ad;

		ad = null;
		for (Dsymbol s = this; s != null; s = s.parent) {
			ad = s.isMember();
			if (ad != null) {
				break;
			}
			if (s.parent == null || (s.parent.isTemplateInstance() == null)) {
				break;
			}
		}
		return ad;
	}
	
	public boolean isVirtual() {
		return isMember() != null
				&& !(isStatic() || protection == PROT.PROTprivate || protection == PROT.PROTpackage)
				&& toParent().isClassDeclaration() != null;
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		boolean gotoL1 = false;
		boolean gotoL2 = false;
		boolean gotoLmainerr = false;
		
		TypeFunction f;
		StructDeclaration sd;
		ClassDeclaration cd;
		InterfaceDeclaration id;

		if (type.next != null) {
			type = type.semantic(sc, context);
		}

		if (type.ty != TY.Tfunction) {
			error("%s must be a function", toChars());
			return;
		}
		f = (TypeFunction) (type);
		int nparams = Argument.dim(f.parameters, context);

		linkage = sc.linkage;
		// if (!parent)
		{
			// parent = sc.scopesym;
			parent = sc.parent;
		}
		protection = sc.protection;
		storage_class |= sc.stc;

		Dsymbol parent = toParent();

		if (isConst() || isAuto() || isScope()) {
			context.acceptProblem(Problem.newSemanticTypeError("Functions cannot be const or auto", IProblem.IllegalModifier, 0, ident.start, ident.length));
		}

		if (isAbstract() && !isVirtual()) {
			context.acceptProblem(Problem.newSemanticTypeError("Non-virtual functions cannot be abstract", IProblem.IllegalModifier, 0, ident.start, ident.length));
		}

		sd = parent.isStructDeclaration();
		if (sd != null) {
			// Verify no constructors, destructors, etc.
			/* removed since this is in CtorDeclaration::semantic and DtorDeclaration::semantic
			if (isCtorDeclaration() != null) {
				context.acceptProblem(Problem.newSemanticTypeError("Constructors only are for class definitions", IProblem.ConstructorsOnlyForClass, 0, start, "this".length()));
			}
			if (isDtorDeclaration() != null) {
				context.acceptProblem(Problem.newSemanticTypeError("Destructors only are for class definitions", IProblem.ConstructorsOnlyForClass, 0, start, "~this".length()));
			}
			*/
		}

		id = parent.isInterfaceDeclaration();
		if (id != null) {
			storage_class |= STC.STCabstract;

			if (isCtorDeclaration() != null || isDtorDeclaration() != null
					|| isInvariantDeclaration() != null
					|| isUnitTestDeclaration() != null
					|| isNewDeclaration() != null || isDelete()) {
				error("special function not allowed in interface %s", id
						.toChars());
			}
			if (fbody != null) {
				error("function body is not abstract in interface %s", id
						.toChars());
			}
		}

		cd = parent.isClassDeclaration();
		if (cd != null) {
			int vi;
			CtorDeclaration ctor;
			DtorDeclaration dtor;
			InvariantDeclaration inv;

			if (isCtorDeclaration() != null) {
				// ctor = (CtorDeclaration *)this;
				// if (!cd.ctor)
				// cd.ctor = ctor;
				return;
			}

			if ((storage_class & STC.STCabstract) != 0) {
				cd.isabstract = true;
			}

			// if static function, do not put in vtbl[]
			if (!isVirtual()) {
				return;
			}

			// Find index of existing function in vtbl[] to override
			if (cd.baseClass != null) {
				for (vi = 0; vi < cd.baseClass.vtbl.size() && !gotoL1; vi++) {
					FuncDeclaration fdv = ((Dsymbol) cd.vtbl.get(vi))
							.isFuncDeclaration();

					// BUG: should give error if argument types match,
					// but return type does not?


					if (fdv != null && fdv.ident.ident == ident.ident) {
						int cov = type.covariant(fdv.type, context);

						if (cov == 2) {
							error(
									"of type %s overrides but is not covariant with %s of type %s",
									type.toChars(), fdv.toPrettyChars(),
									fdv.type.toChars());
						}
						if (cov == 1) {
							if (fdv.isFinal()) {
								error("cannot override final function %s", fdv
										.toPrettyChars());
							}
							if (fdv.toParent() == parent) {
								// If both are mixins, then error.
								// If either is not, the one that is not
								// overrides
								// the other.
								if (fdv.parent.isClassDeclaration() != null) {
									// goto L1;
									gotoL1 = true;
								}

								if (!gotoL1) {
									if (BREAKABI) {
										if (this.parent.isClassDeclaration() == null) {
											error("multiple overrides of same function");
										}
									} else {
										if (this.parent.isClassDeclaration() == null
												&& isDtorDeclaration() == null) {
											error("multiple overrides of same function");
										}
									}
									error("multiple overrides of same function");
								}
							}
							
							if (!gotoL1) {
								cd.vtbl.set(vi, this);
								vtblIndex = vi;
	
								/*
								 * This works by whenever this function is called,
								 * it actually returns tintro, which gets
								 * dynamically cast to type. But we know that tintro
								 * is a base of type, so we could optimize it by not
								 * doing a dynamic cast, but just subtracting the
								 * isBaseOf() offset if the value is != null.
								 */
	
								if (fdv.tintro != null) {
									tintro = fdv.tintro;
								} else if (!type.equals(fdv.type)) {
									/*
									 * Only need to have a tintro if the vptr
									 * offsets differ
									 */
									int[] offset = { 0 };
									if (fdv.type.next.isBaseOf(type.next, offset)) {
										tintro = fdv.type;
									}
								}
							}
							
							// goto L1;
							gotoL1 = true;
						}
						
						if (!gotoL1) {
							if (cov == 3) {
								cd.sizeok = 2; // can't finish due to forward
												// reference
								return;
							}
						}
					}
				}
			}

			// This is an 'introducing' function.
			if (!gotoL1) {
				if (isFinal()) {
					// Verify this doesn't override previous final function
					if (cd.baseClass != null) {
						Dsymbol s = cd.baseClass.search(ident, 0, context);
						if (s != null) {
							FuncDeclaration f2 = s.isFuncDeclaration();
							f2 = f2.overloadExactMatch(type, context);
							if (f2 != null && f2.isFinal()) {
								context.acceptProblem(Problem.newSemanticTypeError("Cannot override the final function " + ident + " from " + cd.ident , IProblem.CannotOverrideFinalFunctions, 0, ident.start, ident.length));
							}
						}
					}
					cd.vtblFinal.add(this);
				} else {
					// Append to end of vtbl[]
					introducing = true;
					vi = cd.vtbl.size();
					cd.vtbl.add(this);
					vtblIndex = vi;
				}
			}

			// L1: ;

			/*
			 * Go through all the interface bases. If this function is covariant
			 * with any members of those interface functions, set the tintro.
			 */
			for (int i = 0; i < cd.interfaces.size() && !gotoL2; i++) {
				BaseClass b = cd.interfaces.get(i);
				for (vi = 0; vi < b.base.vtbl.size() && !gotoL2; vi++) {
					Dsymbol s = (Dsymbol) b.base.vtbl.get(vi);
					FuncDeclaration fdv = s.isFuncDeclaration();
					if (fdv != null && fdv.ident.ident == ident.ident) {
						int cov = type.covariant(fdv.type, context);
						if (cov == 2) {
							error(
									"of type %s overrides but is not covariant with %s of type %s",
									type.toChars(), fdv.toPrettyChars(),
									fdv.type.toChars());
						}
						if (cov == 1) {
							Type ti = null;

							if (fdv.tintro != null) {
								ti = fdv.tintro;
							}
							else if (!type.equals(fdv.type)) {
								/*
								 * Only need to have a tintro if the vptr
								 * offsets differ
								 */
								int[] offset = { 0 };
								if (fdv.type.next.isBaseOf(type.next, offset)) {
									ti = fdv.type;

								}
							}
							if (ti != null) {
								if (tintro != null && !tintro.equals(ti)) {
									error(
											"incompatible covariant types %s and %s",
											tintro.toChars(), ti.toChars());
								}
								tintro = ti;
							}
							// goto L2;
							gotoL2 = true;							
						}
						if (!gotoL2) {
							if (cov == 3) {
								cd.sizeok = 2; // can't finish due to forward
												// reference
								return;
							}
						}
					}
				}
			}

			if (!gotoL2) {
				if (introducing && isOverride()) {
					context.acceptProblem(Problem.newSemanticTypeError("The function " + ident + " of type " + cd.ident + " must override a superclass method", IProblem.FunctionDoesNotOverrideAny, 0, ident.start, ident.length));
				}
			}

			// L2:	;
		} else if (isOverride() && parent.isTemplateInstance() == null) {
			context.acceptProblem(Problem.newSemanticTypeError("Override only applies to class member functions", IProblem.OverrideOnlyForClassMemberFunctions, 0, ident.start, ident.length));
		}

		/*
		 * Do not allow template instances to add virtual functions to a class.
		 */
		if (isVirtual()) {
			TemplateInstance ti = parent.isTemplateInstance();
			if (ti != null) {
				// Take care of nested templates
				while (true) {
					TemplateInstance ti2 = ti.tempdecl.parent
							.isTemplateInstance();
					if (ti2 == null)
						break;
					ti = ti2;
				}

				// If it's a member template
				ClassDeclaration cd2 = ti.tempdecl.isClassMember();
				if (cd2 != null) {
					error(
							"cannot use template to add virtual function to class '%s'",
							cd2.toChars());
				}
			}
		}

		if (isMain()) {
			// Check parameters to see if they are either () or (char[][] args)
			switch (nparams) {
			case 0:
				break;

			case 1: {
				Argument arg0 = Argument.getNth(f.parameters, 0, context);
				if (arg0.type.ty != TY.Tarray || arg0.type.next.ty != TY.Tarray
						|| arg0.type.next.next.ty != TY.Tchar
						|| (arg0.inout != InOut.None && arg0.inout != InOut.In)) {
					// goto Lmainerr;
					gotoLmainerr = true;
				}
				break;
			}

			default:
				// goto Lmainerr;
				gotoLmainerr = true;				
			}

			if (!gotoLmainerr) {
				if (f.next.ty != TY.Tint32 && f.next.ty != TY.Tvoid) {
					context.acceptProblem(Problem.newSemanticTypeError("Must return int or void from main function", IProblem.IllegalReturnType, 0, type.start, type.length));
				}
			}
			if (f.varargs || gotoLmainerr) {
				// Lmainerr: 
				context.acceptProblem(Problem.newSemanticTypeError("Parameters must be main() or main(char[][] args)", IProblem.IllegalParameters, 0, ident.start, ident.length));
			}
		}

		if (ident.ident == Id.assign && (sd != null || cd != null)) { // Disallow
																		// identity
																		// assignment
																		// operator.

			// opAssign(...)
			if (nparams == 0) {
				if (f.varargs) {
					// goto Lassignerr;
					error("identity assignment operator overload is illegal");
					return;
				}
			} else {
				Argument arg0 = Argument.getNth(f.parameters, 0, context);
				Type t0 = arg0.type.toBasetype(context);
				Type tb = sd != null ? sd.type : cd.type;
				if (arg0.type.implicitConvTo(tb, context) != MATCH.MATCHnomatch
						|| (sd != null && t0.ty == TY.Tpointer && t0.next
								.implicitConvTo(tb, context) != MATCH.MATCHnomatch)) {
					if (nparams == 1) {
						// goto Lassignerr;}
						error("identity assignment operator overload is illegal");
						return;
					}
					Argument arg1 = Argument.getNth(f.parameters, 1, context);
					if (arg1.defaultArg != null) {
						// goto Lassignerr;
						error("identity assignment operator overload is illegal");
						return;
					}
				}
			}
		}

		/*
		 * Save scope for possible later use (if we need the function internals)
		 */
		scope = new Scope(sc);
		scope.setNoFree();
		return;
	}
	
	@Override
	public void semantic2(Scope sc, SemanticContext context) {
		
	}
	
	@Override
	public void semantic3(Scope sc, SemanticContext context) {
		TypeFunction f;
	    AggregateDeclaration ad;
	    VarDeclaration argptr = null;
	    VarDeclaration _arguments = null;

	    if (parent == null)
	    {
	    	Assert.isTrue(false);
	    }

	    if (semanticRun != 0)
		return;
	    semanticRun = 1;

	    if (type == null || type.ty != TY.Tfunction)
		return;
	    f = (TypeFunction) (type);
	    int nparams = Argument.dim(f.parameters, context);

	    // Check the 'throws' clause
	    /* throws not used right now
	    if (fthrows)
	    {	int i;

		for (i = 0; i < fthrows.dim; i++)
		{
		    Type *t = (Type *)fthrows.data[i];

		    t = t.semantic(loc, sc);
		    if (!t.isClassHandle())
			error("can only throw classes, not %s", t.toChars());
		}
	    }
	    */

	    if (fbody != null || frequire != null)
	    {
		// Establish function scope
		ScopeDsymbol ss;
		Scope sc2;

		localsymtab = new DsymbolTable();

		ss = new ScopeDsymbol();
		ss.parent = sc.scopesym;
		sc2 = sc.push(ss);
		sc2.func = this;
		sc2.parent = this;
		sc2.callSuper = 0;
		sc2.sbreak = null;
		sc2.scontinue = null;
		sc2.sw = null;
		sc2.fes = fes;
		sc2.linkage = LINK.LINKd;
		sc2.stc &= ~(STC.STCauto | STC.STCscope | STC.STCstatic | STC.STCabstract | STC.STCdeprecated);
		sc2.protection = PROT.PROTpublic;
		sc2.explicitProtection = 0;
		sc2.structalign = 8;
		sc2.incontract = 0;
		sc2.tf = null;

		// Declare 'this'
		ad = isThis();
		if (ad)
		{   VarDeclaration *v;

		    if (isFuncLiteralDeclaration() && isNested())
		    {
			error("literals cannot be class members");
			return;
		    }
		    else
		    {
			assert(!isNested());	// can't be both member and nested
			assert(ad.handle);
			v = new ThisDeclaration(ad.handle);
			v.storage_class |= STCparameter | STCin;
			v.semantic(sc2);
			if (!sc2.insert(v))
			    assert(0);
			v.parent = this;
			vthis = v;
		    }
		}
		else if (isNested())
		{
		    VarDeclaration *v;

		    v = new ThisDeclaration(Type::tvoid.pointerTo());
		    v.storage_class |= STCparameter | STCin;
		    v.semantic(sc2);
		    if (!sc2.insert(v))
			assert(0);
		    v.parent = this;
		    vthis = v;
		}

		// Declare hidden variable _arguments[] and _argptr
		if (f.varargs == 1)
		{   Type *t;

		    if (f.linkage == LINKd)
		    {	// Declare _arguments[]
	#if BREAKABI
			v_arguments = new VarDeclaration(0, Type::typeinfotypelist.type, Id::_arguments_typeinfo, NULL);
			v_arguments.storage_class = STCparameter | STCin;
			v_arguments.semantic(sc2);
			sc2.insert(v_arguments);
			v_arguments.parent = this;

			t = Type::typeinfo.type.arrayOf();
			_arguments = new VarDeclaration(0, t, Id::_arguments, NULL);
			_arguments.semantic(sc2);
			sc2.insert(_arguments);
			_arguments.parent = this;
	#else
			t = Type::typeinfo.type.arrayOf();
			v_arguments = new VarDeclaration(0, t, Id::_arguments, NULL);
			v_arguments.storage_class = STCparameter | STCin;
			v_arguments.semantic(sc2);
			sc2.insert(v_arguments);
			v_arguments.parent = this;
	#endif
		    }
		    if (f.linkage == LINKd || (parameters && parameters.dim))
		    {	// Declare _argptr
	#if IN_GCC
			t = d_gcc_builtin_va_list_d_type;
	#else
			t = Type::tvoid.pointerTo();
	#endif
			argptr = new VarDeclaration(0, t, Id::_argptr, NULL);
			argptr.semantic(sc2);
			sc2.insert(argptr);
			argptr.parent = this;
		    }
		}

		// Propagate storage class from tuple arguments to their sub-arguments.
		if (f.parameters)
		{
		    for (size_t i = 0; i < f.parameters.dim; i++)
		    {	Argument *arg = (Argument *)f.parameters.data[i];

			if (arg.type.ty == Ttuple)
			{   TypeTuple *t = (TypeTuple *)arg.type;
			    size_t dim = Argument::dim(t.arguments);
			    for (size_t j = 0; j < dim; j++)
			    {	Argument *narg = Argument::getNth(t.arguments, j);
				narg.inout = arg.inout;
			    }
			}
		    }
		}

		// Declare all the function parameters as variables
		if (nparams)
		{   // parameters[] has all the tuples removed, as the back end
		    // doesn't know about tuples
		    parameters = new Dsymbols();
		    parameters.reserve(nparams);
		    for (size_t i = 0; i < nparams; i++)
		    {
			Argument *arg = Argument::getNth(f.parameters, i);
			Identifier *id = arg.ident;
			if (!id)
			{
			    //error("no identifier for parameter %d of %s", i + 1, toChars());
			    OutBuffer buf;
			    buf.printf("_param_%zu", i);
			    char *name = (char *)buf.extractData();
			    id = new Identifier(name, TOKidentifier);
			    arg.ident = id;
			}
			VarDeclaration *v = new VarDeclaration(0, arg.type, id, NULL);
			//printf("declaring parameter %s of type %s\n", v.toChars(), v.type.toChars());
			v.storage_class |= STCparameter;
			if (f.varargs == 2 && i + 1 == nparams)
			    v.storage_class |= STCvariadic;
			switch (arg.inout)
			{   case In:    v.storage_class |= STCin;		break;
			    case Out:   v.storage_class |= STCout;		break;
			    case InOut: v.storage_class |= STCin | STCout;	break;
			    case Lazy:  v.storage_class |= STCin | STClazy; break;
			    default: assert(0);
			}
			v.semantic(sc2);
			if (!sc2.insert(v))
			    error("parameter %s.%s is already defined", toChars(), v.toChars());
			else
			    parameters.push(v);
			localsymtab.insert(v);
			v.parent = this;
		    }
		}

		// Declare the tuple symbols and put them in the symbol table,
		// but not in parameters[].
		if (f.parameters)
		{
		    for (size_t i = 0; i < f.parameters.dim; i++)
		    {	Argument *arg = (Argument *)f.parameters.data[i];

			if (!arg.ident)
			    continue;			// never used, so ignore
			if (arg.type.ty == Ttuple)
			{   TypeTuple *t = (TypeTuple *)arg.type;
			    size_t dim = Argument::dim(t.arguments);
			    Objects *exps = new Objects();
			    exps.setDim(dim);
			    for (size_t j = 0; j < dim; j++)
			    {	Argument *narg = Argument::getNth(t.arguments, j);
				assert(narg.ident);
				VarDeclaration *v = sc2.search(0, narg.ident, NULL).isVarDeclaration();
				assert(v);
				Expression *e = new VarExp(0, v);
				exps.data[j] = (void *)e;
			    }
			    assert(arg.ident);
			    TupleDeclaration *v = new TupleDeclaration(0, arg.ident, exps);
			    //printf("declaring tuple %s\n", v.toChars());
			    v.isexp = 1;
			    if (!sc2.insert(v))
				error("parameter %s.%s is already defined", toChars(), v.toChars());
			    localsymtab.insert(v);
			    v.parent = this;
			}
		    }
		}

		sc2.incontract++;

		if (frequire)
		{
		    // BUG: need to error if accessing out parameters
		    // BUG: need to treat parameters as const
		    // BUG: need to disallow returns and throws
		    // BUG: verify that all in and inout parameters are read
		    frequire = frequire.semantic(sc2);
		    labtab = NULL;		// so body can't refer to labels
		}

		if (fensure || addPostInvariant())
		{
		    ScopeDsymbol *sym;

		    sym = new ScopeDsymbol();
		    sym.parent = sc2.scopesym;
		    sc2 = sc2.push(sym);

		    assert(type.next);
		    if (type.next.ty == Tvoid)
		    {
			if (outId)
			    error("void functions have no result");
		    }
		    else
		    {
			if (!outId)
			    outId = Id::result;		// provide a default
		    }

		    if (outId)
		    {	// Declare result variable
			VarDeclaration *v;
			Loc loc = this.loc;

			if (fensure)
			    loc = fensure.loc;

			v = new VarDeclaration(loc, type.next, outId, NULL);
			v.noauto = 1;
			sc2.incontract--;
			v.semantic(sc2);
			sc2.incontract++;
			if (!sc2.insert(v))
			    error("out result %s is already defined", v.toChars());
			v.parent = this;
			vresult = v;

			// vresult gets initialized with the function return value
			// in ReturnStatement::semantic()
		    }

		    // BUG: need to treat parameters as const
		    // BUG: need to disallow returns and throws
		    if (fensure)
		    {	fensure = fensure.semantic(sc2);
			labtab = NULL;		// so body can't refer to labels
		    }

		    if (!global.params.useOut)
		    {	fensure = NULL;		// discard
			vresult = NULL;
		    }

		    // Postcondition invariant
		    if (addPostInvariant())
		    {
			Expression *e = NULL;
			if (isCtorDeclaration())
			{
			    // Call invariant directly only if it exists
			    InvariantDeclaration *inv = ad.inv;
			    ClassDeclaration *cd = ad.isClassDeclaration();

			    while (!inv && cd)
			    {
				cd = cd.baseClass;
				if (!cd)
				    break;
				inv = cd.inv;
			    }
			    if (inv)
			    {
				e = new DsymbolExp(0, inv);
				e = new CallExp(0, e);
				e = e.semantic(sc2);
			    }
			}
			else
			{   // Call invariant virtually
			    ThisExp *v = new ThisExp(0);
			    v.type = vthis.type;
			    e = new AssertExp(0, v);
			}
			if (e)
			{
			    ExpStatement *s = new ExpStatement(0, e);
			    if (fensure)
				fensure = new CompoundStatement(0, s, fensure);
			    else
				fensure = s;
			}
		    }

		    if (fensure)
		    {	returnLabel = new LabelDsymbol(Id::returnLabel);
			LabelStatement *ls = new LabelStatement(0, Id::returnLabel, fensure);
			ls.isReturnLabel = 1;
			returnLabel.statement = ls;
		    }
		    sc2 = sc2.pop();
		}

		sc2.incontract--;

		if (fbody)
		{   ClassDeclaration *cd = isClassMember();

		    if (isCtorDeclaration() && cd)
		    {
			for (int i = 0; i < cd.fields.dim; i++)
			{   VarDeclaration *v = (VarDeclaration *)cd.fields.data[i];

			    v.ctorinit = 0;
			}
		    }

		    if (inferRetType || f.retStyle() != RETstack)
			nrvo_can = 0;

		    fbody = fbody.semantic(sc2);

		    if (inferRetType)
		    {	// If no return type inferred yet, then infer a void
			if (!type.next)
			{
			    type.next = Type::tvoid;
			    type = type.semantic(loc, sc);
			}
			f = (TypeFunction *)type;
		    }

		    int offend = fbody ? fbody.fallOffEnd() : TRUE;

		    if (isStaticCtorDeclaration())
		    {	/* It's a static constructor. Ensure that all
			 * ctor consts were initialized.
			 */

			ScopeDsymbol *ad = toParent().isScopeDsymbol();
			assert(ad);
			for (int i = 0; i < ad.members.dim; i++)
			{   Dsymbol *s = (Dsymbol *)ad.members.data[i];

			    s.checkCtorConstInit();
			}
		    }

		    if (isCtorDeclaration() && cd)
		    {
			//printf("callSuper = x%x\n", sc2.callSuper);

			// Verify that all the ctorinit fields got initialized
			if (!(sc2.callSuper & CSXthis_ctor))
			{
			    for (int i = 0; i < cd.fields.dim; i++)
			    {   VarDeclaration *v = (VarDeclaration *)cd.fields.data[i];

				if (v.ctorinit == 0 && v.isCtorinit())
				    error("missing initializer for const field %s", v.toChars());
			    }
			}

			if (!(sc2.callSuper & CSXany_ctor) &&
			    cd.baseClass && cd.baseClass.ctor)
			{
			    sc2.callSuper = 0;

			    // Insert implicit super() at start of fbody
			    Expression *e1 = new SuperExp(0);
			    Expression *e = new CallExp(0, e1);

			    unsigned errors = global.errors;
			    global.gag++;
			    e = e.semantic(sc2);
			    global.gag--;
			    if (errors != global.errors)
				error("no match for implicit super() call in constructor");

			    Statement *s = new ExpStatement(0, e);
			    fbody = new CompoundStatement(0, s, fbody);
			}
		    }
		    else if (fes)
		    {	// For foreach(){} body, append a return 0;
			Expression *e = new IntegerExp(0);
			Statement *s = new ReturnStatement(0, e);
			fbody = new CompoundStatement(0, fbody, s);
			assert(!returnLabel);
		    }
		    else if (!hasReturnExp && type.next.ty != Tvoid)
			error("expected to return a value of type %s", type.next.toChars());
		    else if (!inlineAsm)
		    {
			if (type.next.ty == Tvoid)
			{
			    if (offend && isMain())
			    {	// Add a return 0; statement
				Statement *s = new ReturnStatement(0, new IntegerExp(0));
				fbody = new CompoundStatement(0, fbody, s);
			    }
			}
			else
			{
			    if (offend)
			    {   Expression *e;

				if (global.params.warnings)
				{   fprintf(stdmsg, "warning - ");
				    error("no return at end of function");
				}

				if (global.params.useAssert &&
				    !global.params.useInline)
				{   /* Add an assert(0, msg); where the missing return
				     * should be.
				     */
				    e = new AssertExp(
					  endloc,
					  new IntegerExp(0),
					  new StringExp(0, "missing return expression")
					);
				}
				else
				    e = new HaltExp(endloc);
				e = new CommaExp(0, e, type.next.defaultInit());
				e = e.semantic(sc2);
				Statement *s = new ExpStatement(0, e);
				fbody = new CompoundStatement(0, fbody, s);
			    }
			}
		    }
		}

		{
		    Statements *a = new Statements();

		    // Merge in initialization of 'out' parameters
		    if (parameters)
		    {	for (size_t i = 0; i < parameters.dim; i++)
			{   VarDeclaration *v;

			    v = (VarDeclaration *)parameters.data[i];
			    if ((v.storage_class & (STCout | STCin)) == STCout)
			    {
				assert(v.init);
				ExpInitializer *ie = v.init.isExpInitializer();
				assert(ie);
				a.push(new ExpStatement(0, ie.exp));
			    }
			}
		    }

		    if (argptr)
		    {	// Initialize _argptr to point past non-variadic arg
	#if IN_GCC
			// Handled in FuncDeclaration::toObjFile
			v_argptr = argptr;
			v_argptr.init = new VoidInitializer(loc);
	#else
			Expression *e1;
			Expression *e;
			Type *t = argptr.type;
			VarDeclaration *p;
			unsigned offset;

			e1 = new VarExp(0, argptr);
			if (parameters && parameters.dim)
			    p = (VarDeclaration *)parameters.data[parameters.dim - 1];
			else
			    p = v_arguments;		// last parameter is _arguments[]
			offset = p.type.size();
			offset = (offset + 3) & ~3;	// assume stack aligns on 4
			e = new SymOffExp(0, p, offset);
			e = new AssignExp(0, e1, e);
			e.type = t;
			a.push(new ExpStatement(0, e));
	#endif
		    }

		    if (_arguments)
		    {
			/* Advance to elements[] member of TypeInfo_Tuple with:
			 *  _arguments = v_arguments.elements;
			 */
			Expression *e = new VarExp(0, v_arguments);
			e = new DotIdExp(0, e, Id::elements);
			Expression *e1 = new VarExp(0, _arguments);
			e = new AssignExp(0, e1, e);
			e = e.semantic(sc);
			a.push(new ExpStatement(0, e));
		    }

		    // Merge contracts together with body into one compound statement

	#ifdef _DH
		    if (frequire && global.params.useIn)
		    {	frequire.incontract = 1;
			a.push(frequire);
		    }
	#else
		    if (frequire && global.params.useIn)
			a.push(frequire);
	#endif

		    // Precondition invariant
		    if (addPreInvariant())
		    {
			Expression *e = NULL;
			if (isDtorDeclaration())
			{
			    // Call invariant directly only if it exists
			    InvariantDeclaration *inv = ad.inv;
			    ClassDeclaration *cd = ad.isClassDeclaration();

			    while (!inv && cd)
			    {
				cd = cd.baseClass;
				if (!cd)
				    break;
				inv = cd.inv;
			    }
			    if (inv)
			    {
				e = new DsymbolExp(0, inv);
				e = new CallExp(0, e);
				e = e.semantic(sc2);
			    }
			}
			else
			{   // Call invariant virtually
			    ThisExp *v = new ThisExp(0);
			    v.type = vthis.type;
			    Expression *se = new StringExp(0, "null this");
			    se = se.semantic(sc);
			    se.type = Type::tchar.arrayOf();
			    e = new AssertExp(loc, v, se);
			}
			if (e)
			{
			    ExpStatement *s = new ExpStatement(0, e);
			    a.push(s);
			}
		    }

		    if (fbody)
			a.push(fbody);

		    if (fensure)
		    {
			a.push(returnLabel.statement);

			if (type.next.ty != Tvoid)
			{
			    // Create: return vresult;
			    assert(vresult);
			    Expression *e = new VarExp(0, vresult);
			    if (tintro)
			    {	e = e.implicitCastTo(sc, tintro.next);
				e = e.semantic(sc);
			    }
			    ReturnStatement *s = new ReturnStatement(0, e);
			    a.push(s);
			}
		    }

		    fbody = new CompoundStatement(0, a);
		}

		sc2.callSuper = 0;
		sc2.pop();
	    }
	    semanticRun = 2;
	}
	
	public FuncDeclaration overloadExactMatch(Type t, SemanticContext context) {
		FuncDeclaration f;
		Declaration d;
		Declaration next;

		for (d = this; d != null; d = next) {
			FuncAliasDeclaration fa = d.isFuncAliasDeclaration();

			if (fa != null) {
				FuncDeclaration f2 = fa.funcalias
						.overloadExactMatch(t, context);
				if (f2 != null) {
					return f2;
				}
				next = fa.overnext;
			} else {
				AliasDeclaration a = d.isAliasDeclaration();

				if (a != null) {
					Dsymbol s = a.toAlias(context);
					next = s.isDeclaration();
					if (next == a) {
						break;
					}
				} else {
					f = d.isFuncDeclaration();
					if (f == null) {
						break; // BUG: should print error message?
					}
					if (t.equals(d.type)) {
						return f;
					}
					next = f.overnext;
				}
			}
		}
		return null;
	}
	
	public boolean isMain() {
		return ident.ident == Id.main &&
			linkage != LINK.LINKc && isMember() == null && !isNested();
	}
	
	@Override
	public boolean overloadInsert(Dsymbol s, SemanticContext context) {
		FuncDeclaration f;
		AliasDeclaration a;

		a = s.isAliasDeclaration();
		if (a != null) {
			if (overnext != null) {
				return overnext.overloadInsert(a, context);
			}
			if (a.aliassym == null && a.type.ty != TY.Tident
					&& a.type.ty != TY.Tinstance) {
				return false;
			}
			overnext = a;
			return true;
		}
		f = s.isFuncDeclaration();
		if (f == null) {
			return false;
		}

		if (type != null && f.type != null
				&& // can be NULL for overloaded constructors
				f.type.covariant(type, context) != 0
				&& isFuncAliasDeclaration() == null) {
			return false;
		}

		if (overnext != null)
			return overnext.overloadInsert(f, context);
		overnext = f;
		return false;
	}
	
	@Override
	public int getNodeType() {
		return FUNC_DECLARATION;
	}

}
