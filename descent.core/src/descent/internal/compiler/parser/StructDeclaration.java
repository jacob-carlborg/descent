package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.PROT.PROTnone;

import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class StructDeclaration extends AggregateDeclaration {
	
	public boolean zeroInit;		// !=0 if initialize with 0 fill

	public StructDeclaration(Loc loc, IdentifierExp id) {
		super(loc, id);
		this.type = new TypeStruct(this);
	}
	
	@Override
	public StructDeclaration isStructDeclaration() {
		return this;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, modifiers);
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, members);
		}
		visitor.endVisit(this);
	}
	
	public PROT getAccess(Dsymbol smember) {
		PROT access_ret = PROTnone;

		if (smember.toParent() == this) {
			access_ret = smember.prot();
		} else if (smember.isDeclaration().isStatic()) {
			access_ret = smember.prot();
		}
		return access_ret;
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
	    Scope sc2;

		Assert.isNotNull(type);
		if (members == null) { // if forward reference
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

		parent = sc.parent;
		handle = type.pointerTo(context);
		structalign = sc.structalign;
		protection = sc.protection;
		assert (!isAnonymous());
		if ((sc.stc & STC.STCabstract) != 0) {
			if (isUnionDeclaration() != null) {
				context.acceptProblem(Problem.newSemanticTypeError("Unions cannot be abstract", IProblem.IllegalModifier, 0, ident.start, ident.length));
			} else {
				context.acceptProblem(Problem.newSemanticTypeError("Structs cannot be abstract", IProblem.IllegalModifier, 0, ident.start, ident.length));
			}
		}

		if (sizeok == 0) { // if not already done the addMember step
			for (Dsymbol s : members) {
				s.addMember(sc, this, 1, context);
			}
		}

		sizeok = 0;
		sc2 = sc.push(this);
		sc2.stc = 0;
		sc2.parent = this;
		if (isUnionDeclaration() != null) {
			sc2.inunion = true;
		}
		sc2.protection = PROT.PROTpublic;
		sc2.explicitProtection = 0;

		int members_dim = members.size();
		for (int i = 0; i < members_dim; i++) {
			Dsymbol s = (Dsymbol) members.get(i);
			s.semantic(sc2, context);
			if (isUnionDeclaration() != null)
				sc2.offset = 0;
		}

		/* The TypeInfo_Struct is expecting an opEquals and opCmp with
		 * a parameter that is a pointer to the struct. But if there
		 * isn't one, but is an opEquals or opCmp with a value, write
		 * another that is a shell around the value:
		 *	int opCmp(struct *p) { return opCmp(*p); }
		 */

		TypeFunction tfeqptr;
		{
			List<Argument> arguments = new ArrayList<Argument>();
			Argument arg = new Argument(InOut.In, handle, new IdentifierExp(loc, 
					Id.p), null);

			arguments.add(arg);
			tfeqptr = new TypeFunction(arguments, Type.tint32, 0,
					LINK.LINKd);
			tfeqptr = (TypeFunction) tfeqptr.semantic(loc, sc, context);
		}

		TypeFunction tfeq;
		{
			List<Argument> arguments = new ArrayList<Argument>();
			Argument arg = new Argument(InOut.In, type, null, null);

			arguments.add(arg);
			tfeq = new TypeFunction(arguments, Type.tint32, 0, LINK.LINKd);
			tfeq = (TypeFunction) tfeq.semantic(loc, sc, context);
		}

		char[] id = Id.eq;
		for (int j = 0; j < 2; j++) {
			Dsymbol s = Expression.search_function(this, id, context);
			FuncDeclaration fdx = s != null ? s.isFuncDeclaration() : null;
			if (fdx != null) {
				FuncDeclaration fd = fdx.overloadExactMatch(tfeqptr, context);
				if (fd == null) {
					fd = fdx.overloadExactMatch(tfeq, context);
					if (fd != null) { // Create the thunk, fdptr
						FuncDeclaration fdptr = new FuncDeclaration(loc, fdx.ident,
								STC.STCundefined, tfeqptr);
						Expression e = new IdentifierExp(loc, Id.p);
						e = new PtrExp(loc, e);
						List<Expression> args = new ArrayList<Expression>();
						args.add(e);
						e = new IdentifierExp(loc, id);
						e = new CallExp(loc, e, args);
						fdptr.fbody = new ReturnStatement(loc, e);
						ScopeDsymbol s2 = fdx.parent.isScopeDsymbol();
						Assert.isNotNull(s2);
						s2.members.add(fdptr);
						fdptr.addMember(sc, s2, 1, context);
						fdptr.semantic(sc2, context);
					}
				}
			}

			id = Id.cmp;
		}

		sc2.pop();

		if (sizeok == 2) { // semantic() failed because of forward references.
			// Unwind what we did, and defer it for later
			fields = new ArrayList<VarDeclaration>(0);
			structsize = 0;
			alignsize = 0;
			structalign = 0;

			scope = scx != null ? scx : new Scope(sc);
			scope.setNoFree();
			scope.module.addDeferredSemantic(this);
			return;
		}

		// 0 sized struct's are set to 1 byte
		if (structsize == 0) {
			structsize = 1;
			alignsize = 1;
		}

		// Round struct size up to next alignsize boundary.
		// This will ensure that arrays of structs will get their internals
		// aligned properly.
		structsize = (structsize + alignsize - 1) & ~(alignsize - 1);

		sizeok = 1;

		/* TODO semantic
		 Module::dprogress++;
		 */

		// Determine if struct is all zeros or not
		zeroInit = true;
		for (int j = 0; j < fields.size(); j++) {
			Dsymbol s = (Dsymbol) fields.get(j);
			VarDeclaration vd = s.isVarDeclaration();
			if (vd != null && !vd.isDataseg(context)) {
				if (vd.init != null) {
					// Should examine init to see if it is really all 0's
					zeroInit = true;
					break;
				} else {
					if (!vd.type.isZeroInit()) {
						zeroInit = false;
						break;
					}
				}
			}
		}

		/* Look for special member functions.
		 */
		inv = (InvariantDeclaration) search(loc, Id.classInvariant, 0, context);
		aggNew = (NewDeclaration) search(loc, Id.classNew, 0, context);
		aggDelete = (DeleteDeclaration) search(loc, Id.classDelete, 0, context);

		if (sc.func != null) {
			semantic2(sc, context);
			semantic3(sc, context);
		}
	}
	
	@Override
	public int getNodeType() {
		return STRUCT_DECLARATION;
	}

}
