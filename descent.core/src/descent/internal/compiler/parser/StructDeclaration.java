package descent.internal.compiler.parser;

public class StructDeclaration extends AggregateDeclaration {
	
	public boolean zeroInit;		// !=0 if initialize with 0 fill

	public StructDeclaration(IdentifierExp id) {
		super(id);
		this.type = new TypeStruct(this);
	}
	
	@Override
	public int kind() {
		return STRUCT_DECLARATION;
	}

}
