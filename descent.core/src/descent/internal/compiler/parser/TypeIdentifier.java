package descent.internal.compiler.parser;

public class TypeIdentifier extends TypeQualified {

	public IdentifierExp ident;

	public TypeIdentifier(IdentifierExp ident) {
		super(TY.Tident);
		this.ident = ident;
	}
	
	@Override
	public Expression toExpression() {
		Expression e = new IdentifierExp(ident.ident);
		e.setSourceRange(ident.start, ident.length);
		if (idents != null) {
			for(IdentifierExp id : idents) {
				e = new DotIdExp(e, id);
				e.setSourceRange(ident.start, id.start + id.length - ident.start);
			}
		}
		return e;
	}
	
	@Override
	public int kind() {
		return TYPE_IDENTIFIER;
	}

}
