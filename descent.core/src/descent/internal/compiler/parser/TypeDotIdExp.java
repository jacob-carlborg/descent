package descent.internal.compiler.parser;

public class TypeDotIdExp extends Expression {
	
	public IdentifierExp ident;

	public TypeDotIdExp(Type type, IdentifierExp ident) {
		super(TOK.TOKtypedot);
		this.type = type;
		this.ident = ident;
	}
	
	@Override
	public int kind() {
		return TYPE_DOT_ID_EXP;
	}

}
