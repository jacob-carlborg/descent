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
	
	public void checkDeprecated(Scope sc, Dsymbol s, SemanticContext context) {
		s.checkDeprecated(sc, context);
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
	
	public static Expression resolveProperties(Scope sc, Expression e,
			SemanticContext context) {
		if (e.type != null) {
			Type t = e.type.toBasetype(context);

			if (t.ty == TY.Tfunction) {
				e = new CallExp(e);
				e = e.semantic(sc, context);
			}

			/*
			 * Look for e being a lazy parameter; rewrite as delegate call
			 */
			else if (e.op == TOK.TOKvar) {
				VarExp ve = (VarExp) e;

				if ((ve.var.storage_class & STC.STClazy) != 0) {
					e = new CallExp(e);
					e = e.semantic(sc, context);
				}
			}

			else if (e.op == TOK.TOKdotexp) {
				e.error("expression has no value");
			}
		}
		return e;
	}
	
	public static Dsymbol search_function(AggregateDeclaration ad, Identifier funcid, SemanticContext context) {
		Dsymbol s;
		FuncDeclaration fd;
		TemplateDeclaration td;

		s = ad.search(funcid, 0, context);
		if (s != null) {
			Dsymbol s2;

			s2 = s.toAlias(context);
			fd = s2.isFuncDeclaration();
			if (fd != null && fd.type.ty == TY.Tfunction) {
				return fd;
			}

			td = s2.isTemplateDeclaration();
			if (td != null) {
				return td;
			}
		}
		return null;
	}

}
