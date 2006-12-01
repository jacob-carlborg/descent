package dtool.dom;



public abstract class Symbol extends ASTElement {
	
	public String name;
	//public EntArcheType archeType;
	
	public Symbol() {
	}

	public Symbol(String name) {
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
