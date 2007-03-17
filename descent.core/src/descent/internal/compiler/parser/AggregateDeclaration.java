package descent.internal.compiler.parser;

public abstract class AggregateDeclaration extends ScopeDsymbol {
	
	public Type type;
	public int storage_class;
	public boolean isdeprecated;
	
	public AggregateDeclaration(IdentifierExp id) {
		super(id);
	}
	
	@Override
	public Type getType() {
		return type;
	}
	
	@Override
	public AggregateDeclaration isAggregateDeclaration() {
		return this;
	}
	
	@Override
	public boolean isDeprecated() {
		return isdeprecated;
	}

}
