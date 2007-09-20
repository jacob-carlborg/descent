package dtool.ast.definitions;

import static melnorme.miscutil.Assert.assertNotNull;
import descent.internal.compiler.parser.Comment;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.IdentifierExp;
import dtool.ast.ASTNeoNode;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeAdaptable;
import dtool.refmodel.IScopeNode;

/**
 * Abstract class for all AST elements that define a new named entity.
 */
public abstract class DefUnit extends ASTNeoNode implements	IScopeAdaptable {
	

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
	
	public /*final*/ Comment[] preComments;
	public final Symbol defname;
	public EArcheType archeType;
	
	public DefUnit(Dsymbol elem) {
		convertNode(elem, false);
		this.defname = new DefSymbol(elem.ident, this);
		int size = 0; 
		if(elem.preDdocs != null)
			size = elem.preDdocs.size();
		if(elem.postDdoc != null)
			size = size+1;
		
		if(size != 0)
			this.preComments = new Comment[size];
		
		if(elem.preDdocs != null) {
			for (int i = 0; i < elem.preDdocs.size(); i++) {
				this.preComments[i] = elem.preDdocs.get(i);
			}
		}
		if(elem.postDdoc != null)
			this.preComments[size-1] = elem.postDdoc;
	}

	public DefUnit(IdentifierExp id) {
		this.defname = new DefSymbol(id, this);
		this.preComments = null;
	}

	public DefUnit(Symbol defname) {
		assertNotNull(defname);
		this.defname = defname;
		this.preComments = null;
	}
	
	public String getName() {
		return defname.name;
	}
	
	
	public String getCombinedDocComments() {
		if(preComments == null || preComments.length == 0)
			return null;
		String str = new String(preComments[0].string);
		for (int i = 1; i < preComments.length; i++) {
			str = str + "\n" + preComments[i].toString();
		}
		return str;
	}

	/** Gets the archtype (the kind) of this DefUnit. */
	public abstract EArcheType getArcheType() ;

	/** Gets the scope which contains the members of this DefUnit. 
	 * In the case of aggregate like DefUnits the members scope is contained
	 * in the DefUnit node, but on other cases the scope is somewhere else.
	 * May be null if the scope is not found. */
	public abstract IScopeNode getMembersScope();

	//@Override
	public IScope getAdaptedScope() {
		return getMembersScope();
	}
	
	@Override
	public String toStringAsElement() {
		return getName();
	}
	
	/** Returns signature-oriented String representation. */
	public String toStringForHoverSignature() {
		String str = getArcheType().toString() 
			+ "  " + getModuleScope().toStringAsElement() + "." + getName();
		//if(getMembersScope() != this)str += " : " + getMembersScope();
		return str;
	}
	
	/** Returns completion proposal oriented String representation. */
	//public abstract String toStringForCodeCompletion() ;
	public String toStringForCodeCompletion() {
		return getName() + " - " + getModuleScope().toStringAsElement();
	}

}