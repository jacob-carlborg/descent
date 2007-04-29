package descent.internal.core.dom;

import descent.core.domX.IASTVisitor;


public class UnionDeclaration extends AggregateDeclaration {

	public UnionDeclaration(Identifier id) {
		super(id, null);
	}
	
	public int getAggregateDeclarationType() {
		return UNION_DECLARATION;
	}

	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptCommonChildren(visitor);
		}
		visitor.endVisit(this);
	}
}
