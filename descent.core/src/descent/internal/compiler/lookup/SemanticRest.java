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
	
	private static int nest = 0;
	public void consume(Dsymbol symbol) {
		if (sc == null && !skipScopeCheck) {
			return;
		}
		
		this.consumed = true;
		
		long buildStructureTime = -1;
		
		if (!structureKnown) {
			long time = System.currentTimeMillis();
			
			buildStructure();
			
			buildStructureTime = System.currentTimeMillis() - time;
		}
		
		long time = System.currentTimeMillis();
		
		nest++;
		
		symbol.semantic(sc, context);
		
		nest--;
		
		time = System.currentTimeMillis() - time;
		
		if (buildStructureTime > 10) {
			for (int i = 0; i < nest; i++) {
				System.out.print("  ");
			}
			System.out.println("SemanticRest#buildStructure(" + symbol.ident + ") = " + buildStructureTime);
		}
		
		if (time > 10) {
			for (int i = 0; i < nest; i++) {
				System.out.print("  ");
			}
			System.out.println("SemanticRest#semantic(" + symbol.getModule().moduleName + "." + symbol.ident + ") = " + time);
		}
	}
	

}
