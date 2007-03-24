package descent.internal.compiler.parser;

import java.math.BigInteger;

import descent.core.compiler.IProblem;
import static descent.internal.compiler.parser.TY.*;

public abstract class Expression extends ASTNode implements Cloneable {
	
	public TOK op;
	public Type type;
	
	public Expression(TOK op) {
		this.op = op;
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
	
	public void checkDeprecated(Scope sc, Dsymbol s, SemanticContext context) {
		s.checkDeprecated(sc, context);
	}
	
	public Expression semantic(Scope sc, SemanticContext context) {
		if (type != null) {
			type = type.semantic(sc, context);
		} else {
			type = Type.tvoid;
		}
		return this;
	}
	
	public Expression optimize(int result) {
		return this;
	}
	
	public Expression copy() {
		try {
			return (Expression) clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Expression implicitCastTo(Scope sc, Type t) {
		return this;
	}
	
	public boolean isBool(boolean result) {
		return false;
	}
	
	public BigInteger toInteger(SemanticContext context) {
		context.acceptProblem(Problem
				.newSemanticTypeError(
						"Integer constant expression expected",
						IProblem.EnumValueOverflow, 0,
						start, length));
		return BigInteger.ZERO;
	}

	public boolean implicitConvTo(Type t, SemanticContext context) {
		return false;
	}

	public void checkEscape() {
	}
	
	public boolean isBit() {
		return false;
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

}
