package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class StaticIfCondition extends Condition {

	public Expression exp, sourceExp;

	public StaticIfCondition(Loc loc, Expression exp) {
		super(loc);
		this.exp = this.sourceExp = exp;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceExp);
		}
		visitor.endVisit(this);
	}

	@Override
	public int getConditionType() {
		return STATIC_IF;
	}

	@Override
	public boolean include(Scope sc, ScopeDsymbol s, SemanticContext context) {
		if (inc == 0) {
			if (null == sc) {
				if (context.acceptsProblems()) {
					context.acceptProblem(Problem.newSemanticTypeError(IProblem.StaticIfConditionalCannotBeAtGlobalScope, this));
				}
				inc = 2;
				return false;
			}

			sc = sc.push(sc.scopesym);
			sc.sd = s; // s gets any addMember()
			sc.flags |= Scope.SCOPEstaticif;
			Expression e = exp.semantic(sc, context);
			sc.pop();
			e = e.optimize(WANTvalue | WANTinterpret, context);
			if (e.isBool(true)) {
				inc = 1;
			} else if (e.isBool(false)) {
				inc = 2;
			} else {
				if (context.acceptsProblems()) {
					context.acceptProblem(Problem.newSemanticTypeError(IProblem.ExpressionIsNotConstantOrDoesNotEvaluateToABool, exp, new String[] { e.toChars(context) }));
				}
				inc = 2;
			}
		}
		return (inc == 1);
	}

	@Override
	public Condition syntaxCopy(SemanticContext context) {
		return new StaticIfCondition(loc, exp.syntaxCopy(context));
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("static if(");
		exp.toCBuffer(buf, hgs, context);
		buf.writeByte(')');
	}

}
