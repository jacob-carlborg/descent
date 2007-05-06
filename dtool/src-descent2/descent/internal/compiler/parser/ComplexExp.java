package descent.internal.compiler.parser;

import java.math.BigInteger;

public class ComplexExp extends Expression {

	private Complex value;

	public ComplexExp(Loc loc, Complex value) {
		super(loc, TOK.TOKcomplex80);
		this.value = value;
	}

	@Override
	public Expression castTo(Scope sc, Type t, SemanticContext context) {
		if (type.iscomplex() && t.iscomplex()) {
			type = t;
		} else {
			return super.castTo(sc, t, context);
		}
		return this;
	}

	@Override
	public BigInteger toInteger(SemanticContext context) {
		return toReal(context).toBigInteger();
	}

	@Override
	public BigInteger toUInteger(SemanticContext context) {
		// TODO toBigUInteger ?
		return toReal(context).toBigInteger();
	}

	@Override
	public Real toReal(SemanticContext context) {
		return value.r;
	}

	@Override
	public Real toImaginary(SemanticContext context) {
		return value.i;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		if (type == null) {
			type = Type.tcomplex80;
		} else {
			type = type.semantic(loc, sc, context);
		}
		return this;
	}

	@Override
	public boolean isBool(boolean result) {
		// TODO check this
		if (result) {
			return !value.r.toBigInteger().equals(BigInteger.ZERO);
		} else {
			return value.r.toBigInteger().equals(BigInteger.ZERO);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o instanceof Expression) {
			if (((Expression) o).op == TOK.TOKcomplex80) {
				ComplexExp ne = (ComplexExp) o;
				return type.equals(ne.type) && value.equals(ne.value);
			}
		}

		return false;
	}

	@Override
	public int getNodeType() {
		return COMPLEX_EXP;
	}

}