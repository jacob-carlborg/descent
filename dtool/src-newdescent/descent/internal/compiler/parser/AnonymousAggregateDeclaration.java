package descent.internal.compiler.parser;

public class AnonymousAggregateDeclaration extends AggregateDeclaration {

	public AnonymousAggregateDeclaration(Loc loc) {
		super(loc, null);
	}
	
	@Override
	public AnonymousAggregateDeclaration isAnonymousAggregateDeclaration() {
		return this;
	}

}
