package descent.internal.compiler.parser;

import java.util.List;

public class NewAnonClassExp extends Expression {

	public Expression thisexp;
	public List<Expression> newargs;
	public ClassDeclaration cd;
	public List<Expression> arguments;

	public NewAnonClassExp(Expression thisexp, List<Expression> newargs,
			ClassDeclaration cd, List<Expression> arguments) {
		super(TOK.TOKnewanonclass);
		this.thisexp = thisexp;
		this.newargs = newargs;
		this.cd = cd;
		this.arguments = arguments;
	}

	@Override
	public int checkSideEffect(int flag, SemanticContext context) {
		return 1;
	}

	@Override
	public int getNodeType() {
		return NEW_ANON_CLASS_EXP;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		Expression d = new DeclarationExp(cd);
		d = d.semantic(sc, context);

		Expression n = new NewExp(thisexp, newargs, cd.type, arguments);

		Expression c = new CommaExp(d, n);
		return c.semantic(sc, context);
	}

	@Override
	public Expression syntaxCopy() {
		return new NewAnonClassExp(thisexp != null ? thisexp.syntaxCopy()
				: null, arraySyntaxCopy(newargs), (ClassDeclaration) cd
				.syntaxCopy(null), arraySyntaxCopy(arguments));
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		if (thisexp != null) {
			expToCBuffer(buf, hgs, thisexp, PREC.PREC_primary, context);
			buf.writeByte('.');
		}
		buf.writestring("new");
		if (newargs != null && newargs.size() > 0) {
			buf.writeByte('(');
			argsToCBuffer(buf, newargs, hgs, context);
			buf.writeByte(')');
		}
		buf.writestring(" class ");
		if (arguments != null && arguments.size() > 0) {
			buf.writeByte('(');
			argsToCBuffer(buf, arguments, hgs, context);
			buf.writeByte(')');
		}
		if (cd != null) {
			cd.toCBuffer(buf, hgs, context);
		}
	}

}
