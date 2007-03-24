package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.Expression.*;

public class DoStatement extends Statement {
	
	public Expression condition;
	public Statement body;
	
	public DoStatement(Statement b, Expression c) {
		this.condition = c;
		this.body = b;
	}
	
	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		sc.noctor++;
	    body = body.semanticScope(sc, this, this, context);
	    sc.noctor--;
	    condition = condition.semantic(sc, context);
	    condition = resolveProperties(sc, condition, context);

	    condition = condition.checkToBoolean(context);

	    return this;
	}
	
	@Override
	public int getNodeType() {
		return DO_STATEMENT;
	}

}
