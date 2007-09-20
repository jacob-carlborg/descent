package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.STC.STCabstract;
import static descent.internal.compiler.parser.STC.STCauto;
import static descent.internal.compiler.parser.STC.STCdeprecated;
import static descent.internal.compiler.parser.STC.STCin;
import static descent.internal.compiler.parser.STC.STClazy;
import static descent.internal.compiler.parser.STC.STCout;
import static descent.internal.compiler.parser.STC.STCparameter;
import static descent.internal.compiler.parser.STC.STCref;
import static descent.internal.compiler.parser.STC.STCscope;
import static descent.internal.compiler.parser.STC.STCstatic;
import static descent.internal.compiler.parser.STC.STCvariadic;
import static descent.internal.compiler.parser.TY.Tarray;
import static descent.internal.compiler.parser.TY.Tchar;
import static descent.internal.compiler.parser.TY.Tfunction;
import static descent.internal.compiler.parser.TY.Tident;
import static descent.internal.compiler.parser.TY.Tinstance;
import static descent.internal.compiler.parser.TY.Tint32;
import static descent.internal.compiler.parser.TY.Tpointer;
import static descent.internal.compiler.parser.TY.*;
import static descent.internal.compiler.parser.TY.Tvoid;
import static descent.internal.compiler.parser.ILS.*;

import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.CharOperation;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class FuncDeclaration extends Declaration {

	private final static char[] missing_return_expression = { 'm', 'i', 's',
			's', 'i', 'n', 'g', ' ', 'r', 'e', 't', 'u', 'r', 'n', ' ', 'e',
			'x', 'p', 'r', 'e', 's', 's', 'i', 'o', 'n' };
	private final static char[] null_this = { 'n', 'u', 'l', 'l', ' ', 't',
			'h', 'i', 's' };

	public List fthrows; // Array of Type's of exceptions (not used)
	public Statement fensure;
	public Statement frequire;
	public Statement fbody;
	public Statement sourceFensure;
	public Statement sourceFrequire;
	public Statement sourceFbody;
	public IdentifierExp outId;
	public int vtblIndex; // for member functions, index into vtbl[]
	public boolean introducing; // !=0 if 'introducing' function
	public Type tintro; // if !=NULL, then this is the type
	// of the 'introducing' function
	// this one is overriding
	public Declaration overnext; // next in overload list
	public Scope scope; // !=NULL means context to use
	public int semanticRun; // !=0 if semantic3() had been run
	public DsymbolTable localsymtab; // used to prevent symbols in different
	// scopes from having the same name
	public ForeachStatement fes; // if foreach body, this is the foreach
	public VarDeclaration vthis; // 'this' parameter (member and nested)
	public VarDeclaration v_arguments; // '_arguments' parameter
	public List<Dsymbol> parameters; // Array of VarDeclaration's for parameters
	public DsymbolTable labtab; // statement label symbol table
	public VarDeclaration vresult; // variable corresponding to outId
	public LabelDsymbol returnLabel; // where the return goes
	public boolean inferRetType;
	public boolean naked; // !=0 if naked
	public boolean inlineAsm; // !=0 if has inline assembler
	public ILS inlineStatus;
	public boolean inlineNest; // !=0 if nested inline
	public boolean nestedFrameRef;
	public int hasReturnExp; // 1 if there's a return exp; statement
	// 2 if there's a throw statement
	// 4 if there's an assert(0)
	// 8 if there's inline asm

	// Support for NRVO (named return value optimization)
	public int nrvo_can; // !=0 means we can do it
	public VarDeclaration nrvo_var; // variable to replace with shidden

	public FuncDeclaration(Loc loc, IdentifierExp ident, int storage_class,
			Type type) {
		super(loc, ident);
		this.storage_class = storage_class;
		this.type = type;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, modifiers);
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, ident);
			// Template args?
			TreeVisitor.acceptChildren(visitor, parameters);
			TreeVisitor.acceptChildren(visitor, sourceFrequire);
			TreeVisitor.acceptChildren(visitor, sourceFbody);
			TreeVisitor.acceptChildren(visitor, outId);
			TreeVisitor.acceptChildren(visitor, sourceFensure);
		}
		visitor.endVisit(this);
	}

	@Override
	public FuncDeclaration isFuncDeclaration() {
		return this;
	}

	public void setFrequire(Statement frequire) {
		this.frequire = frequire;
		sourceFrequire = frequire;
	}

	public void setFensure(Statement fensure) {
		this.fensure = fensure;
		sourceFensure = fensure;
	}

	public void setFbody(Statement fbody) {
		this.fbody = fbody;
		sourceFbody = fbody;
	}

	public boolean isNested() {
		return ((storage_class & STCstatic) == 0)
				&& (toParent2().isFuncDeclaration() != null);
	}

	@Override
	public AggregateDeclaration isThis() {
		AggregateDeclaration ad;

		ad = null;
		if ((storage_class & STCstatic) == 0) {
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
			if (s.parent == null || s.parent.isTemplateInstance() == null) {
				break;
			}
		}
		return ad;
	}

	public boolean isVirtual(SemanticContext context) {
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
			type = type.semantic(loc, sc, context);
		}

		if (type.ty != Tfunction) {
			error("%s must be a function", toChars(context));
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
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.FunctionsCannotBeConstOrAuto, 0, ident.start,
					ident.length));
		}

		if (isAbstract() && !isVirtual(context)) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.NonVirtualFunctionsCannotBeAbstract, 0,
					ident.start, ident.length));
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
			storage_class |= STCabstract;

			if (isCtorDeclaration() != null || isDtorDeclaration() != null
					|| isInvariantDeclaration() != null
					|| isUnitTestDeclaration() != null
					|| isNewDeclaration() != null || isDelete()) {
				error("special function not allowed in interface %s", id
						.toChars(context));
			}
			if (fbody != null) {
				error("function body is not abstract in interface %s", id
						.toChars(context));
			}
		}

		cd = parent.isClassDeclaration();
		if (cd != null) {
			int vi;

			if (isCtorDeclaration() != null) {
				// ctor = (CtorDeclaration *)this;
				// if (!cd.ctor)
				// cd.ctor = ctor;
				return;
			}

			if ((storage_class & STCabstract) != 0) {
				cd.isabstract = true;
			}

			// if static function, do not put in vtbl[]
			if (!isVirtual(context)) {
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
									type.toChars(context), fdv
											.toPrettyChars(context), fdv.type
											.toChars(context));
						}
						if (cov == 1) {
							if (fdv.isFinal()) {
								error("cannot override final function %s", fdv
										.toPrettyChars(context));
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
									if (context.BREAKABI) {
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
									if (fdv.type.next.isBaseOf(type.next,
											offset, context)) {
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
						Dsymbol s = cd.baseClass.search(loc, ident, 0, context);
						if (s != null) {
							FuncDeclaration f2 = s.isFuncDeclaration();
							f2 = f2.overloadExactMatch(type, context);
							if (f2 != null && f2.isFinal()) {
								context
										.acceptProblem(Problem
												.newSemanticTypeError(
														IProblem.CannotOverrideFinalFunctions,
														0,
														ident.start,
														ident.length,
														new String[] {
																new String(
																		ident.ident),
																new String(
																		cd.ident.ident) }));
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
									type.toChars(context), fdv
											.toPrettyChars(context), fdv.type
											.toChars(context));
						}
						if (cov == 1) {
							Type ti = null;

							if (fdv.tintro != null) {
								ti = fdv.tintro;
							} else if (!type.equals(fdv.type)) {
								/*
								 * Only need to have a tintro if the vptr
								 * offsets differ
								 */
								int[] offset = { 0 };
								if (fdv.type.next.isBaseOf(type.next, offset,
										context)) {
									ti = fdv.type;

								}
							}
							if (ti != null) {
								if (tintro != null && !tintro.equals(ti)) {
									error(
											"incompatible covariant types %s and %s",
											tintro.toChars(context), ti
													.toChars(context));
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
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.FunctionDoesNotOverrideAny, 0,
							ident.start, ident.length, new String[] {
									new String(ident.ident),
									new String(cd.ident.ident) }));
				}
			}

			// L2:	;
		} else if (isOverride() && parent.isTemplateInstance() == null) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.OverrideOnlyForClassMemberFunctions, 0,
					ident.start, ident.length));
		}

		/*
		 * Do not allow template instances to add virtual functions to a class.
		 */
		if (isVirtual(context)) {
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
							cd2.toChars(context));
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
				if (arg0.type.ty != Tarray
						|| arg0.type.next.ty != Tarray
						|| arg0.type.next.next.ty != Tchar
						|| ((arg0.storageClass & (STCout | STCref | STClazy)) != 0)) {
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
				if (f.next.ty != Tint32 && f.next.ty != Tvoid) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.MustReturnIntOrVoidFromMainFunction, 0,
							type.start, type.length));
				}
			}
			if (f.varargs != 0 || gotoLmainerr) {
				// Lmainerr: 
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.IllegalMainParameters, 0, ident.start,
						ident.length));
			}
		}

		if (CharOperation.equals(ident.ident, Id.assign)
				&& (sd != null || cd != null)) { // Disallow
			// identity
			// assignment
			// operator.

			// opAssign(...)
			if (nparams == 0) {
				if (f.varargs != 0) {
					// goto Lassignerr;
					error("identity assignment operator overload is illegal");
					return;
				}
			} else {
				Argument arg0 = Argument.getNth(f.parameters, 0, context);
				Type t0 = arg0.type.toBasetype(context);
				Type tb = sd != null ? sd.type : cd.type;
				if (arg0.type.implicitConvTo(tb, context) != MATCH.MATCHnomatch
						|| (sd != null && t0.ty == Tpointer && t0.next
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

		if (parent == null) {
			Assert.isTrue(false);
		}

		if (semanticRun != 0) {
			return;
		}
		semanticRun = 1;

		if (type == null || type.ty != Tfunction) {
			return;
		}
		f = (TypeFunction) (type);
		int nparams = Argument.dim(f.parameters, context);

		// Check the 'throws' clause
		/*
		 * throws not used right now if (fthrows) { int i;
		 * 
		 * for (i = 0; i < fthrows.dim; i++) { Type *t = (Type
		 * *)fthrows.data[i];
		 * 
		 * t = t.semantic(loc, sc); if (!t.isClassHandle()) error("can only
		 * throw classes, not %s", t.toChars()); } }
		 */

		if (fbody != null || frequire != null) {
			// Establish function scope
			ScopeDsymbol ss;
			Scope sc2;

			localsymtab = new DsymbolTable();

			ss = new ScopeDsymbol(loc);
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
			sc2.stc &= ~(STCauto | STCscope | STCstatic | STCabstract | STCdeprecated);
			sc2.protection = PROT.PROTpublic;
			sc2.explicitProtection = 0;
			sc2.structalign = 8;
			sc2.incontract = 0;
			sc2.tf = null;

			// Declare 'this'
			ad = isThis();
			if (ad != null) {
				VarDeclaration v;

				if (isFuncLiteralDeclaration() != null && isNested()) {
					error("literals cannot be class members");
					return;
				} else {
					Assert.isTrue(!isNested()); // can't be both member and
					// nested
					Assert.isNotNull(ad.handle);
					v = new ThisDeclaration(loc, ad.handle);
					v.synthetic = true;
					v.storage_class |= STCparameter | STCin;
					v.semantic(sc2, context);
					if (sc2.insert(v) == null) {
						Assert.isTrue(false);
					}
					v.parent = this;
					vthis = v;
				}
			} else if (isNested()) {
				VarDeclaration v;

				v = new ThisDeclaration(loc, Type.tvoid.pointerTo(context));
				v.synthetic = true;
				v.storage_class |= STCparameter | STCin;
				v.semantic(sc2, context);
				if (sc2.insert(v) == null) {
					Assert.isTrue(false);
				}
				v.parent = this;
				vthis = v;
			}

			// Declare hidden variable _arguments[] and _argptr
			if (f.varargs != 0) {
				Type t;

				if (f.linkage == LINK.LINKd) { // Declare _arguments[]
					if (context.BREAKABI) {
						v_arguments = new VarDeclaration(loc,
								context.typeinfotypelist.type,
								Id._arguments_typeinfo, null);
						v_arguments.synthetic = true;
						v_arguments.storage_class = STCparameter | STCin;
						v_arguments.semantic(sc2, context);
						sc2.insert(v_arguments);
						v_arguments.parent = this;

						t = context.typeinfo.type.arrayOf(context);
						_arguments = new VarDeclaration(loc, t, Id._arguments,
								null);
						_arguments.synthetic = true;
						_arguments.semantic(sc2, context);
						sc2.insert(_arguments);
						_arguments.parent = this;
					} else {
						t = context.typeinfo.type.arrayOf(context);
						v_arguments = new VarDeclaration(loc, t, Id._arguments,
								null);
						v_arguments.storage_class = STCparameter | STCin;
						v_arguments.semantic(sc2, context);
						sc2.insert(v_arguments);
						v_arguments.parent = this;
					}
				}
				if (f.linkage == LINK.LINKd
						|| (parameters != null && parameters.size() > 0)) { // Declare
					// _argptr
					t = Type.tvoid.pointerTo(context);
					argptr = new VarDeclaration(loc, t, Id._argptr, null);
					argptr.synthetic = true;
					argptr.semantic(sc2, context);
					sc2.insert(argptr);
					argptr.parent = this;
				}
			}

			// Propagate storage class from tuple arguments to their
			// sub-arguments.
			if (f.parameters != null) {
				for (int i = 0; i < f.parameters.size(); i++) {
					Argument arg = f.parameters.get(i);

					if (arg.type.ty == Ttuple) {
						TypeTuple t = (TypeTuple) arg.type;
						int dim = Argument.dim(t.arguments, context);
						for (int j = 0; j < dim; j++) {
							Argument narg = Argument.getNth(t.arguments, j,
									context);
							narg.storageClass = arg.storageClass;
						}
					}
				}
			}

			// Declare all the function parameters as variables
			if (nparams != 0) { // parameters[] has all the tuples removed, as
				// the back end
				// doesn't know about tuples
				parameters = new ArrayList<Dsymbol>(nparams);
				for (int i = 0; i < nparams; i++) {
					Argument arg = Argument.getNth(f.parameters, i, context);
					IdentifierExp id = arg.ident;
					if (id == null) {
						id = new IdentifierExp(loc, ("_param_" + i + "u")
								.toCharArray());
						arg.ident = id;
					}
					VarDeclaration v = new VarDeclaration(loc, arg.type, id,
							null);
					v.synthetic = true;
					v.storage_class |= STCparameter;
					if (f.varargs == 2 && i + 1 == nparams)
						v.storage_class |= STCvariadic;
					v.storage_class |= arg.storageClass
							& (STCin | STCout | STCref | STClazy);
					v.semantic(sc2, context);
					if (sc2.insert(v) == null) {
						error("parameter %s.%s is already defined",
								toChars(context), v.toChars(context));
					} else {
						parameters.add(v);
					}
					localsymtab.insert(v);
					v.parent = this;
				}
			}

			// Declare the tuple symbols and put them in the symbol table,
			// but not in parameters[].
			if (f.parameters != null) {
				for (int i = 0; i < f.parameters.size(); i++) {
					Argument arg = f.parameters.get(i);

					if (arg.ident == null) {
						continue; // never used, so ignore
					}
					if (arg.type.ty == Ttuple) {
						TypeTuple t = (TypeTuple) arg.type;
						int dim = Argument.dim(t.arguments, context);
						List exps = new ArrayList(dim);
						for (int j = 0; j < dim; j++) {
							Argument narg = Argument.getNth(t.arguments, j,
									context);
							Assert.isNotNull(narg.ident);
							VarDeclaration v = sc2.search(loc, narg.ident,
									null, context).isVarDeclaration();
							v.synthetic = true;
							Assert.isNotNull(v);
							Expression e = new VarExp(loc, v);
							exps.add(e);
						}
						Assert.isNotNull(arg.ident);
						TupleDeclaration v = new TupleDeclaration(loc,
								arg.ident, exps);
						v.isexp = true;
						if (sc2.insert(v) == null) {
							error("parameter %s.%s is already defined",
									toChars(context), v.toChars(context));
						}
						localsymtab.insert(v);
						v.parent = this;
					}
				}
			}

			sc2.incontract++;

			if (frequire != null) {
				// BUG: need to error if accessing out parameters
				// BUG: need to treat parameters as const
				// BUG: need to disallow returns and throws
				// BUG: verify that all in and inout parameters are read
				frequire = frequire.semantic(sc2, context);
				labtab = null; // so body can't refer to labels
			}

			if (fensure != null || addPostInvariant(context)) {
				ScopeDsymbol sym;

				sym = new ScopeDsymbol(loc);
				sym.parent = sc2.scopesym;
				sc2 = sc2.push(sym);

				Assert.isNotNull(type.next);
				if (type.next.ty == Tvoid) {
					if (outId != null) {
						context.acceptProblem(Problem.newSemanticTypeError(
								IProblem.VoidFunctionsHaveNoResult, 0,
								outId.start, outId.length));
					}
				} else {
					if (outId == null) {
						outId = new IdentifierExp(loc, Id.result); // provide a
						// default
					}
				}

				if (outId != null) { // Declare result variable
					VarDeclaration v;
					Loc loc = this.loc;

					if (fensure != null) {
						fensure.loc = loc;
					}

					v = new VarDeclaration(loc, type.next, outId, null);
					v.synthetic = true;
					v.noauto = true;
					sc2.incontract--;
					v.semantic(sc2, context);
					sc2.incontract++;
					if (sc2.insert(v) == null) {
						error("out result %s is already defined", v
								.toChars(context));
					}
					v.parent = this;
					vresult = v;

					// vresult gets initialized with the function return value
					// in ReturnStatement::semantic()
				}

				// BUG: need to treat parameters as const
				// BUG: need to disallow returns and throws
				if (fensure != null) {
					fensure = fensure.semantic(sc2, context);
					labtab = null; // so body can't refer to labels
				}

				if (!context.global.params.useOut) {
					if (fensure != null) {
						fensure.discarded = true; // discard
					}
					if (vresult != null) {
						vresult.discarded = true;
					}
				}

				// Postcondition invariant
				if (addPostInvariant(context)) {
					Expression e = null;
					if (isCtorDeclaration() != null) {
						// Call invariant directly only if it exists
						InvariantDeclaration inv = ad.inv;
						ClassDeclaration cd = ad.isClassDeclaration();

						while (inv == null && cd != null) {
							cd = cd.baseClass;
							if (cd == null) {
								break;
							}
							inv = cd.inv;
						}
						if (inv != null) {
							e = new DsymbolExp(loc, inv);
							e = new CallExp(loc, e);
							e = e.semantic(sc2, context);
						}
					} else { // Call invariant virtually
						ThisExp v = new ThisExp(loc);
						v.synthetic = true;
						v.type = vthis.type;
						e = new AssertExp(loc, v);
						e.synthetic = true;
					}
					if (e != null) {
						ExpStatement s = new ExpStatement(loc, e);
						s.synthetic = true;
						if (fensure != null) {
							fensure = new CompoundStatement(loc, s, fensure);
							fensure.synthetic = true;
						} else {
							fensure = s;
						}
					}
				}

				if (fensure != null) {
					returnLabel = new LabelDsymbol(loc, Id.returnLabel);
					returnLabel.synthetic = true;
					LabelStatement ls = new LabelStatement(loc,
							new IdentifierExp(loc, Id.returnLabel), fensure);
					ls.synthetic = true;
					ls.isReturnLabel = true;
					returnLabel.statement = ls;
				}
				sc2 = sc2.pop();
			}

			sc2.incontract--;

			if (fbody != null) {
				ClassDeclaration cd = isClassMember();

				if (isCtorDeclaration() != null && cd != null) {
					for (int i = 0; i < cd.fields.size(); i++) {
						VarDeclaration v = cd.fields.get(i);
						v.ctorinit = false;
					}
				}

				if (inferRetType || f.retStyle() != RET.RETstack) {
					nrvo_can = 0;
				}

				fbody = fbody.semantic(sc2, context);

				if (inferRetType) { // If no return type inferred yet, then
					// infer a void
					if (type.next == null) {
						type.next = Type.tvoid;
						type = type.semantic(loc, sc, context);
					}
					f = (TypeFunction) type;
				}

				boolean offend = fbody != null ? fbody.fallOffEnd(context)
						: true;

				if (isStaticCtorDeclaration() != null) { /*
				 * It's a static
				 * constructor.
				 * Ensure that all
				 * ctor consts were
				 * initialized.
				 */

					ScopeDsymbol ad2 = toParent().isScopeDsymbol();
					Assert.isTrue(ad2 != null);
					for (int i = 0; i < ad2.members.size(); i++) {
						Dsymbol s = ad2.members.get(i);

						s.checkCtorConstInit(context);
					}
				}

				if (isCtorDeclaration() != null && cd != null) {

					// Verify that all the ctorinit fields got initialized
					if ((sc2.callSuper & Scope.CSXthis_ctor) == 0) {
						for (int i = 0; i < cd.fields.size(); i++) {
							VarDeclaration v = cd.fields.get(i);

							if (!v.ctorinit && v.isCtorinit())
								error("missing initializer for const field %s",
										v.toChars(context));
						}
					}

					if ((sc2.callSuper & Scope.CSXany_ctor) == 0
							&& cd.baseClass != null
							&& cd.baseClass.ctor != null) {
						sc2.callSuper = 0;

						// Insert implicit super() at start of fbody
						Expression e1 = new SuperExp(loc);
						Expression e = new CallExp(loc, e1);

						/*
						 * TODO semantic unsigned errors =
						 * context.global.errors; context.global.gag++; e =
						 * e.semantic(sc2, context); context.global.gag--; if
						 * (errors != global.errors) error("no match for
						 * implicit super() call in constructor");
						 */

						Statement s = new ExpStatement(loc, e);
						s.synthetic = true;
						fbody = new CompoundStatement(loc, s, fbody);
						fbody.synthetic = true;
					}
				} else if (fes != null) { // For foreach(){} body, append a
					// return 0;
					Expression e = new IntegerExp(loc, 0);
					e.synthetic = true;
					Statement s = new ReturnStatement(loc, e);
					s.synthetic = true;
					fbody = new CompoundStatement(loc, fbody, s);
					fbody.synthetic = true;
					Assert.isTrue(returnLabel == null);
				} else if (hasReturnExp == 0 && type.next.ty != Tvoid)
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.FunctionMustReturnAResultOfType, 0,
							ident.start, ident.length, new String[] { type.next
									.toString() }));
				else if (!inlineAsm) {
					if (type.next.ty == Tvoid) {
						if (offend && isMain()) { // Add a return 0; statement
							Statement s = new ReturnStatement(loc,
									new IntegerExp(loc, 0));
							s.synthetic = true;
							fbody = new CompoundStatement(loc, fbody, s);
							fbody.synthetic = true;
						}
					} else {
						if (offend) {
							Expression e;

							if (context.global.params.warnings) {
								error("warning - no return at end of function");
							}

							if (context.global.params.useAssert
									&& !context.global.params.useInline) { /*
							 * Add
							 * an
							 * assert(0,
							 * msg);
							 * where
							 * the
							 * missing
							 * return
							 * should
							 * be.
							 */
								e = new AssertExp(loc, new IntegerExp(loc, 0),
										new StringExp(loc,
												missing_return_expression));
								e.synthetic = true;
							} else {
								e = new HaltExp(loc);
								e.synthetic = true;
							}
							e = new CommaExp(loc, e, type.next
									.defaultInit(context));
							e.synthetic = true;
							e = e.semantic(sc2, context);
							Statement s = new ExpStatement(loc, e);
							s.synthetic = true;
							fbody = new CompoundStatement(loc, fbody, s);
							fbody.synthetic = true;
						}
					}
				}
			}

			{
				List<Statement> a = new ArrayList<Statement>();

				// Merge in initialization of 'out' parameters
				if (parameters != null) {
					for (int i = 0; i < parameters.size(); i++) {
						VarDeclaration v;

						v = (VarDeclaration) parameters.get(i);
						if ((v.storage_class & (STCout | STCin)) == STCout) {
							Assert.isNotNull(v.init);
							ExpInitializer ie = v.init.isExpInitializer();
							Assert.isNotNull(ie);
							ExpStatement es = new ExpStatement(loc, ie.exp);
							es.synthetic = true;
							a.add(es);
						}
					}
				}

				if (argptr != null) { // Initialize _argptr to point past
					// non-variadic arg
					Expression e1;
					Expression e;
					Type t = argptr.type;
					VarDeclaration p;
					int offset;

					e1 = new VarExp(loc, argptr);
					if (parameters != null && parameters.size() > 0)
						p = (VarDeclaration) parameters
								.get(parameters.size() - 1);
					else
						p = v_arguments; // last parameter is _arguments[]
					offset = p.type.size(loc, context);
					offset = (offset + 3) & ~3; // assume stack aligns on 4
					e = new SymOffExp(loc, p, offset, context);
					e.synthetic = true;
					e = new AssignExp(loc, e1, e);
					e.synthetic = true;
					e.type = t;
					ExpStatement es = new ExpStatement(loc, e);
					es.synthetic = true;
					a.add(es);
				}

				if (_arguments != null) {
					/*
					 * Advance to elements[] member of TypeInfo_Tuple with:
					 * _arguments = v_arguments.elements;
					 */
					Expression e = new VarExp(loc, v_arguments);
					e.synthetic = true;
					e = new DotIdExp(loc, e,
							new IdentifierExp(loc, Id.elements));
					e.synthetic = true;
					Expression e1 = new VarExp(loc, _arguments);
					e1.synthetic = true;
					e = new AssignExp(loc, e1, e);
					e.synthetic = true;
					e = e.semantic(sc, context);
					ExpStatement es = new ExpStatement(loc, e);
					es.synthetic = true;
					a.add(es);
				}

				// Merge contracts together with body into one compound
				// statement

				if (context._DH) {
					if (frequire != null && context.global.params.useIn) {
						frequire.incontract = true;
						a.add(frequire);
					}
				} else {
					if (frequire != null && context.global.params.useIn)
						a.add(frequire);
				}

				// Precondition invariant
				if (addPreInvariant(context)) {
					Expression e = null;
					if (isDtorDeclaration() != null) {
						// Call invariant directly only if it exists
						InvariantDeclaration inv = ad.inv;
						ClassDeclaration cd = ad.isClassDeclaration();

						while (inv == null && cd != null) {
							cd = cd.baseClass;
							if (cd == null)
								break;
							inv = cd.inv;
						}
						if (inv != null) {
							e = new DsymbolExp(loc, inv);
							e = new CallExp(loc, e);
							e = e.semantic(sc2, context);
						}
					} else { // Call invariant virtually
						ThisExp v = new ThisExp(loc);
						v.type = vthis.type;
						v.synthetic = true;
						Expression se = new StringExp(loc, null_this);
						se.synthetic = true;
						se = se.semantic(sc, context);
						se.type = Type.tchar.arrayOf(context);
						e = new AssertExp(loc, v, se);
						e.synthetic = true;
					}
					if (e != null) {
						ExpStatement s = new ExpStatement(loc, e);
						s.synthetic = true;
						a.add(s);
					}
				}

				if (fbody != null) {
					a.add(fbody);
				}

				if (fensure != null) {
					a.add(returnLabel.statement);

					if (type.next.ty != Tvoid) {
						// Create: return vresult;
						Assert.isNotNull(vresult);
						Expression e = new VarExp(loc, vresult);
						e.synthetic = true;
						if (tintro != null) {
							e = e.implicitCastTo(sc, tintro.next, context);
							e = e.semantic(sc, context);
						}
						ReturnStatement s = new ReturnStatement(loc, e);
						s.synthetic = true;
						a.add(s);
					}
				}

				fbody = new CompoundStatement(loc, a);
				fbody.synthetic = true;
			}

			sc2.callSuper = 0;
			sc2.pop();
		}
		semanticRun = 2;
	}

	public boolean addPreInvariant(SemanticContext context) {
		AggregateDeclaration ad = isThis();
		return (ad != null
				&& context.global.params.useInvariants
				&& (protection == PROT.PROTpublic || protection == PROT.PROTexport) && !naked);
	}

	public boolean addPostInvariant(SemanticContext context) {
		AggregateDeclaration ad = isThis();
		return (ad != null
				&& ad.inv != null
				&& context.global.params.useInvariants
				&& (protection == PROT.PROTpublic || protection == PROT.PROTexport) && !naked);
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
		return CharOperation.equals(ident.ident, Id.main)
				&& linkage != LINK.LINKc && isMember() == null && !isNested();
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
			if (a.aliassym == null && a.type.ty != Tident
					&& a.type.ty != Tinstance) {
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

	public FuncDeclaration overloadResolve(List<Expression> arguments,
			SemanticContext context) {
		// TODO semantic
		return null;
	}

	public boolean canInline(boolean hasthis, SemanticContext context) {
		return canInline(hasthis, false, context);
	}

	public boolean canInline(boolean hasthis, boolean hdrscan,
			SemanticContext context) {
		InlineCostState ics = new InlineCostState();
		int cost;

		if (needThis() && !hasthis)
			return false;

		if (inlineNest || (semanticRun == 0 && !hdrscan)) {
			return false;
		}

		switch (inlineStatus) {
		case ILSyes:
			return true;

		case ILSno:
			return false;

		case ILSuninitialized:
			break;

		default:
			throw new IllegalStateException("assert(0);");
		}

		if (type != null) {
			if (type.ty != Tfunction) {
				throw new IllegalStateException("assert(type.ty == Tfunction);");
			}
			TypeFunction tf = (TypeFunction) type;
			if (tf.varargs == 1) { // no variadic parameter lists
				// goto Lno;
				if (!hdrscan) // Don't modify inlineStatus for header content scan
					inlineStatus = ILSno;
				return false;
			}

			/* Don't inline a function that returns non-void, but has
			 * no return expression.
			 */
			if (tf.next != null && tf.next.ty != Tvoid
					&& (hasReturnExp & 1) == 0 && !hdrscan) {
				// goto Lno
				if (!hdrscan) // Don't modify inlineStatus for header content scan
					inlineStatus = ILSno;
				return false;
			}
		} else {
			CtorDeclaration ctor = isCtorDeclaration();

			if (ctor != null && ctor.varargs == 1) {
				// goto Lno
				if (!hdrscan) // Don't modify inlineStatus for header content scan
					inlineStatus = ILSno;
				return false;
			}
		}

		if (fbody == null || !hdrscan
				&& (isSynchronized() || isImportedSymbol() || nestedFrameRef || // no nested references to this frame
				(isVirtual(context) && !isFinal()))) {
			// goto Lno;
			if (!hdrscan) // Don't modify inlineStatus for header content scan
				inlineStatus = ILSno;
			return false;
		}

		/* If any parameters are Tsarray's (which are passed by reference)
		 * or out parameters (also passed by reference), don't do inlining.
		 */
		if (parameters != null) {
			for (int i = 0; i < parameters.size(); i++) {
				VarDeclaration v = (VarDeclaration) parameters.get(i);
				if (v.isOut() || v.isRef()
						|| v.type.toBasetype(context).ty == Tsarray) {
					// goto Lno;
					if (!hdrscan) // Don't modify inlineStatus for header content scan
						inlineStatus = ILSno;
					return false;
				}
			}
		}

		// TODO semantic
		// memset(&ics, 0, sizeof(ics));

		ics.hasthis = hasthis;
		ics.fd = this;
		ics.hdrscan = hdrscan;
		cost = fbody.inlineCost(ics, context);
		if (cost >= COST_MAX) {
			// goto Lno;
			if (!hdrscan) // Don't modify inlineStatus for header content scan
				inlineStatus = ILSno;
			return false;
		}

		if (!hdrscan) // Don't scan recursively for header content scan
			inlineScan(context);

		// Lyes:
		if (!hdrscan) // Don't modify inlineStatus for header content scan
			inlineStatus = ILSyes;
		return true;

		//	Lno:
		//	    if (!hdrscan)    // Don't modify inlineStatus for header content scan
		//		inlineStatus = ILSno;
		//	    return false;
	}

	public void bodyToCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		if (fbody != null
				&& (!hgs.hdrgen || hgs.tpltMember || canInline(true, true,
						context))) {
			buf.writenl();

			// in{}
			if (frequire != null) {
				buf.writestring("in");
				buf.writenl();
				frequire.toCBuffer(buf, hgs, context);
			}

			// out{}
			if (fensure != null) {
				buf.writestring("out");
				if (outId != null) {
					buf.writebyte('(');
					buf.writestring(outId.toChars(context));
					buf.writebyte(')');
				}
				buf.writenl();
				fensure.toCBuffer(buf, hgs, context);
			}

			if (frequire != null || fensure != null) {
				buf.writestring("body");
				buf.writenl();
			}

			buf.writebyte('{');
			buf.writenl();
			fbody.toCBuffer(buf, hgs, context);
			buf.writebyte('}');
			buf.writenl();
		} else {
			buf.writeByte(';');
			buf.writenl();
		}
	}

	public LabelDsymbol searchLabel(IdentifierExp ident) {
		Dsymbol s;

		if (null == labtab)
			labtab = new DsymbolTable(); // guess we need one

		s = labtab.lookup(ident);
		if (null == s) {
			s = new LabelDsymbol(ident);
			labtab.insert(s);
		}
		return (LabelDsymbol) s;
	}

}