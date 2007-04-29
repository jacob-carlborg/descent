package descent.internal.compiler.parser;

import descent.core.compiler.IProblem;


public class StaticAssert extends Dsymbol {
	
	public Expression exp;
	public Expression msg;

	public StaticAssert(Loc loc, Expression exp, Expression msg) {
		super(loc);
		this.exp = exp;
		this.msg = msg;		
	}
	
	@Override
	public int addMember(Scope sc, ScopeDsymbol sd, int memnum, SemanticContext context) {
		return 0; // we didn't add anything
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		
	}
	
	@Override
	public void semantic2(Scope sc, SemanticContext context) {
		Expression e;

		e = exp.semantic(sc, context);
		e = e.optimize(Expression.WANTvalue);
		if (e.isBool(false)) {
			context.acceptProblem(Problem.newSemanticTypeError("The expression statically evaluates to false", IProblem.StaticAssertIsFalse, 0, exp.start, exp.length));
			/* TODO see if appear "msg" in the error
			if (msg != null) {
				msg = msg.semantic(sc, context);
				msg = msg.optimize(Expression.WANTvalue);
				String p = msg.toChars();
				error("(%s) is false, %s", exp.toChars(), p);
			} else {
				error("(%s) is false", exp.toChars());
			}
			*/
			/*
			 * TODO semantic if (!global.gag) fatal();
			 */
		} else if (!e.isBool(true)) {
			error("(%s) is not evaluatable at compile time", exp.toChars());
		}
	}
	
	@Override
	public int getNodeType() {
		return STATIC_ASSERT;
	}

}
