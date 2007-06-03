package dtool.dom.ast;

import util.Assert;
import descent.core.domX.ASTUpTreeVisitor;
import dtool.dom.base.Entity;
import dtool.dom.base.EntitySingle;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Definition;
import dtool.dom.definitions.Module;

public abstract class ASTNeoUpTreeVisitor extends ASTUpTreeVisitor implements IASTNeoVisitor {

	
	/** Visits the element.
	 * @return true if children element should be visited
	 */
	public boolean visit(ASTNeoNode elem) {
		Assert.isTrue(ASTNeoNode.class.getSuperclass().equals(ASTNode.class));
		return visit((ASTNode) elem);
		// return visitAsSuperType(elem, ASTNeoNode.class);
	}

	public boolean visit(DefUnit elem) {
		Assert.isTrue(DefUnit.class.getSuperclass().equals(ASTNeoNode.class));
		return visit((ASTNeoNode) elem);
		//return visitAsSuperType(elem, DefUnit.class);
	}

	public boolean visit(DefUnit.Symbol elem) {
		Assert.isTrue(DefUnit.class.getSuperclass().equals(ASTNeoNode.class));
		return visit((ASTNeoNode) elem);
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
	
	
	/* ============================================= */

}
