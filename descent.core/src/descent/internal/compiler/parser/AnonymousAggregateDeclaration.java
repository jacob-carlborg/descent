package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class AnonymousAggregateDeclaration extends AggregateDeclaration {

	public AnonymousAggregateDeclaration(Loc loc) {
		super(loc, null);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	@Override
	public AnonymousAggregateDeclaration isAnonymousAggregateDeclaration() {
		return this;
	}
	
	public char getSignaturePrefix() {
		// TODO Descent signature
		return 0;
	}

}
