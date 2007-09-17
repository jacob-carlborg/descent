package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class InvariantDeclaration extends FuncDeclaration {
	
	public InvariantDeclaration(Loc loc) {
		super(loc, new IdentifierExp(Loc.ZERO, Id.classInvariant), STC.STCundefined, null);
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceFbody);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public InvariantDeclaration isInvariantDeclaration() {
		return this;
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		AggregateDeclaration ad;

		parent = sc.parent;
		Dsymbol parent = toParent();
		ad = parent.isAggregateDeclaration();
		if (ad == null) {
			// TODO semantic point out the "invariant" token
			context.acceptProblem(Problem.newSemanticTypeError(IProblem.InvariantsOnlyForClassStructUnion, 0, start, "invariant".length()));
			return;
		} else if (ad.inv != null && ad.inv != this) {
			context.acceptProblem(Problem.newSemanticTypeError(IProblem.MoreThanOneInvariant, 0, start, "invariant".length(), new String[] { new String(ad.ident.ident) }));
		}
		ad.inv = this;
		type = new TypeFunction(null, Type.tvoid, 0, LINK.LINKd);

		sc = sc.push();
		sc.stc &= ~STC.STCstatic; // not a static invariant
		sc.incontract++;
		sc.linkage = LINK.LINKd;

		super.semantic(sc, context);

		sc.pop();
	}
	
	@Override
	public boolean isVirtual(SemanticContext context) {
		return false;
	}
	
	@Override
	public boolean overloadInsert(Dsymbol s, SemanticContext context) {
		// TODO semantic this isn't in DMD but it makes sense
		return true;
	}
	
	@Override
	public boolean addPreInvariant(SemanticContext context) {
		return false;
	}
	
	@Override
	public boolean addPostInvariant(SemanticContext context) {
		return false;
	}
	
	@Override
	public int getNodeType() {
		return INVARIANT_DECLARATION;
	}

}
