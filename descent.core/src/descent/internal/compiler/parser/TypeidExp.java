package descent.internal.compiler.parser;

public class TypeidExp extends Expression {

	public TypeidExp(Type type) {
		super(TOK.TOKtypeid);
		this.type = type;
	}
	
	@Override
	public int kind() {
		return TYPEID_EXP;
	}

}
