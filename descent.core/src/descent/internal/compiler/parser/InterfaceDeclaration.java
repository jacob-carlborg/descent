package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.LINK.LINKwindows;

import static descent.internal.compiler.parser.TY.Tclass;
import static descent.internal.compiler.parser.TY.Ttuple;

// DMD 1.020
public class InterfaceDeclaration extends ClassDeclaration {
	
    int cpp;				// !=0 if this is a C++ interface

	public InterfaceDeclaration(Loc loc, IdentifierExp id,
			BaseClasses baseclasses) {
		super(loc, id, baseclasses);
		com = false;
		if (id != null && equals(id, Id.IUnknown)) { // IUnknown is the root
			// of all COM
			// objects
			com = true;
		}
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
	public int getNodeType() {
		return INTERFACE_DECLARATION;
	}

	public boolean isBaseOf(BaseClass bc, int[] poffset) {
		for (int j = 0; j < ASTDmdNode.size(bc.baseInterfaces); j++) {
			BaseClass b = bc.baseInterfaces.get(j);

			if (SemanticMixin.equals(this, b.base)) {
				if (poffset != null) {
					poffset[0] = b.offset;
				}
				return true;
			}
			if (this.isBaseOf(b, poffset)) {
				return true;
			}
		}
		if (poffset != null) {
			poffset[0] = 0;
		}
		return false;
	}

	@Override
	public boolean isBaseOf(ClassDeclaration cd, int[] poffset,
			SemanticContext context) {
		int j;
		
		Assert.isTrue(baseClass == null);
		
		cd.consumeRest();
		
		for (j = 0; j < cd.interfaces.size(); j++) {
			BaseClass b = cd.interfaces.get(j);

			if (SemanticMixin.equals(this, b.base)) {
				if (poffset != null) {
					poffset[0] = b.offset;
					if (j != 0 && cd.isInterfaceDeclaration() != null) {
						poffset[0] = OFFSET_RUNTIME;
					}
				}
				return true;
			}
			if (isBaseOf(b, poffset)) {
				if (j != 0 && poffset != null
						&& cd.isInterfaceDeclaration() != null) {
					poffset[0] = OFFSET_RUNTIME;
				}
				return true;
			}
		}

		if (cd.baseClass != null && isBaseOf(cd.baseClass, poffset, context)) {
			return true;
		}

		if (poffset != null) {
			poffset[0] = 0;
		}
		return false;
	}

	@Override
	public InterfaceDeclaration isInterfaceDeclaration() {
		return this;
	}

	@Override
	public String kind() {
		return "interface";
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		if (rest != null && !rest.isConsumed()) {
			if (rest.getScope() == null) {
				rest.setSemanticContext(sc, context);
			}
			return;
		}
		
		int i;

		if (scope == null) {
			type = type.semantic(loc, sc, context);
			handle = handle.semantic(loc, sc, context);
		}
		if (members == null) // if forward reference
		{
			return;
		}
		if (symtab != null) // if already done
		{
			if (scope == null) {
				return;
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
		
	    if ((sc.stc & STC.STCdeprecated) != 0) {
			isdeprecated = true;
		}

		// Expand any tuples in baseclasses[]
		for (i = 0; i < baseclasses.size();) {
			BaseClass b = baseclasses.get(0);
			b.type = b.type.semantic(loc, sc, context);
			Type tb = b.type.toBasetype(context);

			if (tb.ty == Ttuple) {
				TypeTuple tup = (TypeTuple) tb;
				PROT protection = b.protection;
				baseclasses.remove(i);
				int dim = Argument.dim(tup.arguments, context);
				for (int j = 0; j < dim; j++) {
					Argument arg = Argument.getNth(tup.arguments, j, context);
					b = new BaseClass(arg.type, protection);
					baseclasses.set(i + j, b);
				}
			} else {
				i++;
			}
		}

		// Check for errors, handle forward references
		for (i = 0; i < baseclasses.size();) {
			TypeClass tc;
			BaseClass b;
			Type tb;

			b = baseclasses.get(i);
			b.type = b.type.semantic(loc, sc, context);
			tb = b.type.toBasetype(context);
			if (tb.ty == Tclass) {
				tc = (TypeClass) tb;
			} else {
				tc = null;
			}
			if (tc == null || tc.sym.isInterfaceDeclaration() == null) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.BaseTypeMustBeInterface, b.sourceType));
				baseclasses.remove(i);
				continue;
			} else {
				tc.sym.consumeRest();
				
				// Check for duplicate interfaces
				for (int j = 0; j < i; j++) {
					BaseClass b2 = baseclasses.get(j);
					if (b2.base == tc.sym) {
						context.acceptProblem(Problem.newSemanticTypeErrorLoc(
								IProblem.InterfaceInheritsFromDuplicateInterface, b2, new String[] { toChars(context), b2.base.toChars(context) }));
					}
				}

				b.base = tc.sym;
				if (b.base == this || isBaseOf2(b.base)) {
					context.acceptProblem(Problem.newSemanticTypeErrorLoc(IProblem.CircularInheritanceOfInterface, b));
					baseclasses.remove(i);
					continue;
				}
				if (b.base.symtab == null || b.base.scope != null) {
					// Forward reference of base, try again later
					scope = scx != null ? scx : new Scope(sc, context);
					scope.setNoFree();
					scope.module.addDeferredSemantic(this, context);
					return;
				}
			}
			i++;
		}

		interfaces = new BaseClasses(baseclasses);

		interfaceSemantic(sc, context);

		if (vtblOffset() != 0) {
			vtbl.add(this); // leave room at vtbl[0] for classinfo
		}

		// Cat together the vtbl[]'s from base interfaces
		Lcontinue: for (i = 0; i < interfaces.size(); i++) {
			BaseClass b = interfaces.get(i);

			// Skip if b has already appeared
			for (int k = 0; k < i; k++) {
				if (b == interfaces.get(i)) {
					// goto Lcontinue;
					continue Lcontinue;
				}
			}

			// Copy vtbl[] from base class
			if (b.base.vtblOffset() != 0) {
				int d = b.base.vtbl.size();
				if (d > 1) {
					for (int j = 1; j < d; j++) {
						vtbl.add(b.base.vtbl.get(j));
					}
				}
			} else {
				vtbl.add(b.base.vtbl);
			}

			// Lcontinue: ;
		}

		for (Dsymbol s : members) {
			s.addMember(sc, this, 1, context);
		}

		sc = sc.push(this);
		sc.parent = this;
		if (isCOMinterface()) {
			sc.linkage = LINKwindows;
		}
		sc.structalign = 8;
		structalign = sc.structalign;
		sc.offset = 8;

		for (int j = 0; j < size(members); j++) {
			members.get(j).semantic(sc, context);
		}
		sc.pop();
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s, SemanticContext context) {
		consumeRestStructure();
		
		InterfaceDeclaration id;

		if (s != null) {
			id = (InterfaceDeclaration) s;
		} else {
			id = new InterfaceDeclaration(loc, ident, null);
		}

		super.syntaxCopy(id, context);
		
		id.copySourceRange(this);
		id.javaElement = javaElement;
		id.templated = templated;
		
		return id;
	}

	@Override
	public int vtblOffset() {
		if (isCOMinterface()) {
			return 0;
		}
		return 1;
	}
	
	@Override
	public boolean isCOMinterface() {
		return com;
	}
	
	public char getSignaturePrefix() {
		if (templated) {
			return ISignatureConstants.TEMPLATED_INTERFACE;
		} else {
			return ISignatureConstants.INTERFACE;
		}
	}

}
