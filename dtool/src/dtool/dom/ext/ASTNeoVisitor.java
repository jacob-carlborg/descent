package dtool.dom.ext;

import descent.core.domX.ASTNode;
import descent.core.domX.ASTVisitor;
import dtool.dom.ASTElement;
import dtool.dom.Definition;
import dtool.dom.EntityReference;
import dtool.dom.Module;
import dtool.dom.SingleEntityRef;
import dtool.dom.SymbolDef;


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

	public boolean visit(EntityReference elem) {
		return visitAsSuperType(elem, EntityReference.class);
	}
	
	public boolean visit(SingleEntityRef elem) {
		return visitAsSuperType(elem, SingleEntityRef.class);
	}
	
	public boolean visit(SingleEntityRef.Identifier elem) {
		return visitAsSuperType(elem, SingleEntityRef.Identifier.class);
	}

	public boolean visit(SingleEntityRef.TemplateInstance elem) {
		return visitAsSuperType(elem, SingleEntityRef.TemplateInstance.class);
	}

	public boolean visit(SingleEntityRef.TypeDynArray elem) {
		return visitAsSuperType(elem, SingleEntityRef.TypeDynArray.class);
	}
	
	/* ---------------------------------- */

	public boolean visit(Definition elem) {
		return visitAsSuperType(elem, Definition.class);
	}
	
	
	/* ---------------------------------- */

	public boolean visit(Module elem) {
		return visitAsSuperType(elem, Module.class);
	}

}
