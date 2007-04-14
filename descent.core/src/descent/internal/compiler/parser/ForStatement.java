package descent.internal.compiler.parser;

import java.math.BigInteger;

public class ForStatement extends Statement {
	
	public Statement init;
	public Expression condition;
	public Expression increment;
	public Statement body;

	public ForStatement(Loc loc, Statement init, Expression condition, Expression increment, Statement body) {
		super(loc);
		this.init = init;
		this.condition = condition;
		this.increment = increment;
		this.body = body;		
	}
	
	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		ScopeDsymbol sym = new ScopeDsymbol(loc);
		sym.parent = sc.scopesym;
		sc = sc.push(sym);
		if (init != null) {
			init = init.semantic(sc, context);
		}
		if (condition == null) {
			// Use a default value
			condition = new IntegerExp(loc, "1", BigInteger.ONE, Type.tboolean);
		}
		sc.noctor++;
		condition = condition.semantic(sc, context);
		condition = resolveProperties(sc, condition, context);
		condition = condition.checkToBoolean(context);
		if (increment != null) {
			increment = increment.semantic(sc, context);
		}

		sc.sbreak = this;
		sc.scontinue = this;
		body = body.semantic(sc, context);
		sc.noctor--;

		sc.pop();
		return this;
	}
	
	@Override
	public int getNodeType() {
		return FOR_STATEMENT;
	}

}
