package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;

public class ClassDeclaration extends AggregateDeclaration {
	
	public List<BaseClass> sourceBaseclasses;

	public List<BaseClass> baseclasses;	
	public ClassDeclaration baseClass;			// null only if this is Object
	public CtorDeclaration ctor;
	public List<FuncDeclaration> dtors;		// Array of destructors
	public FuncDeclaration staticCtor;
	public FuncDeclaration staticDtor;
	public List<BaseClass> interfaces;
	public PROT protection;
	public boolean isnested;					// !=0 if is nested
	public VarDeclaration vthis;				// 'this' parameter if this class is nested
	public boolean com;							// !=0 if this is a COM class
	public List vtbl;			// Array of FuncDeclaration's making up the vtbl[]
    public List vtblFinal;		// More FuncDeclaration's that aren't in vtbl[]

	public ClassDeclaration(IdentifierExp id, List<BaseClass> baseclasses) {
		super(id);
		if (baseclasses == null) {
			this.baseclasses = new ArrayList<BaseClass>(0);
		} else {
			this.baseclasses = baseclasses;
			this.sourceBaseclasses = new ArrayList<BaseClass>(baseclasses.size());
			this.sourceBaseclasses.addAll(baseclasses);
		}
		this.type = new TypeClass(this);
		this.vtbl = new ArrayList(0);
		this.vtblFinal = new ArrayList(0);
		handle = type;
	}
	
	@Override
	public ClassDeclaration isClassDeclaration() {
		return this;
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		int i;
		int offset;

		if (ident == null) { // if anonymous class
			String id = "__anonclass";
			ident = context.generateId(id);
		}

		if (scope != null) {
			if (parent == null && sc.parent != null
					&& sc.parent.isModule() == null)
				parent = sc.parent;

			type = type.semantic(sc, context);
			handle = handle.semantic(sc, context);
		}
		if (members == null) // if forward reference
		{
			return;
		}
		if (symtab != null) {
			if (scope == null) {
				return; // semantic() already completed
			}
		} else
			symtab = new DsymbolTable();

		Scope scx = null;
		if (scope != null) {
			sc = scope;
			scx = scope; // save so we don't make redundant copies
			scope = null;
		}

		// Expand any tuples in baseclasses[]
		for (i = 0; i < baseclasses.size();) {
			BaseClass b = (BaseClass) baseclasses.get(i);
			b.type = b.type.semantic(sc, context);
			Type tb = b.type.toBasetype(context);

			if (tb.ty == TY.Ttuple) {
				TypeTuple tup = (TypeTuple) tb;
				PROT protection = b.protection;
				baseclasses.remove(i);
				int dim = Argument.dim(tup.arguments, context);
				for (int j = 0; j < dim; j++) {
					Argument arg = Argument.getNth(tup.arguments, j, context);
					b = new BaseClass(arg.type, protection);
					baseclasses.add(i + j, b);
				}
			} else
				i++;
		}

		// See if there's a base class as first in baseclasses[]
		if (baseclasses.size() > 0) {
			TypeClass tc;
			BaseClass b;
			Type tb;

			b = (BaseClass) baseclasses.get(0);
			// b.type = b.type.semantic(loc, sc);
			tb = b.type.toBasetype(context);
			if (tb.ty != TY.Tclass) {
				// If already reported error, don't report it twice
				if (tb.ty != TY.Terror) {
					context.acceptProblem(Problem.newSemanticTypeError(
							"Base type must be class or interface",
							IProblem.BaseTypeMustBeClassOrInterface, 0,
							b.sourceType.start, b.sourceType.length));
				}
				baseclasses.remove(0);
			} else {
				tc = (TypeClass) (tb);
				if (tc.sym.isInterfaceDeclaration() != null)
					;
				else {
					boolean gotoL7 = false;
					for (ClassDeclaration cdb = tc.sym; cdb != null; cdb = cdb.baseClass) {
						if (cdb == this) {
							BaseClass firstBaseClass = this.baseclasses.get(0);
							context.acceptProblem(Problem.newSemanticTypeError(
									"Circular inheritance",
									IProblem.CircularDefinition, 0,
									firstBaseClass.sourceType.start,
									firstBaseClass.sourceType.length));
							baseclasses.remove(0);
							// goto L7;
							gotoL7 = true;
							break;
						}
					}
					if (!gotoL7) {
						if (tc.sym.symtab == null || tc.sym.scope != null
								|| tc.sym.sizeok == 0) {
							// error("forward reference of base class %s",
							// baseClass.toChars());
							// Forward reference of base class, try again later
							// printf("\ttry later, forward reference of base
							// class %s\n",
							// tc.sym.toChars());
							scope = scx != null ? scx : new Scope(sc);
							scope.setNoFree();
							scope.module.addDeferredSemantic(this);
							return;
						} else {
							baseClass = tc.sym;
							b.base = baseClass;
						}
					}
					// L7: ;
				}
			}
		}

		// Treat the remaining entries in baseclasses as interfaces
		// Check for errors, handle forward references
		for (i = (baseClass != null ? 1 : 0); i < baseclasses.size();) {
			TypeClass tc;
			BaseClass b;
			Type tb;

			b = (BaseClass) baseclasses.get(i);
			b.type = b.type.semantic(sc, context);
			tb = b.type.toBasetype(context);
			if (tb.ty == TY.Tclass)
				tc = (TypeClass) tb;
			else
				tc = null;
			if (tc == null || tc.sym.isInterfaceDeclaration() == null) {
				// If already reported error, don't report it twice
				if (tb.ty != TY.Terror) {
					context.acceptProblem(Problem.newSemanticTypeError(
							"Base type must be class or interface",
							IProblem.BaseTypeMustBeClassOrInterface, 0,
							b.sourceType.start, b.sourceType.length));
				}
				baseclasses.remove(i);
				continue;
			} else {
				// Check for duplicate interfaces
				for (int j = (baseClass != null ? 1 : 0); j < i; j++) {
					BaseClass b2 = (BaseClass) baseclasses.get(j);
					if (b2.base == tc.sym) {
						context.acceptProblem(Problem.newSemanticTypeError(
								"Duplicated interface " + b.sourceType
										+ " for the type " + this.ident,
								IProblem.DuplicatedInterfaceInheritance, 0,
								b.sourceType.start, b.sourceType.length));
					}
				}

				b.base = tc.sym;
				if (b.base.symtab == null || b.base.scope != null) {
					// error("forward reference of base class %s",
					// baseClass.toChars());
					// Forward reference of base, try again later
					// printf("\ttry later, forward reference of base %s\n",
					// baseClass.toChars());
					scope = scx != null ? scx : new Scope(sc);
					scope.setNoFree();
					scope.module.addDeferredSemantic(this);
					return;
				}
			}
			i++;
		}

		// If no base class, and this is not an Object, use Object as base class
		if (baseClass == null && ident.ident != Id.Object) {
			// BUG: what if Object is redefined in an inner scope?
			Type tbase = new TypeIdentifier(Id.Object);
			BaseClass b;
			TypeClass tc;
			Type bt;

			if (context.object == null) {
				error("missing or corrupt object.d");
				fatal();
			}
			bt = tbase.semantic(sc, context).toBasetype(context);
			b = new BaseClass(bt, PROT.PROTpublic);
			baseclasses.add(0, b);
			/*
			 * TODO semantic Assert.isTrue(b.type.ty == TY.Tclass); tc =
			 * (TypeClass) (b.type); baseClass = tc.sym;
			 * Assert.isTrue(baseClass.isInterfaceDeclaration() == null); b.base =
			 * baseClass;
			 */
			// TODO semantic remove the following line
			baseClass = new ClassDeclaration(new IdentifierExp(Id.Object), null);
		}

		interfaces = new ArrayList<BaseClass>(baseclasses.size());
		interfaces.addAll(baseclasses);

		if (baseClass != null) {
			interfaces.remove(0);

			// Copy vtbl[] from base class
			if (baseClass.vtbl != null) {
				vtbl = new ArrayList<FuncDeclaration>(baseClass.vtbl.size());
				vtbl.addAll(baseClass.vtbl);
			}

			// Inherit properties from base class
			com = baseClass.isCOMclass();
			isauto = baseClass.isauto;
			vthis = baseClass.vthis;
		} else {
			// No base class, so this is the root of the class hierarchy
			vtbl = new ArrayList<FuncDeclaration>(1);
			vtbl.add(this); // leave room for classinfo as first member
		}

		protection = sc.protection;
		storage_class |= sc.stc;

		if (sizeok == 0) {
			interfaceSemantic(sc, context);

			for (Dsymbol s : members) {
				s.addMember(sc, this, 1, context);
			}

			/*
			 * If this is a nested class, add the hidden 'this' member which is
			 * a pointer to the enclosing scope.
			 */
			if (vthis != null) // if inheriting from nested class
			{ // Use the base class's 'this' member
				isnested = true;
				if ((storage_class & STC.STCstatic) != 0)
					error("static class cannot inherit from nested class %s",
							baseClass.toChars());
				if (toParent2() != baseClass.toParent2())
					error("super class %s is nested within %s, not %s",
							baseClass.toChars(), baseClass.toParent2()
									.toChars(), toParent2().toChars());
			} else if ((storage_class & STC.STCstatic) == 0) {
				Dsymbol s = toParent2();
				if (s != null) {
					ClassDeclaration cd = s.isClassDeclaration();
					FuncDeclaration fd = s.isFuncDeclaration();

					if (cd != null || fd != null) {
						isnested = true;
						Type t = null;
						if (cd != null)
							t = cd.type;
						else if (fd != null) {
							AggregateDeclaration ad = fd.isMember2();
							if (ad != null)
								t = ad.handle;
							else {
								t = new TypePointer(Type.tvoid);
								t = t.semantic(sc, context);
							}
						} else
							Assert.isTrue(false);
						Assert.isTrue(vthis == null);
						vthis = new ThisDeclaration(t);
						members.add(vthis);
					}
				}
			}
		}

		if ((storage_class & (STC.STCauto | STC.STCscope)) != 0)
			isauto = true;
		if ((storage_class & STC.STCabstract) != 0)
			isabstract = true;
		if ((storage_class & STC.STCdeprecated) != 0)
			isdeprecated = true;

		sc = sc.push(this);
		sc.stc &= ~(STC.STCauto | STC.STCscope | STC.STCstatic
				| STC.STCabstract | STC.STCdeprecated);
		sc.parent = this;
		sc.inunion = false;

		if (isCOMclass())
			sc.linkage = LINK.LINKwindows;
		sc.protection = PROT.PROTpublic;
		sc.explicitProtection = 0;
		sc.structalign = 8;
		structalign = sc.structalign;
		if (baseClass != null) {
			sc.offset = baseClass.structsize;
			alignsize = baseClass.alignsize;
			// if (isnested)
			// sc.offset += PTRSIZE; // room for uplevel context pointer
		} else {
			sc.offset = 8; // allow room for vptr[] and monitor
			alignsize = 4;
		}
		structsize = sc.offset;
		Scope scsave = sc;
		int members_dim = members.size();
		sizeok = 0;
		for (i = 0; i < members_dim; i++) {
			Dsymbol s = (Dsymbol) members.get(i);
			s.semantic(sc, context);
		}

		if (sizeok == 2) { // semantic() failed because of forward
			// references.
			// Unwind what we did, and defer it for later
			fields.clear();
			structsize = 0;
			alignsize = 0;
			structalign = 0;

			sc.pop();

			scope = scx != null ? scx : new Scope(sc);
			scope.setNoFree();
			scope.module.addDeferredSemantic(this);

			// printf("\tsemantic('%s') failed\n", toChars());
			return;
		}

		// printf("\tsemantic('%s') successful\n", toChars());

		structsize = sc.offset;
		// members.print();

		/*
		 * Look for special member functions. They must be in this class, not in
		 * a base class.
		 */
		ctor = (CtorDeclaration) search(Id.ctor, 0, context);
		if (ctor != null && ctor.toParent() != this)
			ctor = null;

		// dtor = (DtorDeclaration *)search(Id::dtor, 0);
		// if (dtor && dtor.toParent() != this)
		// dtor = NULL;

		// inv = (InvariantDeclaration *)search(Id::classInvariant, 0);
		// if (inv && inv.toParent() != this)
		// inv = NULL;

		// Can be in base class
		aggNew = (NewDeclaration) search(Id.classNew, 0, context);
		aggDelete = (DeleteDeclaration) search(Id.classDelete, 0, context);

		// If this class has no constructor, but base class does, create
		// a constructor:
		// this() { }
		if (ctor == null && baseClass != null && baseClass.ctor != null) {
			// toChars());
			ctor = new CtorDeclaration(null, 0);
			ctor.fbody = new CompoundStatement(new ArrayList<Statement>());
			members.add(ctor);
			ctor.addMember(sc, this, 1, context);
			sc = scsave;
			sc.offset = structsize;
			ctor.semantic(sc, context);
		}

		/*
		 * TODO semantic // Allocate instance of each new interface for (i = 0;
		 * i < vtblInterfaces.dim; i++) { BaseClass *b = (BaseClass
		 * *)vtblInterfaces.data[i]; unsigned thissize = PTRSIZE;
		 * 
		 * alignmember(structalign, thissize, &sc.offset); assert(b.offset ==
		 * 0); b.offset = sc.offset; // Take care of single inheritance offsets
		 * while (b.baseInterfaces_dim) { b = &b.baseInterfaces[0]; b.offset =
		 * sc.offset; }
		 * 
		 * sc.offset += thissize; if (alignsize < thissize) alignsize =
		 * thissize;
		 */

		structsize = sc.offset;
		sizeok = 1;

		/*
		 * TODO semantic Module::dprogress++;
		 */

		sc.pop();
	}
	    
	public void interfaceSemantic(Scope sc, SemanticContext context) {
		
	}
	
	public boolean isCOMclass() {
		return com;
	}
	
	@Override
	public int getNodeType() {
		return CLASS_DECLARATION;
	}

}

