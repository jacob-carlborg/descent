package descent.internal.compiler.parser;

public class UnionDeclaration extends AggregateDeclaration {

	public UnionDeclaration(IdentifierExp id) {
		super(id);
	}
	
	@Override
	public int getNodeType() {
		return UNION_DECLARATION;
	}

}
