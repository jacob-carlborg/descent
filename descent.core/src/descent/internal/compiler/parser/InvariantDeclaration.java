package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class InvariantDeclaration extends FuncDeclaration implements IInvariantDeclaration {

	public int invariantStart;

	public InvariantDeclaration(Loc loc) {
		super(loc, new IdentifierExp(Loc.ZERO, Id.classInvariant),
				STC.STCundefined, null);
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceFbody);
		}
		visitor.endVisit(this);
	}

	@Override
	public boolean addPostInvariant(SemanticContext context) {
		return false;
	}

	@Override
	public boolean addPreInvariant(SemanticContext context) {
		return false;
	}

	@Override
	public int getNodeType() {
		return INVARIANT_DECLARATION;
	}

	@Override
	public InvariantDeclaration isInvariantDeclaration() {
		return this;
	}

	@Override
	public boolean isVirtual(SemanticContext context) {
		return false;
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		IAggregateDeclaration ad;

		parent = sc.parent;
		IDsymbol parent = toParent();
		ad = parent.isAggregateDeclaration();
		if (ad == null) {
			context.acceptProblem(Problem.newSemanticTypeErrorLoc(
					IProblem.InvariantsOnlyForClassStructUnion, this));
			return;
		} else if (ad.inv() != null && ad.inv() != this) {
			context.acceptProblem(Problem.newSemanticTypeErrorLoc(
					IProblem.MoreThanOneInvariant, this,
					new String[] { new String(ad.ident().ident) }));
		}
		ad.inv(this);
		type = new TypeFunction(null, Type.tvoid, 0, LINK.LINKd);

		sc = sc.push();
		sc.stc &= ~STC.STCstatic; // not a static invariant
		sc.incontract++;
		sc.linkage = LINK.LINKd;

		super.semantic(sc, context);

		sc.pop();
	}

	@Override
	public Dsymbol syntaxCopy(IDsymbol s, SemanticContext context) {
		InvariantDeclaration id;

		if (s != null) {
			throw new IllegalStateException("assert(!s);");
		}
		id = new InvariantDeclaration(loc);
		super.syntaxCopy(id, context);
		return id;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		if (hgs.hdrgen) {
			return;
		}
		buf.writestring("invariant");
		bodyToCBuffer(buf, hgs, context);
	}
	
	@Override
	public int getErrorStart() {
		return invariantStart;
	}
	
	@Override
	public int getErrorLength() {
		return 9; // "invariant".length()
	}

}
