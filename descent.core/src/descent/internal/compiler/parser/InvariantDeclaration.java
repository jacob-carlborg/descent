package descent.internal.compiler.parser;

import descent.core.compiler.IProblem;

public class InvariantDeclaration extends FuncDeclaration {
	
	public InvariantDeclaration() {
		super(new IdentifierExp(Id.classInvariant), STC.STCundefined, null);
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
			context.acceptProblem(Problem.newSemanticTypeError("Invariants only are for struct/union/class definitions", IProblem.InvariantsOnlyForClassStructUnion, 0, start, "invariant".length()));
			return;
		} else if (ad.inv != null && ad.inv != this) {
			context.acceptProblem(Problem.newSemanticTypeError("More than one invariant for " + ad.ident, IProblem.MoreThanOneInvariant, 0, start, "invariant".length()));
		}
		ad.inv = this;
		type = new TypeFunction(null, Type.tvoid, false, LINK.LINKd);

		sc = sc.push();
		sc.stc &= ~STC.STCstatic; // not a static invariant
		sc.incontract++;
		sc.linkage = LINK.LINKd;

		super.semantic(sc, context);

		sc.pop();
	}
	
	@Override
	public int getNodeType() {
		return INVARIANT_DECLARATION;
	}

}
