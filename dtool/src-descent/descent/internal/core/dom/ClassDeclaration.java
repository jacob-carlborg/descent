package descent.internal.core.dom;

import java.util.List;

import descent.core.domX.IASTVisitor;

public class ClassDeclaration extends AggregateDeclaration {
	
	public ClassDeclaration(Identifier id, List<BaseClass> baseClasses) {
		super(id, baseClasses);
	}
	
	public int getAggregateDeclarationType() {
		return CLASS_DECLARATION;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptCommonChildren(visitor);
		}
		visitor.endVisit(this);
	}

}
