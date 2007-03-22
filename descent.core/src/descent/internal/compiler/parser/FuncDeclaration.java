package descent.internal.compiler.parser;

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
