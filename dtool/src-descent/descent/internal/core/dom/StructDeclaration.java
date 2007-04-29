package descent.internal.core.dom;

import descent.core.domX.IASTVisitor;



public class StructDeclaration extends AggregateDeclaration {

	public StructDeclaration(Identifier id) {
		super(id, null);
		
		type = new TypeStruct(this);
	}

	public int getAggregateDeclarationType() {
		return STRUCT_DECLARATION;
	}

	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptCommonChildren(visitor);
		}
		visitor.endVisit(this);
	}
}
