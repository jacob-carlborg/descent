package descent.internal.compiler.lookup;

import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;

public class SemanticRest {
	
	private Scope sc;
	private SemanticContext context;
	private boolean consumed;
	private boolean structureKnown;
	private final Runnable runnable;
	
	public boolean skipScopeCheck;
	
	public SemanticRest(Runnable runnable) {
		this.runnable = runnable;
	}

	public void setSemanticContext(Scope scope, SemanticContext context) {
		if (scope != null) {
			this.sc = Scope.copy(scope);
		}
		this.context = context;
	}
	
	public boolean isConsumed() {
		return consumed;
	}
	
	public boolean isStructureKnown() {
		return structureKnown;
	}
	
	public Scope getScope() {
		return sc;
	}
	
	public void buildStructure() {
		structureKnown = true;
		runnable.run();
	}
	
	public void consume(Dsymbol symbol) {
		if (sc == null && !skipScopeCheck) {
			return;
		}
		
		this.consumed = true;
		
		if (!structureKnown) {
			buildStructure();
		}
		
		symbol.semantic(sc, context);
	}
	

}
