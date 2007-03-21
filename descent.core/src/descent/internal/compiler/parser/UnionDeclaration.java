package descent.internal.compiler.parser;

public class UnionDeclaration extends StructDeclaration {

	public UnionDeclaration(IdentifierExp id) {
		super(id);
	}
	
	@Override
	public int getNodeType() {
		return UNION_DECLARATION;
	}

}
