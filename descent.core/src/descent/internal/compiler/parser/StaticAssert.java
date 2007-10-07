package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class StaticAssert extends Dsymbol {

	public Expression exp;
	public Expression msg;

	public StaticAssert(Loc loc, Expression exp, Expression msg) {
		super(IdentifierExp.EMPTY);
		this.loc = loc;
		this.exp = exp;
		this.msg = msg;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, msg);
		}
		visitor.endVisit(this);
	}

	@Override
	public int addMember(Scope sc, ScopeDsymbol sd, int memnum,
			SemanticContext context) {
		return 0; // we didn't add anything
	}

	@Override
	public int getNodeType() {
		return STATIC_ASSERT;
	}

	@Override
	public void inlineScan(SemanticContext context) {
		// empty
	}

	@Override
	public String kind() {
		return "static assert";
	}

	@Override
	public boolean oneMember(Dsymbol[] ps, SemanticContext context) {
		ps[0] = null;
		return true;
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		// empty
	}

	@Override
	public void semantic2(Scope sc, SemanticContext context) {
		Expression e;

		e = exp.semantic(sc, context);
		e = e.optimize(WANTvalue | WANTinterpret, context);
		if (e.isBool(false)) {
			if (msg != null) {
				HdrGenState hgs = new HdrGenState();
				OutBuffer buf = new OutBuffer();

				msg = msg.semantic(sc, context);
				msg = msg.optimize(WANTvalue | WANTinterpret, context);
				hgs.console = 1;
				msg.toCBuffer(buf, hgs, context);
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.AssertionFailed, 0, exp.start, exp.length,
						new String[] { buf.toChars() }));
			} else {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.AssertionFailedNoMessage, 0, exp.start,
						exp.length, new String[] { exp.toChars(context) }));
			}
			if (context.global.gag == 0) {
				fatal();
			}
		} else if (!e.isBool(true)) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.ExpressionIsNotEvaluatableAtCompileTime, 0,
					exp.start, exp.length,
					new String[] { exp.toChars(context) }));
		}
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		StaticAssert sa;

		if (s != null) {
			throw new IllegalStateException("assert(!s);");
		}
		sa = new StaticAssert(loc, exp.syntaxCopy(), msg != null ? msg
				.syntaxCopy() : null);
		return sa;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring(kind());
		buf.writeByte('(');
		exp.toCBuffer(buf, hgs, context);
		if (msg != null) {
			buf.writeByte(',');
			msg.toCBuffer(buf, hgs, context);
		}
		buf.writestring(");");
		buf.writenl();
	}

}
