package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.PREC.*;
import static descent.internal.compiler.parser.TOK.*;

// DMD 1.020
public class ArrayLengthExp extends UnaExp {

	public ArrayLengthExp(Loc loc, Expression e1) {
		super(loc, TOK.TOKarraylength, e1);
	}

	@Override
	public int getNodeType() {
		return ARRAY_LENGTH_EXP;
	}

	@Override
	protected void accept0(IASTVisitor visitor) {
		melnorme.miscutil.Assert.fail("accept0 on a fake Node");
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		if (type == null) {
			super.semantic(sc, context);
			e1 = resolveProperties(sc, e1, context);

			type = Type.tsize_t;
		}
		return this;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		expToCBuffer(buf, hgs, e1, PREC_primary, context);
		buf.writestring(".length");
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		Expression e;
		Expression e1;

		e1 = this.e1.interpret(istate, context);
		if (e1 == EXP_CANT_INTERPRET)
			return EXP_CANT_INTERPRET;
		if (e1.op == TOKstring || e1.op == TOKarrayliteral
				|| e1.op == TOKassocarrayliteral) {
			e = ArrayLength(type, e1);
		} else
			return EXP_CANT_INTERPRET;
		return e;
	}

	@Override
	public Expression optimize(int result, SemanticContext context) {
		Expression e;

		e1 = e1.optimize(WANTvalue | (result & WANTinterpret), context);
		e = this;
		if (e1.op == TOKstring || e1.op == TOKarrayliteral
				|| e1.op == TOKassocarrayliteral) {
			e = ArrayLength(type, e1);
		}
		return e;
	}

}
