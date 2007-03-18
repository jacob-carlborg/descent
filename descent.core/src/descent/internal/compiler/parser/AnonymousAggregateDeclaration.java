package descent.internal.compiler.parser;

public class AnonymousAggregateDeclaration extends AggregateDeclaration {

	public AnonymousAggregateDeclaration() {
		super(null);
	}
	
	@Override
	public AnonymousAggregateDeclaration isAnonymousAggregateDeclaration() {
		return this;
	}

}
