package dtool.dom;

/**
 * Abstract class for all AST elements that define a new symbol.
 */
public abstract class SymbolDef extends ASTElement {
	
	public String name;
	//public EntArcheType archeType;
	
	public SymbolDef() {
	}

	public SymbolDef(String name) {
		this.name = name;
	}
	
	static public enum ArcheType {
		Module,
		Variable,
		Parameter,
		Function,
		Alias,
		Typedef,
		Aggregate
		;
	}
	
	public abstract ArcheType getArcheType() ;
	
}
