package descent.internal.compiler.parser;

public class AssertExp extends UnaExp {

	public Expression msg;

	public AssertExp(Loc loc, Expression e) {
		this(loc, e, null);
	}

	public AssertExp(Loc loc, Expression e, Expression msg) {
		super(loc, TOK.TOKassert, e);
		this.msg = msg;
	}

	@Override
	public Expression syntaxCopy() {
		AssertExp ae = new AssertExp(loc, e1.syntaxCopy(), msg != null ? msg
				.syntaxCopy() : null);
		return ae;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		super.semantic(sc, context);
		e1 = resolveProperties(sc, e1, context);
		// BUG: see if we can do compile time elimination of the Assert
		e1 = e1.optimize(WANTvalue);
		e1 = e1.checkToBoolean(context);
		if (msg != null) {
			msg = msg.semantic(sc, context);
			msg = resolveProperties(sc, msg, context);
			msg = msg.implicitCastTo(sc, Type.tchar.arrayOf(context), context);
			msg = msg.optimize(WANTvalue);
		}
		if (e1.isBool(false)) {
			FuncDeclaration fd = sc.parent.isFuncDeclaration();
			fd.hasReturnExp |= 4;

			if (!context.global.params.useAssert) {
				Expression e = new HaltExp(loc);
				e = e.semantic(sc, context);
				return e;
			}
		}
		type = Type.tvoid;
		return this;
	}

	@Override
	public int checkSideEffect(int flag, SemanticContext context) {
		return 1;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("assert(");
		expToCBuffer(buf, hgs, e1, PREC.PREC_assign, context);
		if (msg != null) {
			buf.writeByte(',');
			expToCBuffer(buf, hgs, msg, PREC.PREC_assign, context);
		}
		buf.writeByte(')');
	}

	@Override
	public int getNodeType() {
		return ASSERT_EXP;
	}

}
