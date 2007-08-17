package descent.internal.compiler.parser;

import java.util.List;

public abstract class Statement extends ASTDmdNode {
	
	public Loc loc;
	public boolean incontract;
	
	public Statement(Loc loc) {
		this.loc = loc;
	}
	
	public Statement semantic(Scope sc, SemanticContext context) {
		return this;
	}
	
	public List<Statement> flatten(Scope sc) {
		return null;
	}
	
	public void scopeCode(Statement[] sentry, Statement[] sexception, Statement[] sfinally) {
		sentry[0] = null;
	    sexception[0] = null;
	    sfinally[0] = null;
	}
	
	public Statement semanticScope(Scope sc, Statement sbreak, Statement scontinue, SemanticContext context) {
		Scope scd;
		Statement s;

		scd = sc.push();
		if (sbreak != null) {
			scd.sbreak = sbreak;
		}
		if (scontinue != null) {
			scd.scontinue = scontinue;
		}
		s = semantic(scd, context);
		scd.pop();
		return s;
	}
	
	public Statement syntaxCopy() {
		// TODO semantic
		return this;
	}
	
	public boolean fallOffEnd() {
		return true;
	}

}
