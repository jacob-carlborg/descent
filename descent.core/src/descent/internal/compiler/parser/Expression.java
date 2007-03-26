package descent.internal.compiler.parser;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import descent.core.compiler.IProblem;
import static descent.internal.compiler.parser.TY.*;
import static descent.internal.compiler.parser.TOK.*;
import static descent.internal.compiler.parser.MATCH.*;

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

	public void checkDeprecated(Scope sc, Dsymbol s, SemanticContext context) {
		s.checkDeprecated(sc, context);
	}

	public void checkEscape() {
	}

	public int checkSideEffect(int flags) {
		// TODO semantic
		return 1;
	}

	public Expression checkToBoolean(SemanticContext context) {
		if (type.checkBoolean(context)) {
			error("expression %s of type %s does not have a boolean value",
					toChars(), type.toChars());
		}
		return this;
	}

	public Expression copy() {
		try {
			return (Expression) clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
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

	public boolean isBit() {
		return false;
	}

	public boolean isBool(boolean result) {
		return false;
	}

	public Expression optimize(int result) {
		return this;
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
		// TODO semantic
		return null;
	}

	public void toCBuffer(OutBuffer buf, HdrGenState hgs) {
		// TOOD semantic
	}

	public BigInteger toInteger(SemanticContext context) {
		context.acceptProblem(Problem.newSemanticTypeError(
				"Integer constant expression expected",
				IProblem.EnumValueOverflow, 0, start, length));
		return BigInteger.ZERO;
	}

}
