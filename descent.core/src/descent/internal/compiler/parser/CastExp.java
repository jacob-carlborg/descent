package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.Constfold.Cast;
import static descent.internal.compiler.parser.TOK.TOKarrayliteral;
import static descent.internal.compiler.parser.TOK.TOKcall;
import static descent.internal.compiler.parser.TOK.TOKnull;
import static descent.internal.compiler.parser.TOK.TOKstring;
import static descent.internal.compiler.parser.TOK.TOKsymoff;
import static descent.internal.compiler.parser.TOK.TOKvar;

import static descent.internal.compiler.parser.TY.Tarray;
import static descent.internal.compiler.parser.TY.Tclass;
import static descent.internal.compiler.parser.TY.Tpointer;
import static descent.internal.compiler.parser.TY.Tsarray;
import static descent.internal.compiler.parser.TY.Tstruct;
import static descent.internal.compiler.parser.TY.Tvoid;


public class CastExp extends UnaExp {

	public Type to, sourceTo;
	public TOK tok;
	public int modifierStart;

	public CastExp(Loc loc, Expression e1, TOK tok, int modifierStart) {
		super(loc, TOK.TOKcast, e1);
		this.modifierStart = modifierStart;
		this.to = null;
		this.tok = tok;
	}

	public CastExp(Loc loc, Expression e1, Type t) {
		super(loc, TOK.TOKcast, e1);
		this.to = this.sourceTo = t;
		this.tok = null;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceTo);
			TreeVisitor.acceptChildren(visitor, sourceE1);
		}
		visitor.endVisit(this);
	}

	@Override
	public void checkEscape(SemanticContext context) {
		Type tb = type.toBasetype(context);
		if (tb.ty == Tarray && e1.op == TOKvar
				&& e1.type.toBasetype(context).ty == Tsarray) {
			VarExp ve = (VarExp) e1;
			VarDeclaration v = ve.var.isVarDeclaration();
			if (v != null) {
				if (!v.isDataseg(context) && !v.isParameter()) {
					if (context.acceptsProblems()) {
						context.acceptProblem(Problem.newSemanticTypeError(
								IProblem.EscapingReferenceToLocal, this, v.toChars(context)));
					}
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
	public Expression interpret(InterState istate, SemanticContext context) {
		// Expression e;
		Expression e1;

		e1 = this.e1.interpret(istate, context);
		if (e1 == EXP_CANT_INTERPRET) {
			// goto Lcant;
			return EXP_CANT_INTERPRET;
		}
		return Cast(type, to, e1, context);
	}

	@Override
	public char[] opId(SemanticContext context) {
		return Id.cast;
	}

	@Override
	public Expression optimize(int result, SemanticContext context) {
		if (type == null) {
			// SEMANTIC 
			// throw new IllegalStateException("assert(type);");
			return e1;
		}
		TOK op1 = e1.op;

		e1 = e1.optimize(result, context);
		if ((result & WANTinterpret) != 0) {
			e1 = fromConstInitializer(e1, context);
		}

		if ((e1.op == TOKstring || e1.op == TOKarrayliteral)
				&& (type.ty == Tpointer || type.ty == Tarray)
				&& type.next.equals(e1.type.next)) {
			e1.type = type;
			return e1;
		}
		/* The first test here is to prevent infinite loops
		 */
		if (op1 != TOKarrayliteral && e1.op == TOKarrayliteral) {
			return e1.castTo(null, to, context);
		}
		if (e1.op == TOKnull && (type.ty == Tpointer || type.ty == Tclass)) {
			e1.type = type;
			return e1;
		}

		if ((result & WANTflags) != 0 && type.ty == Tclass
				&& e1.type.ty == Tclass) {
			// See if we can remove an unnecessary cast
			ClassDeclaration cdfrom;
			ClassDeclaration cdto;
			int[] offset = { 0 };

			cdfrom = e1.type.isClassHandle();
			cdto = type.isClassHandle();
			if (cdto.isBaseOf(cdfrom, offset, context) && offset[0] == 0) {
				e1.type = type;
				return e1;
			}
		}

		Expression e;

		if (e1.isConst()) {
			if (e1.op == TOKsymoff) {
				if (type.size(context) == e1.type.size(context)
						&& type.toBasetype(context).ty != Tsarray) {
					e1.type = type;
					return e1;
				}
				return this;
			}
			if (to.toBasetype(context).ty == Tvoid) {
				e = this;
			} else {
				e = Cast(type, to, e1, context);
			}
		} else {
			e = this;
		}
		return e;
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

			e = op_overload(sc, context);
			if (e != null) {
				return e.implicitCastTo(sc, to, context);
			}

			Type tob = to.toBasetype(context);
			if (tob.ty == Tstruct && 
				!tob.equals(e1.type.toBasetype(context)) &&
			    null == ((TypeStruct)to).sym.search(Loc.ZERO, Id.call, 0, context)
			    ) 
			{
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
		e = e1.castTo(sc, to, context);
		return e;
	}

	@Override
	public Expression syntaxCopy(SemanticContext context) {
		return new CastExp(loc, e1.syntaxCopy(context), to.syntaxCopy(context));
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("cast(");
		to.toCBuffer(buf, null, hgs, context);
		buf.writeByte(')');
		expToCBuffer(buf, hgs, e1, op.precedence, context);
	}

}
