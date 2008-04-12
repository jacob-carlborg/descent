package descent.internal.compiler.lookup;

import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;

public class SemanticRest {
	
	private Scope sc;
	private LINK linkage;
	private SemanticContext context;
	private boolean consumed;
	private boolean structureKnown;
	private final Runnable runnable;
	
	public SemanticRest(Runnable runnable) {
		this.runnable = runnable;
	}

	public void setSemanticContext(Scope scope, SemanticContext context) {
		if (scope != null) {
			this.sc = scope;
			this.linkage = scope.linkage;
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
		if (sc == null) {
			return;
		}
		
		this.consumed = true;
		
		if (!structureKnown) {
			buildStructure();
		}
		
		LINK linkage_save = sc.linkage;
		sc.linkage = linkage;
		
		symbol.semantic(sc, context);
		
		sc.linkage = linkage_save;
	}
	

}
