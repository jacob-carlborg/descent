package descent.internal.core.dom;

import java.util.List;

import descent.core.domX.IASTVisitor;

public class InterfaceDeclaration extends AggregateDeclaration {

	public InterfaceDeclaration(Identifier id, List<BaseClass> baseclasses) {
		super(id, baseclasses);
	}

	public int getAggregateDeclarationType() {
		return INTERFACE_DECLARATION;
	}

	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptCommonChildren(visitor);
		}
		visitor.endVisit(this);
	}
}
