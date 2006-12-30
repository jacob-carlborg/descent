package dtool.dom.ast;

import descent.core.domX.ASTVisitor;
import dtool.dom.base.ASTElement;
import dtool.dom.base.ASTNode;
import dtool.dom.base.DefUnit;
import dtool.dom.base.Definition;
import dtool.dom.base.Entity;
import dtool.dom.base.EntitySingle;
import dtool.dom.base.Module;


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

	public boolean visit(DefUnit elem) {
		return visitAsSuperType(elem, DefUnit.class);
	}

	public boolean visit(Entity elem) {
		return visitAsSuperType(elem, Entity.class);
	}

	public boolean visit(Entity.QualifiedEnt elem) {
		return visitAsSuperType(elem, Entity.QualifiedEnt.class);
	}
	
	public boolean visit(EntitySingle elem) {
		return visitAsSuperType(elem, EntitySingle.class);
	}
	
	public boolean visit(EntitySingle.Identifier elem) {
		return visitAsSuperType(elem, EntitySingle.Identifier.class);
	}

	public boolean visit(EntitySingle.TemplateInstance elem) {
		return visitAsSuperType(elem, EntitySingle.TemplateInstance.class);
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
