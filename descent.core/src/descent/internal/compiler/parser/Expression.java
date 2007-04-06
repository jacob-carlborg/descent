package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.MATCH.MATCHconvert;
import static descent.internal.compiler.parser.MATCH.MATCHnomatch;
import static descent.internal.compiler.parser.TOK.TOKint64;
import static descent.internal.compiler.parser.TY.Tbit;
import static descent.internal.compiler.parser.TY.Tbool;
import static descent.internal.compiler.parser.TY.Tpointer;
import static descent.internal.compiler.parser.TY.Treference;
import static descent.internal.compiler.parser.TY.Tsarray;
import static descent.internal.compiler.parser.TY.Tvoid;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import descent.core.compiler.IProblem;

public abstract class Expression extends ASTNode implements Cloneable {

	public static List<Expression> arraySyntaxCopy(List<Expression> exps) {
		List<Expression> a = null;

		if (exps != null) {
			a = new ArrayList<Expression>(exps.size());
			for (Expression e : exps) {
				e = e.syntaxCopy();
				a.add(e);
			}
		}
		return a;
	}

	public TOK op;
	public Type type;

	public Expression(TOK op) {
		this.op = op;
		this.type = null;
	}

	public Expression addressOf(Scope sc, SemanticContext context) {
		Expression e;

		e = toLvalue(sc, null, context);
		e = new AddrExp(e);
		e.type = type.pointerTo(context);
		return e;
	}

	public Expression castTo(Scope sc, Type t, SemanticContext context) {
		Expression e;
		Type tb;

		e = this;
		tb = t.toBasetype(context);
		type = type.toBasetype(context);
		if (tb != type) {
			if (tb.ty == Tbit && isBit()) {
				;
			}

			// Do (type *) cast of (type [dim])
			else if (tb.ty == Tpointer && type.ty == Tsarray) {

				if (type.size() == 0) {
					e = new NullExp();
				} else {
					e = new AddrExp(e);
				}
			} else {
				e = new CastExp(e, tb);
			}
		}
		e.type = t;
		return e;
	}

	public void checkArithmetic(SemanticContext context) {
		if (!type.isintegral() && !type.isfloating()) {
			error("'%s' is not an arithmetic type", toChars());
		}
	}

	public void checkDeprecated(Scope sc, Dsymbol s, SemanticContext context) {
		s.checkDeprecated(sc, context);
	}

	public void checkEscape(SemanticContext context) {
	}

	public Expression checkIntegral(SemanticContext context) {
		if (!type.isintegral()) {
			error("'%s' is not of integral type, it is a %s", toChars(), type
					.toChars());
			return new IntegerExp(0);
		}
		return this;
	}

	public void checkNoBool(SemanticContext context) {
		if (type.toBasetype(context).ty == Tbool) {
			error("operation not allowed on bool '%s'", toChars());
		}
	}

	public void checkScalar(SemanticContext context) {
		if (!type.isscalar()) {
			error("'%s' is not a scalar, it is a %s", toChars(), type.toChars());
		}
	}

	public int checkSideEffect(int flag, SemanticContext context) {
		if (flag == 0) {
			/* TODO semantic
			error("%s has no effect in expression (%s)", op.toString(),
					toChars());
					*/
		}
		return 0;
	}

	public Expression checkToBoolean(SemanticContext context) {
		if (type.checkBoolean(context)) {
			error("expression %s of type %s does not have a boolean value",
					toChars(), type.toChars());
		}
		return this;
	}

	public Expression checkToPointer(SemanticContext context) {
		Expression e;
		Type tb;

		e = this;

		// If C static array, convert to pointer
		tb = type.toBasetype(context);
		if (tb.ty == Tsarray) {
			TypeSArray ts = (TypeSArray) tb;
			if (ts.size() == 0) {
				e = new NullExp();
			} else {
				e = new AddrExp(this);
			}
			e.type = tb.next.pointerTo(context);
		}
		return e;
	}

	public Expression combine(Expression e1, Expression e2) {
		if (e1 != null) {
			if (e2 != null) {
				e1 = new CommaExp(e1, e2);
				e1.type = e2.type;
			}
		} else {
			e1 = e2;
		}
		return e1;
	}

	public Expression copy() {
		try {
			return (Expression) clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public Expression deref() {
		if (type.ty == Treference) {
			Expression e;

			e = new PtrExp(this);
			e.type = type.next;
			return e;
		}
		return this;
	}

	@Override
	public DYNCAST dyncast() {
		return DYNCAST.DYNCAST_EXPRESSION;
	}

	public Expression implicitCastTo(Scope sc, Type t, SemanticContext context) {
		if (implicitConvTo(t, context) != MATCHnomatch) {
			if (context.global.params.warnings
					&& Type.impcnvWarn[type.toBasetype(context).ty.ordinal()][t
							.toBasetype(context).ty.ordinal()]
					&& op != TOKint64) {
				Expression e = optimize(WANTflags | WANTvalue);

				if (e.op == TOKint64) {
					return e.implicitCastTo(sc, t, context);
				}

				/*
				 * TODO semantic fprintf(stdmsg, "warning - ");
				 */
				error(
						"implicit conversion of expression (%s) of type %s to %s can cause loss of data",
						toChars(), type.toChars(), t.toChars());
			}
			return castTo(sc, t, context);
		}

		Expression e = optimize(WANTflags | WANTvalue);
		if (e != this) {
			return e.implicitCastTo(sc, t, context);
		}

		if (t.deco == null) { /*
		 * Can happen with: enum E { One } class A {
		 * static void fork(EDG dg) { dg(E.One); } alias
		 * void delegate(E) EDG; } Should eventually
		 * make it work.
		 */
			error("forward reference to type %s", t.toChars());
		} else if (t.reliesOnTident() != null) {
			error("forward reference to type %s", t.reliesOnTident().toChars());
		}

		context.acceptProblem(Problem.newSemanticTypeError(
				"Type mismatch: cannot implicitly convert from " + type
						+ " to " + t, IProblem.CannotImplicitlyConvert, 0,
				start, length));

		return castTo(sc, t, context);
	}

	public MATCH implicitConvTo(Type t, SemanticContext context) {
		if (type == null) {
			error("%s is not an expression", toChars());
			type = Type.terror;
		}
		if (t.ty == Tbit && isBit()) {
			return MATCHconvert;
		}
		Expression e = optimize(WANTvalue | WANTflags);
		if (e != this) {
			return e.implicitConvTo(t, context);
		}
		MATCH match = type.implicitConvTo(t, context);
		if (match != MATCHnomatch) {
			return match;
		}
		return MATCHnomatch;
	}

	public Expression integralPromotions(Scope sc, SemanticContext context) {
		Expression e;

		e = this;
		switch (type.toBasetype(context).ty) {
		case Tvoid:
			error("void has no value");
			break;

		case Tint8:
		case Tuns8:
		case Tint16:
		case Tuns16:
		case Tbit:
		case Tbool:
		case Tchar:
		case Twchar:
			e = e.castTo(sc, Type.tint32, context);
			break;

		case Tdchar:
			e = e.castTo(sc, Type.tuns32, context);
			break;
		}
		return e;
	}

	public boolean isBit() {
		return false;
	}

	public boolean isBool(boolean result) {
		return false;
	}

	public Expression modifiableLvalue(Scope sc, Expression e,
			SemanticContext context) {
		// See if this expression is a modifiable lvalue (i.e. not const)
		return toLvalue(sc, e, context);
	}

	public Expression optimize(int result) {
		return this;
	}

	public void rvalue(SemanticContext context) {
		if (type != null && type.toBasetype(context).ty == Tvoid) {
			error("expression %s is void and has no value", toChars());
		}
	}

	public Expression semantic(Scope sc, SemanticContext context) {
		if (type != null) {
			type = type.semantic(sc, context);
		} else {
			type = Type.tvoid;
		}
		return this;
	}

	public Expression syntaxCopy() {
		return copy();
	}

	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		buf.writestring(op.toString());
	}

	@Override
	public String toChars() {
		OutBuffer buf;
		HdrGenState hgs = new HdrGenState();

		buf = new OutBuffer();
		// TODO check this null
		toCBuffer(buf, hgs, null);
		return buf.toChars();
	}

	public Complex toComplex(SemanticContext context) {
		error("Floating point constant expression expected instead of %s",
				toChars());
		return Complex.ZERO;
	}

	public Expression toDelegate(Scope sc, Type tret) {
		// TODO semantic
		return null;
	}

	public Real toImaginary(SemanticContext context) {
		error("Floating point constant expression expected instead of %s",
				toChars());
		return Real.ZERO;
	}

	public BigInteger toInteger(SemanticContext context) {
		context.acceptProblem(Problem.newSemanticTypeError(
				"Integer constant expression expected",
				IProblem.IntegerConstantExpressionExpected, 0, start, length));
		return BigInteger.ZERO;
	}

	public Expression toLvalue(Scope sc, Expression e, SemanticContext context) {
		if (e == null) {
			e = this;
		}
		/* TODO semantic
		 else if (!loc.filename) {
		 loc = e.loc;
		 }
		 */
		error("%s is not an lvalue", e.toChars());
		return this;
	}

	public void toMangleBuffer(OutBuffer buf, SemanticContext context) {
		error("expression %s is not a valid template value argument", toChars());
	}

	public Real toReal(SemanticContext context) {
		error("Floating point constant expression expected instead of %s",
				toChars());
		return Real.ZERO;
	}

	public BigInteger toUInteger(SemanticContext context) {
		return toInteger(context);
	}

}
