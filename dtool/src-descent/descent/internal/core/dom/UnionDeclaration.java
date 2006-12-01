package descent.internal.core.dom;


public class UnionDeclaration extends AggregateDeclaration {

	public UnionDeclaration(Identifier id) {
		super(id, null);
	}
	
	public int getAggregateDeclarationType() {
		return UNION_DECLARATION;
	}

}
