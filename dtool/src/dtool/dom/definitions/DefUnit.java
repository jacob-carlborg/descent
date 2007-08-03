package dtool.dom.definitions;

import descent.internal.core.dom.Dsymbol;
import descent.internal.core.dom.Identifier;
import dtool.dom.ast.ASTNeoNode;
import dtool.refmodel.IScopeNode;

/**
 * Abstract class for all AST elements that define a new named entity.
 */
public abstract class DefUnit extends ASTNeoNode {
	

	static public enum EArcheType {
		Module,
		Package,
		Native,
		Aggregate, 
		Enum,
		EnumMember, // same as var?
		Variable,
		Parameter,
		Function,
		Alias,
		Typedef,
		Template,
		Mixin,
		Tuple,   
		;
	}
	
	public String comments;
	public Symbol defname;
	public EArcheType archeType;

	protected void convertDsymbol(Dsymbol elem) {
		convertNode(elem);
		convertIdentifier(elem.ident);
		//TODO: The parser is not parsing comments
		this.comments = elem.comments; 
	}

	protected void convertIdentifier(Identifier id) {
		this.defname = new DefSymbol(id, this);
	}		


	public String getName() {
		return defname.name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	/** Returns signature-oriented String representation. */
	public String toStringFullSignature() {
		String str = getArcheType().toString() 
			+ "  " + getModule().toStringFullSignature() + "."	+ getName();
		//if(getMembersScope() != this)str += " : " + getMembersScope();
		return str;
	}
	
	/** Returns completion proposal oriented String representation. */
	public String toStringAsCodeCompletion() {
		return getName() + " - " + getModule();
	}

	/** Gets the archtype (the kind) of this DefUnit. */
	public abstract EArcheType getArcheType() ;

	/** Gets the scope which contains the members of this DefUnit. 
	 * In the case of aggregate like DefUnits the members scope is contained
	 * in the DefUnit node, but on other cases the scope is somewhere else.
	 * May be null if the scope is not found. */
	public abstract IScopeNode getMembersScope();

}