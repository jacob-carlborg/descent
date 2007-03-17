package descent.internal.compiler.parser;

public abstract class AggregateDeclaration extends ScopeDsymbol {
	
	public Type type;
	public int storage_class;
	
	public AggregateDeclaration(IdentifierExp id) {
		super(id);
	}
	
	@Override
	public AggregateDeclaration isAggregateDeclaration() {
		return this;
	}

}
