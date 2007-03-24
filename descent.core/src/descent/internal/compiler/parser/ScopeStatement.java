package descent.internal.compiler.parser;

import java.util.List;

public class ScopeStatement extends Statement {
	
	public Statement statement;

    public ScopeStatement(Statement s) {
    	this.statement = s;
    }
    
    @Override
    public Statement semantic(Scope sc, SemanticContext context) {
    	ScopeDsymbol sym;

		if (statement != null) {
			List<Statement> a;

			sym = new ScopeDsymbol();
			sym.parent = sc.scopesym;
			sc = sc.push(sym);

			a = statement.flatten(sc);
			if (a != null) {
				statement = new CompoundStatement(a);
			}

			statement = statement.semantic(sc, context);
			if (statement != null) {
				Statement[] sentry = { null };
				Statement[] sexception = { null };
				Statement[] sfinally = { null };

				statement.scopeCode(sentry, sexception, sfinally);
				if (sfinally[0] != null) {
					statement = new CompoundStatement(statement, sfinally[0]);
				}
			}

			sc.pop();
		}
		return this;
    }
    
    @Override
    public int getNodeType() {
    	return SCOPE_STATEMENT;
    }

}
