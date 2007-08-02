package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.TY.*;
import static descent.internal.compiler.parser.TOK.*;

public class CastExp extends UnaExp {

	public Type to;
	public TOK tok;
	public int modifierStart;

	public CastExp(Loc loc, Expression e1, Type t) {
		super(loc, TOK.TOKcast, e1);
		this.to = t;
		this.tok = null;
	}
	
	public CastExp(Loc loc, Expression e1, TOK tok, int modifierStart) {
		super(loc, TOK.TOKcast, e1);
		this.modifierStart = modifierStart;
		this.to = null;
		this.tok = tok;
	}

	@Override
	public void checkEscape(SemanticContext context) {
		Type tb = type.toBasetype(context);
		if (tb.ty == Tarray && e1.op == TOKvar
				&& e1.type.toBasetype(context).ty == Tsarray) {
			VarExp ve = (VarExp) e1;
			VarDeclaration v = ve.var.isVarDeclaration();
			if (v != null) {
				if (!v.isDataseg(context)) {
					error("escaping reference to local %s", v.toChars());
				}
			}
		}
	}

	@Override
	public int checkSideEffect(int flag, SemanticContext context) {
		/* if not:
		 *  cast(void)
		 *  cast(classtype)func()
		 */
		if (!to.equals(Type.tvoid)
				&& !(to.ty == Tclass && e1.op == TOKcall && e1.type.ty == Tclass)) {
			return super.checkSideEffect(flag, context);
		}
		return 1;
	}

	@Override
	public int getNodeType() {
		return CAST_EXP;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		Expression e;
		//BinExp b;
		//UnaExp u;

		if (type != null) {
			return this;
		}
		super.semantic(sc, context);
		if (e1.type != null) // if not a tuple
		{
			e1 = resolveProperties(sc, e1, context);
			to = to.semantic(loc, sc, context);

			e = op_overload(sc);
			if (e != null) {
				return e.implicitCastTo(sc, to, context);
			}

			Type tob = to.toBasetype(context);
			if (tob.ty == Tstruct && !tob.equals(e1.type.toBasetype(context))) {
				/* Look to replace:
				 *	cast(S)t
				 * with:
				 *	S(t)
				 */

				// Rewrite as to.call(e1)
				e = new TypeExp(loc, to);
				e = new DotIdExp(loc, e, new IdentifierExp(loc, Id.call));
				e = new CallExp(loc, e, e1);
				e = e.semantic(sc, context);
				return e;
			}
		}
		return e1.castTo(sc, to, context);
	}

	@Override
	public Expression syntaxCopy() {
		return new CastExp(loc, e1.syntaxCopy(), to.syntaxCopy());
	}
	
	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		buf.writestring("cast(");
	    to.toCBuffer(buf, null, hgs);
	    buf.writeByte(')');
	    expToCBuffer(buf, hgs, e1, op.precedence, context);
	}

}
