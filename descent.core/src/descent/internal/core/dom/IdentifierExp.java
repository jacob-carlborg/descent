package descent.internal.core.dom;

public class IdentifierExp extends Expression {
	
	public Identifier id;

	public IdentifierExp(Loc loc, Identifier id) {
		this.id = id;
		this.start = id.start;
		this.length = id.length;
	}
	
	public int getElementType() {
		return IDENTIFIER_EXPRESSION;
	}
	
	@Override
	public String toString() {
		return id.string;
	}

}
