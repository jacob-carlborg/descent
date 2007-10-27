package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import descent.core.compiler.IProblem;
import static descent.internal.compiler.parser.LINK.LINKd;

import static descent.internal.compiler.parser.MATCH.MATCHconvert;
import static descent.internal.compiler.parser.MATCH.MATCHnomatch;

import static descent.internal.compiler.parser.TOK.TOKdelegate;
import static descent.internal.compiler.parser.TOK.TOKimport;
import static descent.internal.compiler.parser.TOK.TOKint64;

import static descent.internal.compiler.parser.TY.Tbit;
import static descent.internal.compiler.parser.TY.Tbool;
import static descent.internal.compiler.parser.TY.Tpointer;
import static descent.internal.compiler.parser.TY.Treference;
import static descent.internal.compiler.parser.TY.Tsarray;
import static descent.internal.compiler.parser.TY.Tvoid;

// DMD 1.020
public abstract class Expression extends ASTDmdNode implements Cloneable {
	
	public class Parenthesis {
		public int startPosition;
		public int length;
		public Parenthesis(int startPosition, int length) {
			this.startPosition = startPosition;
			this.length = length;
		}
	}
	
	public static Expressions arraySyntaxCopy(Expressions exps) {
		Expressions a = null;

		if (exps != null) {
			a = new Expressions();
			a.setDim(exps.size());
			for(int i = 0; i < exps.size(); i++) {
				Expression e = exps.get(i);
				e = e.syntaxCopy();
				a.set(i, e);
			}
		}
		return a;
	}

	public static Expression build_overload(Loc loc, Scope sc,
			Expression ethis, Expression earg, IdentifierExp id,
			SemanticContext context) {
		Expression e;

		e = new DotIdExp(loc, ethis, id);

		if (earg != null) {
			e = new CallExp(loc, e, earg);
		} else {
			e = new CallExp(loc, e);
		}

		e = e.semantic(sc, context);
		return e;
	}
	
	public static Expression build_overload(Loc loc, Scope sc,
			Expression ethis, Expression earg, char[] id,
			SemanticContext context) {
		return build_overload(loc, sc, ethis, earg, new IdentifierExp(id), context);
	}

	public static Expression combine(Expression e1, Expression e2) {
		if (e1 != null) {
			if (e2 != null) {
				e1 = new CommaExp(e1.loc, e1, e2);
				e1.type = e2.type;
			}
		} else {
			e1 = e2;
		}
		return e1;
	}
	public Loc loc;
	public TOK op;
	public Type type, sourceType;
	public List<Parenthesis> parenthesis;

	public Expression(Loc loc, TOK op) {
		this.loc = loc;
		this.op = op;
		this.type = null;
	}
	
	public void addParenthesis(int startPosition, int length) {
		if (parenthesis == null) {
			parenthesis = new ArrayList<Parenthesis>();
		}
		parenthesis.add(new Parenthesis(startPosition, length));
	}

	public Expression addressOf(Scope sc, SemanticContext context) {
		Expression e;

		e = toLvalue(sc, null, context);
		e = new AddrExp(loc, e);
		e.type = type.pointerTo(context);
		return e;
	}

	public Expression castTo(Scope sc, Type t, SemanticContext context) {
		Expression e;
		Type tb;

		e = this;
		tb = t.toBasetype(context);
		type = type.toBasetype(context);
		if (!same(tb, type)) {
			if (tb.ty == Tbit && isBit()) {
				;
			}

			// Do (type *) cast of (type [dim])
			else if (tb.ty == Tpointer && type.ty == Tsarray) {

				if (type.size(loc, context) == 0) {
					e = new NullExp(loc);
				} else {
					e = new AddrExp(loc, e);
				}
			} else {
				e = new CastExp(loc, e, tb);
			}
		}
		e.type = t;
		e.copySourceRange(this);
		return e;
	}

	public void checkArithmetic(SemanticContext context) {
		if (!type.isintegral() && !type.isfloating()) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.SymbolIsNotAnArithmeticType, this, new String[] { toChars(context) }));
		}
	}

	public void checkDeprecated(Scope sc, Dsymbol s, SemanticContext context) {
		s.checkDeprecated(sc, context);
	}

	public void checkEscape(SemanticContext context) {
		// empty
	}

	public Expression checkIntegral(SemanticContext context) {
		if (!type.isintegral()) {
			context.acceptProblem(Problem.newSemanticTypeError(IProblem.SymbolIsNotOfIntegralType, this, new String[] { toChars(context),
					type.toChars(context) }));
			return new IntegerExp(loc, 0);
		}
		return this;
	}

	public void checkNoBool(SemanticContext context) {
		if (type.toBasetype(context).ty == Tbool) {
			context.acceptProblem(Problem.newSemanticTypeError(IProblem.OperationNotAllowedOnBool, this, new String[] { toChars(context) }));
		}
	}

	public void checkScalar(SemanticContext context) {
		if (!type.isscalar(context)) {
			context.acceptProblem(Problem.newSemanticTypeError(IProblem.SymbolIsNotAScalar, this, new String[] { toChars(context), type
					.toChars(context) }));
		}
	}

	public int checkSideEffect(int flag, SemanticContext context) {
		if (flag == 0) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.ExpressionHasNoEffect, this));
		}
		return 0;
	}

	public Expression checkToBoolean(SemanticContext context) {
		if (!type.checkBoolean(context)) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.ExpressionOfTypeDoesNotHaveABooleanValue, this, new String[] { toChars(context), type.toChars(context) }));
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
			if (ts.size(loc, context) == 0) {
				e = new NullExp(loc);
			} else {
				e = new AddrExp(loc, this);
			}
			e.type = tb.next.pointerTo(context);
		}
		return e;
	}

	public boolean compare(Expression exp) {
		// TODO semantic
		return false;
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

			e = new PtrExp(loc, this);
			e.type = type.next;
			return e;
		}
		return this;
	}

	public Expression doInline(InlineDoState ids) {
		return copy();
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
				Expression e = optimize(WANTflags | WANTvalue, context);

				if (e.op == TOKint64) {
					return e.implicitCastTo(sc, t, context);
				}

				context.acceptProblem(Problem.newSemanticTypeWarning(
						IProblem.ImplicitConversionCanCauseLossOfData, 0, start,
						length, new String[] { toChars(context), type.toChars(context), t.toChars(context) }));
			}
			return castTo(sc, t, context);
		}

		Expression e = optimize(WANTflags | WANTvalue, context);
		if (e != this) {
			return e.implicitCastTo(sc, t, context);
		}

		if (t.deco == null) { /*
		 * Can happen with: enum E { One } class A {
		 * static void fork(EDG dg) { dg(E.One); } alias
		 * void delegate(E) EDG; } Should eventually
		 * make it work.
		 */
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.ForwardReferenceToType, this, new String[] { t.toChars(context) }));
		} else if (t.reliesOnTident() != null) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.ForwardReferenceToType, this, new String[] { t.reliesOnTident().toChars(context) }));
		}

		context.acceptProblem(Problem.newSemanticTypeError(
				IProblem.CannotImplicitlyConvert, this,
				new String[] { toChars(context), type.toChars(context), t.toChars(context) }));

		return castTo(sc, t, context);
	}

	public MATCH implicitConvTo(Type t, SemanticContext context) {
		if (type == null) {
			context.acceptProblem(Problem.newSemanticTypeWarning(IProblem.SymbolNotAnExpression, 0, start, length, new String[] { toChars(context) }));
			type = Type.terror;
		}
		if (t.ty == Tbit && isBit()) {
			return MATCHconvert;
		}
		Expression e = optimize(WANTvalue | WANTflags, context);
		if (e != this) {
			return e.implicitConvTo(t, context);
		}
		MATCH match = type.implicitConvTo(t, context);
		if (match != MATCHnomatch) {
			return match;
		}
		return MATCHnomatch;
	}

	public int inlineCost(InlineCostState ics, SemanticContext context) {
		return 1;
	}

	public Expression inlineScan(InlineScanState iss, SemanticContext context) {
		return this;
	}

	public Expression integralPromotions(Scope sc, SemanticContext context) {
		Expression e;

		e = this;
		switch (type.toBasetype(context).ty) {
		case Tvoid:
			context.acceptProblem(Problem.newSemanticTypeError(IProblem.SymbolHasNoValue, this, new String[] { "void" }));
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

	public Expression interpret(InterState istate, SemanticContext context) {
		return EXP_CANT_INTERPRET;
	}

	public boolean isBit() {
		return false;
	}

	public boolean isBool(boolean result) {
		return false;
	}

	public boolean isCommutative() {
		return false; // default is no reverse
	}

	public boolean isConst() {
		return false;
	}

	public Expression modifiableLvalue(Scope sc, Expression e,
			SemanticContext context) {
		// See if this expression is a modifiable lvalue (i.e. not const)
		return toLvalue(sc, e, context);
	}

	public char[] opId() {
		throw new IllegalStateException("assert(0);");
	}

	public char[] opId_r() {
		return null;
	}

	public Expression optimize(int result, SemanticContext context) {
		return this;
	}

	public void rvalue(SemanticContext context) {
		if (type != null && type.toBasetype(context).ty == Tvoid) {
			context.acceptProblem(Problem.newSemanticTypeError(IProblem.ExpressionIsVoidAndHasNoValue, this, new String[] { toChars(context) }));
		}
	}

	public void scanForNestedRef(Scope sc, SemanticContext context) {
		// empty
	}

	public Expression semantic(Scope sc, SemanticContext context) {
		if (type != null) {
			type = type.semantic(loc, sc, context);
		} else {
			type = Type.tvoid;
		}
		return this;
	}

	public Expression syntaxCopy() {
		return copy();
	}

	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring(op.toString());
	}

	@Override
	public String toChars(SemanticContext context) {
		OutBuffer buf = new OutBuffer();
		HdrGenState hgs = new HdrGenState();
		toCBuffer(buf, hgs, context);
		return buf.toChars();
	}

	public complex_t toComplex(SemanticContext context) {
		context.acceptProblem(Problem.newSemanticTypeError(
				IProblem.FloatingPointConstantExpressionExpected, this, new String[] { toChars(context) }));
		return complex_t.ZERO;
	}

	public Expression toDelegate(Scope sc, Type t, SemanticContext context) {
		TypeFunction tf = new TypeFunction(null, t, 0, LINKd);
		FuncLiteralDeclaration fld = new FuncLiteralDeclaration(loc, tf,
				TOKdelegate, null);
		Expression e;
		sc = sc.push();
		sc.parent = fld; // set current function to be the delegate
		e = this;
		e.scanForNestedRef(sc, context);
		sc = sc.pop();
		Statement s = new ReturnStatement(loc, e);
		fld.fbody = s;
		e = new FuncExp(loc, fld);
		e = e.semantic(sc, context);
		return e;
	}

	public real_t toImaginary(SemanticContext context) {
		context.acceptProblem(Problem.newSemanticTypeError(
				IProblem.FloatingPointConstantExpressionExpected, this, new String[] { toChars(context) }));
		return real_t.ZERO;
	}

	public integer_t toInteger(SemanticContext context) {
		context.acceptProblem(Problem.newSemanticTypeError(
				IProblem.IntegerConstantExpressionExpected, this));
		return integer_t.ZERO;
	}

	public Expression toLvalue(Scope sc, Expression e, SemanticContext context) {
		if (e == null) {
			e = this;
		} else if (loc.filename == null) {
			loc = e.loc;
		}
		context.acceptProblem(Problem.newSemanticTypeError(IProblem.NotAnLvalue, e, new String[] { e.toChars(context) }));
		return this;
	}

	public void toMangleBuffer(OutBuffer buf, SemanticContext context) {
		context.acceptProblem(Problem.newSemanticTypeError(
				IProblem.ExpressionIsNotAValidTemplateValueArgument, this, new String[] { toChars(context) }));
	}

	public real_t toReal(SemanticContext context) {
		context.acceptProblem(Problem.newSemanticTypeError(
				IProblem.FloatingPointConstantExpressionExpected, this, new String[] { toChars(context) }));
		return real_t.ZERO;
	}

	public integer_t toUInteger(SemanticContext context) {
		return toInteger(context).castToUns64();
	}
	
	@Override
	public int getLineNumber() {
		return loc.linnum;
	}

}
