package descent.internal.compiler.parser;

import java.math.BigDecimal;

import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class RealExp extends Expression {

	public char[] str;
	public real_t value;

	public RealExp(Loc loc, char[] str, real_t value, Type type) {
		super(loc, TOK.TOKfloat64);
		this.str = str;
		if (value == null) {
			throw new IllegalStateException("assert(value)");
		}
		this.value = value;
		this.type = type;
	}
	
	public RealExp(Loc loc, real_t value, Type type) {
		this(loc, null, value, type);
	}
	
	public RealExp(Loc loc, double d, Type type) {
		this(loc, new real_t(BigDecimal.valueOf(d)), type);
	}

	@Override
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
		return result ? !value.equals(real_t.ZERO) : value.equals(real_t.ZERO);
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
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		// TODO semantic toCBuffer
		buf.data.append(value);
	}

	@Override
	public String toChars(SemanticContext context) {
		// TODO semantic toChars
		return value.toString();
	}

	@Override
	public complex_t toComplex(SemanticContext context) {
		return new complex_t(toReal(context), toImaginary(context));
	}

	@Override
	public real_t toImaginary(SemanticContext context) {
		return type.isreal() ? real_t.ZERO : value;
	}

	@Override
	public integer_t toInteger(SemanticContext context) {
		// TODO the correct implementation is in the next line:
		// return (sinteger_t) toReal();
		return value.to_integer_t();
	}

	@Override
	public real_t toReal(SemanticContext context) {
		return type.isreal() ? value : real_t.ZERO;
	}

	@Override
	public integer_t toUInteger(SemanticContext context) {
		return value.to_integer_t().castToUns64();
	}
	
	@Override
	public char[] toCharArray() {
		return str;
	}
	
	@Override
	public boolean isConst() {
		return true;
	}
	
	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		return this;
	}

}
