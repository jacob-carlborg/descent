package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.STC.STCabstract;
import static descent.internal.compiler.parser.STC.STCauto;
import static descent.internal.compiler.parser.STC.STCdeprecated;
import static descent.internal.compiler.parser.STC.STCfinal;
import static descent.internal.compiler.parser.STC.STCscope;
import static descent.internal.compiler.parser.STC.STCstatic;

import static descent.internal.compiler.parser.TY.Tclass;

// DMD 1.020
public class ClassDeclaration extends AggregateDeclaration implements IClassDeclaration {

	public final static int OFFSET_RUNTIME = 0x76543210;
	
	public BaseClasses sourceBaseclasses;
	public BaseClasses baseclasses;
	
	public IClassDeclaration baseClass; // null only if this is Object
	public ICtorDeclaration ctor;
	public ICtorDeclaration defaultCtor; // default constructor
	public FuncDeclarations dtors; // Array of destructors
	public FuncDeclaration staticCtor;
	public FuncDeclaration staticDtor;
	public BaseClasses interfaces;
	public BaseClasses vtblInterfaces; // array of base interfaces that
	// have
	// their own vtbl[]
	public PROT protection;
	public boolean isnested; // !=0 if is nested
	public IVarDeclaration vthis; // 'this' parameter if this class is nested

	public ClassInfoDeclaration vclassinfo; // the ClassInfo object for this ClassDeclaration
	public boolean com; // !=0 if this is a COM class
	public boolean isauto; // !=0 if this is an auto class
	public boolean isabstract; // !=0 if abstract class
	public List vtbl; // Array of FuncDeclaration's making up the vtbl[]
	public List vtblFinal; // More FuncDeclaration's that aren't in vtbl[]

	public ClassDeclaration(Loc loc, char[] id) {
		this(loc, id, null);
	}

	public ClassDeclaration(Loc loc, char[] id, BaseClasses baseclasses) {
		this(loc, new IdentifierExp(loc, id), baseclasses);
	}

	public ClassDeclaration(Loc loc, IdentifierExp id,
			BaseClasses baseclasses) {
		super(loc, id);
		if (baseclasses == null) {
			this.baseclasses = new BaseClasses(0);
		} else {
			this.baseclasses = baseclasses;
			this.sourceBaseclasses = new BaseClasses(baseclasses
					.size());
			this.sourceBaseclasses.addAll(baseclasses);
		}
		this.type = new TypeClass(this);
		this.vtbl = new ArrayList(0);
		this.vtblFinal = new ArrayList(0);
		handle = type;

		// TODO missing semantic scode
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, modifiers);
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, sourceBaseclasses);
			TreeVisitor.acceptChildren(visitor, members);
			
			acceptSynthetic(visitor);
		}
		visitor.endVisit(this);
	}

	@Override
	public void addLocalClass(ClassDeclarations aclasses,
			SemanticContext context) {
		aclasses.add(this);
	}

	public IFuncDeclaration findFunc(IdentifierExp id, TypeFunction tf,
			SemanticContext context) {
		IClassDeclaration cd = this;
		List vtbl = cd.vtbl();
		while (true) {
			for (int i = 0; i < vtbl.size(); i++) {
				IFuncDeclaration fd = (IFuncDeclaration) vtbl.get(i);

				if (equals(ident, fd.ident()) && fd.type().covariant(tf, context) == 1) {
					return fd;
				}
			}
			if (cd == null) {
				break;
			}
			vtbl = cd.vtblFinal();
			cd = cd.baseClass();
		}

		return null;
	}

	@Override
	public PROT getAccess(IDsymbol smember) {
		return SemanticMixin.getAccess(this, smember);
	}

	@Override
	public int getNodeType() {
		return CLASS_DECLARATION;
	}

	public void interfaceSemantic(Scope sc, SemanticContext context) {
		int i;

		vtblInterfaces = new BaseClasses(interfaces.size());

		for (i = 0; i < interfaces.size(); i++) {
			BaseClass b = interfaces.get(i);

			// If this is an interface, and it derives from a COM interface,
			// then this is a COM interface too.
			if (b.base.isCOMclass()) {
				com = true;
			}

			vtblInterfaces.add(b);
			b.copyBaseInterfaces(vtblInterfaces);
		}
	}

	public boolean isAbstract() {
		if (isabstract) {
			return true;
		}
		for (int i = 1; i < vtbl.size(); i++) {
			IFuncDeclaration fd = ((IDsymbol) vtbl.get(i)).isFuncDeclaration();

			if (fd == null || fd.isAbstract()) {
				isabstract |= true;
				return true;
			}
		}
		return false;
	}

	public boolean isBaseOf(IClassDeclaration cd, int[] poffset,
			SemanticContext context) {
		if (poffset != null) {
			poffset[0] = 0;
		}
		while (cd != null) {
			if (this == cd.baseClass()) {
				return true;
			}

			/*
			 * cd.baseClass might not be set if cd is forward referenced.
			 */
			if (cd.baseClass() == null && cd.baseclasses().size() > 0
					&& cd.isInterfaceDeclaration() == null) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.BaseClassIsForwardReferenced, this, new String[] { toChars(context) }));
			}

			cd = cd.baseClass();
		}
		return false;
	}

	public boolean isBaseOf2(IClassDeclaration cd) {
		if (cd == null) {
			return false;
		}
		for (int i = 0; i < cd.baseclasses().size(); i++) {
			BaseClass b = cd.baseclasses().get(i);

			if (b.base == this || isBaseOf2(b.base)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ClassDeclaration isClassDeclaration() {
		return this;
	}

	public boolean isCOMclass() {
		return com;
	}

	public boolean isNested() {
		return isnested;
	}

	@Override
	public String kind() {
		return "class";
	}

	@Override
	public IDsymbol search(Loc loc, char[] ident, int flags,
			SemanticContext context) {
		IDsymbol s;

		if (scope != null) {
			semantic(scope, context);
		}

		if (members == null || symtab == null || scope != null) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.ForwardReferenceWhenLookingFor, this,
					new String[] { new String(this.ident.ident),
							new String(ident) }));
			return null;
		}

		s = super.search(loc, ident, flags, context);
		if (s == null) {
			// Search bases classes in depth-first, left to right order

			int i;

			for (i = 0; i < baseclasses.size(); i++) {
				BaseClass b = baseclasses.get(i);

				if (b.base != null) {
					if (b.base.symtab() == null) {
						context.acceptProblem(Problem.newSemanticTypeError(
								IProblem.BaseIsForwardReferenced, this, new String[] { b.base.ident()
										.toChars() }));
					} else {
						s = b.base.search(loc, ident, flags, context);
						if (s == this) {
							// derives from this
							s = null;
						} else if (s != null) {
							break;
						}
					}
				}
			}
		}
		return s;
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		int i;
		// int offset;

		if (ident == null) { // if anonymous class
			String id = "__anonclass";
			ident = context.generateId(id);
		}
		
		if (null == scope) {
			if (parent == null && sc.parent != null
					&& sc.parent.isModule() == null) {
				parent = sc.parent;
			}

			type = type.semantic(loc, sc, context);
			handle = handle.semantic(loc, sc, context);
		}
		if (members == null) // if forward reference
		{
			return;
		}
		if (symtab != null) {
			if (scope == null) {
				return; // semantic() already completed
			}
		} else {
			symtab = new DsymbolTable();
		}

		Scope scx = null;
		if (scope != null) {
			sc = scope;
			scx = scope; // save so we don't make redundant copies
			scope = null;
		}

		// Expand any tuples in baseclasses[]
		for (i = 0; i < baseclasses.size();) {
			BaseClass b = baseclasses.get(i);
			b.type = b.type.semantic(loc, sc, context);
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
			} else {
				i++;
			}
		}

		// See if there's a base class as first in baseclasses[]
		if (baseclasses.size() > 0) {
			TypeClass tc;
			BaseClass b;
			Type tb;

			b = baseclasses.get(0);
			// b.type = b.type.semantic(loc, sc);
			tb = b.type.toBasetype(context);
			if (tb.ty != TY.Tclass) {
				// If already reported error, don't report it twice
				if (tb.ty != TY.Terror) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.BaseTypeMustBeClassOrInterface, b.sourceType));
				}
				baseclasses.remove(0);
			} else {
				tc = (TypeClass) (tb);
				if (tc.sym.isInterfaceDeclaration() != null) {
					;
				} else {
					boolean gotoL7 = false;
					for (IClassDeclaration cdb = tc.sym; cdb != null; cdb = cdb.baseClass()) {
						if (SemanticMixin.equals(cdb, this)) {
							BaseClass firstBaseClass = this.baseclasses.get(0);
							context.acceptProblem(Problem.newSemanticTypeError(
									IProblem.CircularDefinition, firstBaseClass.sourceType, new String[] { toChars(context) }));
							baseclasses.remove(0);
							// goto L7;
							gotoL7 = true;
							break;
						}
					}
					if (!gotoL7) {
						if (tc.sym.symtab() == null || tc.sym.scope() != null
								|| tc.sym.sizeok() == 0) {
							// error("forward reference of base class %s",
							// baseClass.toChars());
							// Forward reference of base class, try again later
							// printf("\ttry later, forward reference of base
							// class %s\n",
							// tc.sym.toChars());
							scope = scx != null ? scx : new Scope(sc, context);
							scope.setNoFree();
							scope.module.addDeferredSemantic(this, context);
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

			b = baseclasses.get(i);
			b.type = b.type.semantic(loc, sc, context);
			tb = b.type.toBasetype(context);
			if (tb.ty == TY.Tclass) {
				tc = (TypeClass) tb;
			} else {
				tc = null;
			}
			if (tc == null || tc.sym.isInterfaceDeclaration() == null) {
				// If already reported error, don't report it twice
				if (tb.ty != TY.Terror) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.BaseTypeMustBeClassOrInterface, b.sourceType));
				}
				baseclasses.remove(i);
				continue;
			} else {
				// Check for duplicate interfaces
				for (int j = (baseClass != null ? 1 : 0); j < i; j++) {
					BaseClass b2 = baseclasses.get(j);
					if (b2.base == tc.sym) {
						context.acceptProblem(Problem.newSemanticTypeError(
								IProblem.DuplicatedInterfaceInheritance, b.sourceType,
								new String[] { b.sourceType.toString(),
										new String(this.ident.ident) }));
					}
				}

				b.base = tc.sym;
				if (b.base.symtab() == null || b.base.scope() != null) {
					// error("forward reference of base class %s",
					// baseClass.toChars());
					// Forward reference of base, try again later
					// printf("\ttry later, forward reference of base %s\n",
					// baseClass.toChars());
					scope = scx != null ? scx : new Scope(sc, context);
					scope.setNoFree();
					scope.module.addDeferredSemantic(this, context);
					return;
				}
			}
			i++;
		}

		// If no base class, and this is not an Object, use Object as base class
		if (baseClass == null && !equals(ident, Id.Object)) {
			// BUG: what if Object is redefined in an inner scope?
			Type tbase = new TypeIdentifier(loc, Id.Object);
			BaseClass b;
			TypeClass tc;
			Type bt;

			if (context.ClassDeclaration_object == null) {
				context.acceptProblem(Problem.newSemanticTypeErrorLoc(
						IProblem.MissingOrCurruptObjectDotD, this));
				fatal(context);
			}
			bt = tbase.semantic(loc, sc, context).toBasetype(context);
			b = new BaseClass(bt, PROT.PROTpublic);
			baseclasses.add(0, b);
			if (b.type.ty != Tclass) {
				
				// This may happen if object.d is not found.
				// So, just return: another error somewhere else be reported
				return;
				
				//throw new IllegalStateException("assert(b.type.ty == Tclass);");
			}
			tc = (TypeClass) (b.type);
			baseClass = tc.sym;
			if (baseClass.isInterfaceDeclaration() != null) {
				throw new IllegalStateException(
						"assert(!baseClass->isInterfaceDeclaration());");
			}
			b.base = baseClass;
		}

		interfaces = new BaseClasses(baseclasses.size());
		interfaces.addAll(baseclasses);

		if (baseClass != null) {
			if ((baseClass.storage_class() & STCfinal) != 0) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.CannotInheritFromFinalClass, this, new String[] { baseClass
								.toString() }));
			}

			interfaces.remove(0);

			// Copy vtbl[] from base class
			if (baseClass.vtbl() != null) {
				vtbl = new ArrayList(baseClass.vtbl().size());
				vtbl.addAll(baseClass.vtbl());
			}

			// Inherit properties from base class
			com = baseClass.isCOMclass();
			isauto = baseClass.isauto();
			vthis = baseClass.vthis();
		} else {
			// No base class, so this is the root of the class hierarchy
			vtbl = new ArrayList(1);
			vtbl.add(this); // leave room for classinfo as first member
		}

		protection = sc.protection;
		storage_class |= sc.stc;

		if (sizeok == 0) {
			interfaceSemantic(sc, context);

			for (IDsymbol s : members) {
				s.addMember(sc, this, 1, context);
			}

			/*
			 * If this is a nested class, add the hidden 'this' member which is
			 * a pointer to the enclosing scope.
			 */
			if (vthis != null) // if inheriting from nested class
			{ // Use the base class's 'this' member
				isnested = true;
				if ((storage_class & STC.STCstatic) != 0) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.StaticClassCannotInheritFromNestedClass, this, new String[] { baseClass.toChars(context) }));
				}
				if (toParent2() != baseClass.toParent2()) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.SuperClassIsNestedWithin, this, new String[] { baseClass.toChars(context), baseClass.toParent2()
									.toChars(context), toParent2().toChars(
											context) }));
				}
			} else if ((storage_class & STC.STCstatic) == 0) {
				IDsymbol s = toParent2();
				if (s != null) {
					IClassDeclaration cd = s.isClassDeclaration();
					IFuncDeclaration fd = s.isFuncDeclaration();

					if (cd != null || fd != null) {
						isnested = true;
						Type t = null;
						if (cd != null) {
							t = cd.type();
						} else if (fd != null) {
							IAggregateDeclaration ad = fd.isMember2();
							if (ad != null) {
								t = ad.handle();
							} else {
								t = new TypePointer(Type.tvoid);
								t = t.semantic(loc, sc, context);
							}
						} else {
							Assert.isTrue(false);
						}
						Assert.isTrue(vthis == null);
						vthis = new ThisDeclaration(loc, t);
						members.add(vthis);
					}
				}
			}
		}

		if ((storage_class & (STC.STCauto | STC.STCscope)) != 0) {
			isauto = true;
		}
		if ((storage_class & STC.STCabstract) != 0) {
			isabstract = true;
		}
		if ((storage_class & STC.STCdeprecated) != 0) {
			isdeprecated = true;
		}

		sc = sc.push(this);
		sc.stc &= ~(STCfinal | STCauto | STCscope | STCstatic | STCabstract | STCdeprecated);

		sc.parent = this;
		sc.inunion = false;

		if (isCOMclass()) {
			sc.linkage = LINK.LINKwindows;
		}
		sc.protection = PROT.PROTpublic;
		sc.explicitProtection = 0;
		sc.structalign = 8;
		structalign = sc.structalign;
		if (baseClass != null) {
			sc.offset = baseClass.structsize();
			alignsize = baseClass.alignsize();
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
			IDsymbol s = members.get(i);
			s.semantic(sc, context);
		}

		if (sizeok == 2) { // semantic() failed because of forward
			// references.
			// Unwind what we did, and defer it for later
			fields.clear();
			structsize = 0;
			alignsize = 0;
			structalign = 0;

			sc = sc.pop();

			scope = scx != null ? scx : new Scope(sc, context);
			scope.setNoFree();
			scope.module.addDeferredSemantic(this, context);

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
		ctor = (ICtorDeclaration) search(loc, Id.ctor, 0, context);
		if (ctor != null && ctor.toParent() != this) {
			ctor = null;
		}

		// dtor = (DtorDeclaration *)search(Id::dtor, 0);
		// if (dtor && dtor.toParent() != this)
		// dtor = NULL;

		// inv = (InvariantDeclaration *)search(Id::classInvariant, 0);
		// if (inv && inv.toParent() != this)
		// inv = NULL;

		// Can be in base class
		aggNew = (NewDeclaration) search(loc, Id.classNew, 0, context);
		aggDelete = (DeleteDeclaration) search(loc, Id.classDelete, 0, context);

		// If this class has no constructor, but base class does, create
		// a constructor:
		// this() { }
		if (ctor == null && baseClass != null && baseClass.ctor() != null) {
			// toChars());
			CtorDeclaration ctor = new CtorDeclaration(loc, null, 0);
			ctor.synthetic = true;
			ctor.fbody = new CompoundStatement(loc, new Statements());
			this.ctor = ctor;
			
			members.add(ctor);
			ctor.addMember(sc, this, 1, context);
			sc = scsave;
			sc.offset = structsize;
			ctor.semantic(sc, context);
			defaultCtor = ctor;
		}

		 // Allocate instance of each new interface
        for (i = 0; i < vtblInterfaces.size(); i++)
        {
            BaseClass b = (BaseClass) vtblInterfaces.get(i);
            int thissize = Type.PTRSIZE;
            
            int[] p_sc_offset = new int[]
            { sc.offset };
            alignmember(structalign, thissize, p_sc_offset);
            sc.offset = p_sc_offset[0];
            Assert.isTrue(b.offset == 0);
            b.offset = sc.offset; // Take care of single inheritance offsets
            while (b.baseInterfaces.size() > 0)
            {
                b = b.baseInterfaces.get(0);
                b.offset = sc.offset;
            }
            
            sc.offset += thissize;
            if (alignsize < thissize)
                alignsize = thissize;
        }

		structsize = sc.offset;
		sizeok = 1;

		context.Module_dprogress++;

		sc.pop();
	}

	@Override
	public IDsymbol syntaxCopy(IDsymbol s, SemanticContext context) {
		ClassDeclaration cd;

		if (s != null) {
			cd = (ClassDeclaration) s;
		} else {
			cd = new ClassDeclaration(loc, ident, null);
		}

		cd.storage_class |= storage_class;

		cd.baseclasses = new BaseClasses(this.baseclasses.size());
		for (int i = 0; i < this.baseclasses.size(); i++) {
			BaseClass b = this.baseclasses.get(i);
			BaseClass b2 = new BaseClass(b.type.syntaxCopy(context), b.protection);
			cd.baseclasses.add(b2);
		}

		super.syntaxCopy(cd, context);
		return cd;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		if (!isAnonymous()) {
			buf.writestring(kind());
			buf.writestring(toChars(context));
			if (baseclasses.size() > 0) {
				buf.writestring(" : ");
			}
		}
		for (int i = 0; i < baseclasses.size(); i++) {
			BaseClass b = baseclasses.get(i);

			if (i != 0) {
				buf.writeByte(',');
			}
			b.type.toCBuffer(buf, null, hgs, context);
		}
		buf.writenl();
		buf.writeByte('{');
		buf.writenl();
		for (int i = 0; i < members.size(); i++) {
			IDsymbol s = members.get(i);

			buf.writestring("    ");
			s.toCBuffer(buf, hgs, context);
		}
		buf.writestring("}");
		buf.writenl();
	}

	public int vtblOffset() {
		return 1;
	}

	@Override
	public String mangle(SemanticContext context) {
		return SemanticMixin.mangle(this, context);
	}
	
	public IClassDeclaration baseClass() {
		return baseClass;
	}
	
	public BaseClasses interfaces() {
		return interfaces;
	}
	
	public BaseClasses baseclasses() {
		return baseclasses;
	}
	
	public void isabstract(boolean isabstract) {
		this.isabstract = isabstract;
	}
	
	public List vtbl() {
		return vtbl;
	}
	
	public List vtblFinal() {
		return vtblFinal;
	}
	
	public ICtorDeclaration ctor() {
		return ctor;
	}
	
	public void defaultCtor(CtorDeclaration defaultCtor) {
		this.defaultCtor = defaultCtor;
	}
	
	public FuncDeclarations dtors() {
		return dtors;
	}
	
	public void dtors(FuncDeclarations dtors) {
		this.dtors = dtors;
	}
	
	public Scope scope() {
		return scope;
	}
	
	public boolean isauto() {
		return isauto;
	}
	
	public IVarDeclaration vthis() {
		return vthis;
	}
	
	public ClassInfoDeclaration vclassinfo() {
		return vclassinfo;
	}
	
	public void vclassinfo(ClassInfoDeclaration vclassinfo) {
		this.vclassinfo = vclassinfo;
	}
	
	public char getSignaturePrefix() {
		return ISignatureConstants.CLASS;
	}

}
