package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.CompoundStatement;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Statement;
import descent.internal.compiler.parser.Statements;

public class CompletionOnCompoundStatement extends CompoundStatement {
	
	public Scope scope;

	public CompletionOnCompoundStatement(Loc loc, Statements statements) {
		super(loc, statements);
	}
	
	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		Statement statement = super.semantic(sc, context);
		
		this.scope = ScopeCopy.copy(sc, context);
		
		return statement;
	}
	
	@Override
	public Statements flatten(Scope sc, SemanticContext context) {
		Statements statements = super.flatten(sc, context);
		
		this.scope = ScopeCopy.copy(sc, context);
		
		return statements;
	}

}
