package descent.internal.compiler.parser;

public abstract class AggregateDeclaration extends ScopeDsymbol {
	
	public AggregateDeclaration(IdentifierExp id) {
		super(id);
	}
	
	@Override
	public AggregateDeclaration isAggregateDeclaration() {
		return this;
	}

}
