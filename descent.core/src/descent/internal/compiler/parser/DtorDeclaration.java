package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class DtorDeclaration extends FuncDeclaration implements IDtorDeclaration {

	public int thisStart;

	public DtorDeclaration(Loc loc) {
		super(loc, new IdentifierExp(Loc.ZERO, Id.dtor), STC.STCundefined, null);
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, modifiers);
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, sourceFrequire);
			TreeVisitor.acceptChildren(visitor, sourceFbody);
			TreeVisitor.acceptChildren(visitor, outId);
			TreeVisitor.acceptChildren(visitor, sourceFensure);
		}
		visitor.endVisit(this);
	}

	@Override
	public boolean addPostInvariant(SemanticContext context) {
		return false;
	}

	@Override
	public boolean addPreInvariant(SemanticContext context) {
		return (vthis != null && context.global.params.useInvariants);
	}

	@Override
	public int getNodeType() {
		return DTOR_DECLARATION;
	}

	@Override
	public DtorDeclaration isDtorDeclaration() {
		return this;
	}

	@Override
	public boolean isVirtual(SemanticContext context) {
		if (context.BREAKABI) {
			return false;
		} else {
			return super.isVirtual(context);
		}
	}

	@Override
	public boolean overloadInsert(Dsymbol s, SemanticContext context) {
		return false; // cannot overload destructors
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		IClassDeclaration cd;

		parent = sc.parent;
		IDsymbol parent = toParent();
		cd = parent.isClassDeclaration();
		if (cd == null) {
			context.acceptProblem(Problem.newSemanticTypeErrorLoc(
					IProblem.DestructorsOnlyForClass, this));
		} else {
			if (cd.dtors() == null) {
				cd.dtors(new FuncDeclarations());
			}
			cd.dtors().add(this);
		}
		type = new TypeFunction(null, Type.tvoid, 0, LINK.LINKd);

		sc = sc.push();
		sc.stc &= ~STC.STCstatic; // not a static destructor
		sc.linkage = LINK.LINKd;

		super.semantic(sc, context);

		sc.pop();
	}

	@Override
	public IDsymbol syntaxCopy(IDsymbol s, SemanticContext context) {
		if (s != null) {
			throw new IllegalStateException("assert(!s);");
		}

		DtorDeclaration dd = new DtorDeclaration(loc);
		return super.syntaxCopy(dd, context);
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		if (hgs.hdrgen) {
			return;
		}
		buf.writestring("~this()");
		bodyToCBuffer(buf, hgs, context);
	}
	
	@Override
	public int getErrorStart() {
		return thisStart;
	}
	
	@Override
	public int getErrorLength() {
		return 4; // "this".length()
	}

}
