package dtool.dom.definitions;

import util.Assert;
import descent.internal.core.dom.Dsymbol;
import descent.internal.core.dom.Identifier;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.model.IScope;

/**
 * Abstract class for all AST elements that define a new named entity.
 */
public abstract class DefUnit extends ASTNeoNode {
	
	/** A Symbol is node wrapping an identifier, used only in DefUnits names.*/
	public static class Symbol extends ASTNeoNode {
		public String name;

		public Symbol(Identifier id) {
			Assert.isTrue(id.getClass() == Identifier.class);
			setSourceRange(id);
			this.name = id.string;
		}

		@Override
		public boolean equals(Object obj) {
			return this.name.equals(obj);
		}

		@Override
		public void accept0(IASTNeoVisitor visitor) {
			visitor.visit(this);
			visitor.endVisit(this);
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	static public enum EArcheType {
		Module,
		Variable,
		Parameter,
		Function,
		Alias,
		Typedef,
		Enum,
		Aggregate, 
		Template,
		Mixin,
		Tuple,
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
	
	public Symbol defname;
	public EArcheType archeType;

	protected void convertDsymbol(Dsymbol elem) {
		convertNode(elem);
		convertIdentifier(elem.ident);
	}

	protected void convertIdentifier(Identifier id) {
		this.defname = new Symbol(id);
	}		

	/** Gets the archtype (the kind) of this DefUnit. */
	public abstract EArcheType getArcheType() ;

	/** Gets the scope which contains the members of this DefUnit. 
	 * In the case of aggregate like DefUnits the members scope is contained
	 * in the DefUnit node, but on other cases the scope is somewhere else.
	 * May be null if the scope is not found. */
	public abstract IScope getMembersScope();

	
	@Override
	public String toString() {
		return defname.name;
	}


}