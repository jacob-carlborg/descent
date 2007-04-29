package descent.internal.compiler.parser;

public class EnumMember extends Dsymbol {

	public Expression value;

	public EnumMember(Loc loc, IdentifierExp id, Expression value) {
		super(loc, id);
		this.value = value;
	}

	@Override
	public int getNodeType() {
		return ENUM_MEMBER;
	}

	@Override
	public EnumMember isEnumMember() {
		return this;
	}

	@Override
	public String kind() {
		return "enum member";
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		Expression e = null;
		if (value != null) {
			e = value.syntaxCopy();
		}

		EnumMember em;
		if (s != null) {
			em = (EnumMember) s;
			em.value = e;
		} else {
			em = new EnumMember(loc, ident, e);
		}
		return em;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		buf.writestring(ident.toChars());
		if (value != null) {
			buf.writestring(" = ");
			value.toCBuffer(buf, hgs, context);
		}
	}

}
