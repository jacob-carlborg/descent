package descent.internal.compiler.parser;

public class TypeExp extends Expression {

	public TypeExp(Type type) {
		super(TOK.TOKtype);
		this.type = type;
	}
	
	@Override
	public int kind() {
		return TYPE_EXP;
	}

}
