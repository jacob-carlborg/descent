package descent.internal.compiler.parser;

import java.math.BigInteger;

import descent.core.compiler.IProblem;

public abstract class Expression extends ASTNode implements Cloneable {
	
	public final static int WANTflags = 1;
	public final static int WANTvalue = 2;
	public final static int WANTinterpret = 4;
	
	public TOK op;
	public Type type;
	
	public Expression(TOK op) {
		this.op = op;
	}
	
	public Expression semantic(Scope sc, SemanticContext context) {
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
	
	public BigInteger toInteger(SemanticContext context) {
		context.acceptProblem(Problem
				.newSemanticTypeError(
						"Integer constant expression expected",
						IProblem.EnumValueOverflow, 0,
						start, length));
		return BigInteger.ZERO;
	}

}
