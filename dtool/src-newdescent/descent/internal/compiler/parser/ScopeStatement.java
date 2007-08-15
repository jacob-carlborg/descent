package descent.internal.compiler.parser;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.IASTVisitor;

public class ScopeStatement extends Statement {
	
	public Statement statement;

    public ScopeStatement(Loc loc, Statement s) {
    	super(loc);
    	this.statement = s;
    }
    
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, statement);
		}
		visitor.endVisit(this);
	}
    
    @Override
    public Statement semantic(Scope sc, SemanticContext context) {
    	ScopeDsymbol sym;

		if (statement != null) {
			List<Statement> a;

			sym = new ScopeDsymbol(loc);
			sym.parent = sc.scopesym;
			sc = sc.push(sym);

			a = statement.flatten(sc);
			if (a != null) {
				statement = new CompoundStatement(loc, a);
			}

			statement = statement.semantic(sc, context);
			if (statement != null) {
				Statement[] sentry = { null };
				Statement[] sexception = { null };
				Statement[] sfinally = { null };

				statement.scopeCode(sentry, sexception, sfinally);
				if (sfinally[0] != null) {
					statement = new CompoundStatement(loc, statement, sfinally[0]);
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
