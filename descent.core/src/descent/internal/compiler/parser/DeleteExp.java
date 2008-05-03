package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.TOK.TOKindex;

import static descent.internal.compiler.parser.TY.Taarray;
import static descent.internal.compiler.parser.TY.Tstruct;

// DMD 1.020
public class DeleteExp extends UnaExp {

	public DeleteExp(Loc loc, Expression e1) {
		super(loc, TOK.TOKdelete, e1);
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceE1);
		}
		visitor.endVisit(this);
	}

	@Override
	public int checkSideEffect(int flag, SemanticContext context) {
		return 1;
	}

	@Override
	public Expression checkToBoolean(SemanticContext context) {
		if (context.acceptsProblems()) {
			context
					.acceptProblem(Problem.newSemanticTypeError(
							IProblem.ExpressionDoesNotGiveABooleanResult, this));
		}
		return this;
	}

	@Override
	public int getNodeType() {
		return DELETE_EXP;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		Type tb;

		super.semantic(sc, context);
		e1 = resolveProperties(sc, e1, context);
		e1 = e1.toLvalue(sc, null, context);
		type = Type.tvoid;

		tb = e1.type.toBasetype(context);
		switch (tb.ty) {
		case Tclass: {
			TypeClass tc = (TypeClass) tb;
			ClassDeclaration cd = tc.sym;

			if (cd.isInterfaceDeclaration() != null && cd.isCOMclass()) {
				if (context.acceptsProblems()) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.CannotDeleteInstanceOfComInterface, this, new String[] { cd.toChars(context) }));
				}
			}
			break;
		}
		case Tpointer:
			tb = tb.next.toBasetype(context);
			if (tb.ty == Tstruct) {
				TypeStruct ts = (TypeStruct) tb;
				StructDeclaration sd = ts.sym;
				FuncDeclaration f = sd.aggDelete;

				if (f != null) {
					Expression e;
					Expression ec;
					Type tpv = Type.tvoid.pointerTo(context);

					e = e1;
					e.type = tpv;
					ec = new VarExp(loc, f);
					e = new CallExp(loc, ec, e);
					return e.semantic(sc, context);
				}
			}
			break;

		case Tarray:
			break;

		default:
			if (e1.op == TOKindex) {
				IndexExp ae = (IndexExp) (e1);
				Type tb1 = ae.e1.type.toBasetype(context);
				if (tb1.ty == Taarray) {
					break;
				}
			}
			if (context.acceptsProblems()) {
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.CannotDeleteType, this, new String[] { e1.type.toChars(context) }));
			}
			break;
		}

		if (e1.op == TOKindex) {
			IndexExp ae = (IndexExp) (e1);
			Type tb1 = ae.e1.type.toBasetype(context);
			if (tb1.ty == Taarray) {
				if (!context.global.params.useDeprecated) {
					if (context.acceptsProblems()) {
						context.acceptProblem(Problem.newSemanticTypeError(IProblem.DeleteAAKeyDeprecated, this));
					}
				}
			}
		}

		return this;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("delete ");
		expToCBuffer(buf, hgs, e1, op.precedence, context);
	}

}
