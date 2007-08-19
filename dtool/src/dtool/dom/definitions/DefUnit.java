package dtool.dom.definitions;

import java.util.List;

import descent.internal.compiler.parser.Comment;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.IdentifierExp;
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
		Function,
		Alias,
		Typedef,
		Template,
		Mixin,
		Tuple,   
		;
	}
	
	public List<Comment> preComments;
	public Symbol defname;
	public EArcheType archeType;
	
	public DefUnit(Dsymbol elem) {
		convertDsymbol(elem, false);
	}
	
	public DefUnit(IdentifierExp id) {
		convertIdentifier(id);
	}

	public DefUnit(Symbol defname) {
		this.defname = defname;
	}
	
	protected void convertDsymbol(Dsymbol elem, boolean checkRange) {
		convertNode(elem, checkRange);
		convertIdentifier(elem.ident);
		this.preComments = elem.preDdocs;
		if(elem.postDdoc != null)
			this.preComments.add(elem.postDdoc);
	}

	protected void convertIdentifier(IdentifierExp id) {
		this.defname = new DefSymbol(id, this);
	}		


	public String getName() {
		return defname.name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public String getCombinedDocComments() {
		if(preComments == null || preComments.size() == 0)
			return null;
		String str = preComments.get(0).toString();
		for (int i = 1; i < preComments.size(); i++) {
			str = str + "\n" + preComments.get(i).toString();
		}
		return str;
	}
	
	/** Returns signature-oriented String representation. */
	public String toStringFullSignature() {
		String str = getArcheType().toString() 
			+ "  " + getModuleScope() + "." + getName();
		//if(getMembersScope() != this)str += " : " + getMembersScope();
		return str;
	}
	
	/** Returns completion proposal oriented String representation. */
	public String toStringAsCodeCompletion() {
		return getName() + " - " + getModuleScope();
	}

	/** Gets the archtype (the kind) of this DefUnit. */
	public abstract EArcheType getArcheType() ;

	/** Gets the scope which contains the members of this DefUnit. 
	 * In the case of aggregate like DefUnits the members scope is contained
	 * in the DefUnit node, but on other cases the scope is somewhere else.
	 * May be null if the scope is not found. */
	public abstract IScopeNode getMembersScope();



}