package descent.internal.compiler.parser;

public class StructDeclaration extends AggregateDeclaration {

	public StructDeclaration(IdentifierExp id) {
		super(id);
	}
	
	@Override
	public int kind() {
		return STRUCT_DECLARATION;
	}

}
