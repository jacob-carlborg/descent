package descent.internal.compiler.parser;

public abstract class TypeArray extends Type {

	public TypeArray(TY ty, Type next) {
		super(ty, next);
	}

	@Override
	public void toCBuffer2(OutBuffer buf, IdentifierExp ident, HdrGenState hgs,
			SemanticContext context) {
		OutBuffer buf2 = new OutBuffer();
		// TODO semantic
		// toPrettyBracket(buf2, hgs);
		buf.prependstring(buf2.toChars());
		if (ident != null) {
			buf.writeByte(' ');
			buf.writestring(ident.toChars(context));
		}
		next.toCBuffer2(buf, null, hgs, context);
	}

}
