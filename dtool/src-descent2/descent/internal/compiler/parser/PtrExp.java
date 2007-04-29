package descent.internal.compiler.parser;

public class PtrExp extends UnaExp {

	public PtrExp(Loc loc, Expression e) {
		super(loc, TOK.TOKstar, e);
	}

	public PtrExp(Loc loc, Expression e, Type t) {
		super(loc, TOK.TOKstar, e);
		this.type = t;
	}

	@Override
	public int getNodeType() {
		return PTR_EXP;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		Type tb;

		super.semantic(sc, context);
		e1 = resolveProperties(sc, e1, context);
		if (type != null) {
			return this;
		}
		if (e1.type == null) {
			// printf("PtrExp.semantic('%s')\n", toChars());
		}
		tb = e1.type.toBasetype(context);
		switch (tb.ty) {
		case Tpointer:
			type = tb.next;
			if (type.isbit()) {
				Expression e;

				// Rewrite *p as p[0]
				e = new IndexExp(loc, e1, new IntegerExp(loc, 0));
				return e.semantic(sc, context);
			}
			break;

		case Tsarray:
		case Tarray:
			type = tb.next;
			e1 = e1.castTo(sc, type.pointerTo(context), context);
			break;

		default:
			error("can only * a pointer, not a '%s'", e1.type.toChars());
			type = Type.tint32;
			break;
		}
		rvalue(context);
		return this;
	}
	
	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		buf.writeByte('*');
	    expToCBuffer(buf, hgs, e1, op.precedence, context);
	}
	
	@Override
	public Expression toLvalue(Scope sc, Expression e, SemanticContext context) {
		return this;
	}

}
