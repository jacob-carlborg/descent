package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;

public class RealExp extends Expression {

	public char[] str;
	public Real value;

	public RealExp(Loc loc, char[] str, Real value, Type type) {
		super(loc, TOK.TOKfloat64);
		this.str = str;
		this.value = value;
		this.type = type;
	}
	
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}


	@Override
	public Expression castTo(Scope sc, Type t, SemanticContext context) {
		if (type.isreal() && t.isreal()) {
			type = t;
		} else if (type.isimaginary() && t.isimaginary()) {
			type = t;
		} else {
			return super.castTo(sc, t, context);
		}
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o instanceof Expression) {
			if (((Expression) o).op == TOK.TOKfloat64) {
				RealExp ne = (RealExp) o;
				return type.equals(ne.type) && value.equals(ne.value);
			}
		}

		return false;
	}

	@Override
	public int getNodeType() {
		return REAL_EXP;
	}

	@Override
	public boolean isBool(boolean result) {
		return result ? !value.equals(Real.ZERO) : value.equals(Real.ZERO);
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		if (type == null) {
			type = Type.tfloat64;
		} else {
			type = type.semantic(loc, sc, context);
		}
		return this;
	}

	@Override
	public String toChars() {
		return new String(str).intern();
	}

	@Override
	public Complex toComplex(SemanticContext context) {
		return new Complex(toReal(context), toImaginary(context));
	}

	@Override
	public Real toImaginary(SemanticContext context) {
		return type.isreal() ? Real.ZERO : value;
	}

	@Override
	public IntegerWrapper toInteger(SemanticContext context) {
		return value.toIntegerWrapper();
	}

	@Override
	public Real toReal(SemanticContext context) {
		return type.isreal() ? value : Real.ZERO;
	}

	@Override
	public IntegerWrapper toUInteger(SemanticContext context) {
		return BigIntegerUtils.castToUns64(value.toIntegerWrapper());
	}
	
	@Override
	public char[] toCharArray() {
		return str;
	}

}
