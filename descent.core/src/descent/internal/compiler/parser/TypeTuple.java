package descent.internal.compiler.parser;

import melnorme.miscutil.Assert;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.STC.STCin;

public class TypeTuple extends Type {

	public Arguments arguments;

	private TypeTuple() {
		super(TY.Ttuple, null);
	}

	public void accept0(IASTVisitor visitor) {
		Assert.fail("Accept0 on fake class");
	}

	public static TypeTuple newArguments(Arguments arguments) {
		TypeTuple tt = new TypeTuple();
		tt.arguments = arguments;
		return tt;
	}

	public static TypeTuple newExpressions(Expressions exps,
			SemanticContext context) {
		TypeTuple tt = new TypeTuple();
		Arguments arguments = new Arguments();
		if (exps != null) {
			arguments.ensureCapacity(exps.size());
			for (int i = 0; i < exps.size(); i++) {
				Expression e = exps.get(i);
				if (e.type.ty == TY.Ttuple) {
					e.error("cannot form tuple of tuples");
				}
				Argument arg = new Argument(STCin, e.type, null, null);
				arguments.set(i, arg);
			}
		}
		tt.arguments = arguments;
		return tt;
	}

	@Override
	public int getNodeType() {
		return TYPE_TUPLE;
	}

	@Override
	public void toCBuffer2(OutBuffer buf, IdentifierExp ident, HdrGenState hgs,
			SemanticContext context) {
		OutBuffer buf2 = new OutBuffer();
		argsToCBuffer(buf2, hgs, arguments, 0, context);
		buf.prependstring(buf2.toChars());
		if (ident != null) {
			buf.writeByte(' ');
			buf.writestring(ident.toChars());
		}
	}

}
