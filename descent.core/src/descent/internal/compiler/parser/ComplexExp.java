package descent.internal.compiler.parser;

import java.math.BigInteger;

import descent.internal.compiler.parser.ast.IASTVisitor;

public class ComplexExp extends Expression {

	public complex_t value;
	public Type type;

	public ComplexExp(Loc loc, complex_t value, Type type) {
		super(loc, TOK.TOKcomplex80);
		this.value = value;
		this.type = type;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
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
	public integer_t toInteger(SemanticContext context) {
		return toReal(context).to_integer_t();
	}

	@Override
	public integer_t toUInteger(SemanticContext context) {
		// TODO toBigUInteger ?
		return toReal(context).to_integer_t();
	}

	@Override
	public real_t toReal(SemanticContext context) {
		return value.r;
	}

	@Override
	public real_t toImaginary(SemanticContext context) {
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
			return !value.r.to_integer_t().equals(BigInteger.ZERO);
		} else {
			return value.r.to_integer_t().equals(BigInteger.ZERO);
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
	
	@Override
	public boolean isConst() {
		return true;
	}
	
	@Override
	public String toChars(SemanticContext context) {
		// TODO semantic toChars
		return super.toChars(context);
	}
	
	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		// TODO semantic
		super.toCBuffer(buf, hgs, context);
	}

}
