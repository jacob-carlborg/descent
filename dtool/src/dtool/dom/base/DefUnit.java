package dtool.dom.base;

import dtool.dom.ast.ASTNeoVisitor;
import dtool.model.IScope;

/**
 * Abstract class for all AST elements that define a new symbol.
 */
public abstract class DefUnit extends ASTNeoNode {
	
	public Symbol name; // XXX fixme the accept0
	public EArcheType archeType;
	
	public DefUnit() {
	}

	public DefUnit(String name) {
		this.name = new Symbol(name);
	}
	
	public static class Symbol extends ASTNeoNode {
		public String name;

		public Symbol(String name) {
			this.name = name;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof String)
				return name.equals(obj);
			else
				return super.equals(obj);
		}

		@Override
		public void accept0(ASTNeoVisitor visitor) {
			visitor.visit(this);
			visitor.endVisit(this);
		}
	}
	
	static public enum EArcheType {
		Module,
		Variable,
		Parameter,
		Function,
		Alias,
		Typedef,
		Aggregate
		;
		
		public String toString(EArcheType archetype) {
			switch(archetype) {
			case Module: return "Module";
			case Variable: return "Variable";
			case Parameter: return "Parameter";
			case Function: return "Function";
			case Alias: return "Alias";
			case Typedef: return "Typedef";
			case Aggregate: return "Aggregate";
			default: assert(false); return null;
			}
		}
	}
	
	public abstract EArcheType getArcheType() ;

	public abstract IScope getScope() ;

	
	@Override
	public String toString() {
		return super.toString() + " => " + name;
	}
}
