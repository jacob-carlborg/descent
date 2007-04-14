package descent.internal.compiler.parser;

public class UnionDeclaration extends StructDeclaration {

	public UnionDeclaration(Loc loc, IdentifierExp id) {
		super(loc, id);
	}
	
	@Override
	public int getNodeType() {
		return UNION_DECLARATION;
	}

}
