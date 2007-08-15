package dtool.dom.definitions;

import java.util.List;

import descent.core.dom.DDocComment;
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
		Parameter,
		Function,
		Alias,
		Typedef,
		Template,
		Mixin,
		Tuple,   
		;
	}
	
	public List<DDocComment> preComments;
	public Symbol defname;
	public EArcheType archeType;

	protected void convertDsymbol(Dsymbol elem) {
		convertDsymbol(elem, false);
	}
	
	protected void convertDsymbol(Dsymbol elem, boolean checkRange) {
		convertNode(elem, checkRange);
		convertIdentifier(elem.ident);
		this.preComments = elem.preDdocs;
		// TODO: post comments
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
		String str = null;
		if(preComments != null)
			for (DDocComment preComment : preComments) {
				if(preComment != null)
					str = str + "\n" + preComment.toString();
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