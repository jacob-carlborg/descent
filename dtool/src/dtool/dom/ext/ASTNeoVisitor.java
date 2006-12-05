package dtool.dom.ext;

import descent.core.domX.ASTNode;
import descent.core.domX.ASTVisitor;
import dtool.dom.ASTElement;
import dtool.dom.Definition;
import dtool.dom.EntityReference;
import dtool.dom.Module;
import dtool.dom.SymbolDef;
import dtool.dom.SymbolReference;


/**
 * A visitor for the "neo AST", substitues the previous Visitor.
 */
public abstract class ASTNeoVisitor extends ASTVisitor {

	
	public void preVisit(ASTNode elem) {
		// Default implementation: do nothing
	}

	public void postVisit(ASTNode elem) {
		// Default implementation: do nothing
	}
	
	
	/**
	 * Visits the element.
	 * @param element the element to visit
	 * @return true if children element should be visited
	 */
	public boolean visit(ASTElement elem) {
		return visitAsSuperType(elem, ASTElement.class);
	}

	public boolean visit(SymbolDef elem) {
		return visitAsSuperType(elem, SymbolDef.class);
	}
	
	public boolean visit(SymbolReference elem) {
		return visitAsSuperType(elem, SymbolReference.class);
	}

	public boolean visit(EntityReference.AnyEntityReference elem) {
		return visitAsSuperType(elem, EntityReference.AnyEntityReference.class);
	}
	
	public boolean visit(Module elem) {
		return visitAsSuperType(elem, Module.class);
	}
	
	public boolean visit(Definition elem) {
		return visitAsSuperType(elem, Definition.class);
	}
	
	
	/* ---------------------------------- */


}
