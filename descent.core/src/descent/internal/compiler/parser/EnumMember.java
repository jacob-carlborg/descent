package descent.internal.compiler.parser;

public class EnumMember extends Dsymbol {

	public Expression value;

	public EnumMember(IdentifierExp id, Expression value) {
		super(id);
		this.value = value;
	}
	
	@Override
	public int kind() {
		return ENUM_MEMBER;
	}

}
