package descent.internal.compiler.parser;

import java.util.ArrayList;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.PROT.PROTnone;

import static descent.internal.compiler.parser.STC.STCin;

// DMD 1.020
public class StructDeclaration extends AggregateDeclaration implements IStructDeclaration {

	public boolean zeroInit; // !=0 if initialize with 0 fill

	public StructDeclaration(Loc loc, IdentifierExp id) {
		super(loc, id);
		this.type = new TypeStruct(this);
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, modifiers);
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, members);
			
			acceptSynthetic(visitor);
		}
		visitor.endVisit(this);
	}

	@Override
	public PROT getAccess(IDsymbol smember) {
		PROT access_ret = PROTnone;

		if (smember.toParent() == this) {
			access_ret = smember.prot();
		} else if (smember.isDeclaration().isStatic()) {
			access_ret = smember.prot();
		}
		return access_ret;
	}

	@Override
	public int getNodeType() {
		return STRUCT_DECLARATION;
	}

	@Override
	public StructDeclaration isStructDeclaration() {
		return this;
	}

	@Override
	public String kind() {
		return "struct";
	}

	@Override
	public String mangle(SemanticContext context) {
		return Dsymbol_mangle(context);
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
				context.acceptProblem(Problem.newSemanticTypeErrorLoc(
						IProblem.UnionsCannotBeAbstract, this));
			} else {
				context.acceptProblem(Problem.newSemanticTypeErrorLoc(
						IProblem.StructsCannotBeAbstract, this));
			}
		}

		if (sizeok == 0) { // if not already done the addMember step
			for (IDsymbol s : members) {
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
			IDsymbol s = members.get(i);
			s.semantic(sc2, context);
			if (isUnionDeclaration() != null) {
				sc2.offset = 0;
			}
		}

		/* The TypeInfo_Struct is expecting an opEquals and opCmp with
		 * a parameter that is a pointer to the struct. But if there
		 * isn't one, but is an opEquals or opCmp with a value, write
		 * another that is a shell around the value:
		 *	int opCmp(struct *p) { return opCmp(*p); }
		 */

		TypeFunction tfeqptr;
		{
			Arguments arguments = new Arguments();
			Argument arg = new Argument(STCin, handle, new IdentifierExp(loc,
					Id.p), null);

			arguments.add(arg);
			tfeqptr = new TypeFunction(arguments, Type.tint32, 0, LINK.LINKd);
			tfeqptr = (TypeFunction) tfeqptr.semantic(loc, sc, context);
		}

		TypeFunction tfeq;
		{
			Arguments arguments = new Arguments();
			Argument arg = new Argument(STCin, type, null, null);

			arguments.add(arg);
			tfeq = new TypeFunction(arguments, Type.tint32, 0, LINK.LINKd);
			tfeq = (TypeFunction) tfeq.semantic(loc, sc, context);
		}

		char[] id = Id.eq;
		for (int j = 0; j < 2; j++) {
			IDsymbol s = ASTDmdNode.search_function(this, id, context);
			IFuncDeclaration fdx = s != null ? s.isFuncDeclaration() : null;
			if (fdx != null) {
				IFuncDeclaration fd = fdx.overloadExactMatch(tfeqptr, context);
				if (fd == null) {
					fd = fdx.overloadExactMatch(tfeq, context);
					if (fd != null) { // Create the thunk, fdptr
						FuncDeclaration fdptr = new FuncDeclaration(loc, 
								fdx.ident(), STC.STCundefined, tfeqptr);
						Expression e = new IdentifierExp(loc, Id.p);
						e = new PtrExp(loc, e);
						Expressions args = new Expressions();
						args.add(e);
						e = new IdentifierExp(loc, id);
						e = new CallExp(loc, e, args);
						fdptr.fbody = new ReturnStatement(loc, e);
						IScopeDsymbol s2 = fdx.parent().isScopeDsymbol();
						Assert.isNotNull(s2);
						s2.members().add(fdptr);
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
			fields = new ArrayList<IVarDeclaration>(0);
			structsize = 0;
			alignsize = 0;
			structalign = 0;

			scope = scx != null ? scx : new Scope(sc, context);
			scope.setNoFree();
			scope.module.addDeferredSemantic(this, context);
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

		context.Module_dprogress++;

		// Determine if struct is all zeros or not
		zeroInit = true;
		for (int j = 0; j < fields.size(); j++) {
			IDsymbol s = fields.get(j);
			IVarDeclaration vd = s.isVarDeclaration();
			if (vd != null && !vd.isDataseg(context)) {
				if (vd.init() != null) {
					// Should examine init to see if it is really all 0's
					zeroInit = true;
					break;
				} else {
					if (!vd.type().isZeroInit(context)) {
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
	public IDsymbol syntaxCopy(IDsymbol s, SemanticContext context) {
		StructDeclaration sd;

		if (s != null) {
			sd = (StructDeclaration) s;
		} else {
			sd = new StructDeclaration(loc, ident);
		}
		super.syntaxCopy(sd, context);
		return sd;
	}
	
	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		int i;

		buf.writestring(kind());
		if (!isAnonymous()) {
			buf.writestring(toChars(context));
		}
		if (null == members) {
			buf.writeByte(';');
			buf.writenl();
			return;
		}
		buf.writenl();
		buf.writeByte('{');
		buf.writenl();
		for (i = 0; i < members.size(); i++) {
			IDsymbol s = members.get(i);

			buf.writestring("    ");
			s.toCBuffer(buf, hgs, context);
		}
		buf.writeByte('}');
		buf.writenl();
	}
	
	@Override
	public int getErrorStart() {
		if (ident != null) {
			return ident.start;
		}
		return start;
	}
	
	@Override
	public int getErrorLength() {
		if (ident != null) {
			return ident.length;
		}
		return 6; // "struct".length()
	}

	public String getSignature() {
		return type.getSignature();
	}

}
