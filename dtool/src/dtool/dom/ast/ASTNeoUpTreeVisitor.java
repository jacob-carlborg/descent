package dtool.dom.ast;

import util.Assert;
import descent.core.domX.ASTUpTreeVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Definition;
import dtool.dom.definitions.Module;
import dtool.dom.definitions.Symbol;
import dtool.dom.references.EntIdentifier;
import dtool.dom.references.EntQualified;
import dtool.dom.references.EntTemplateInstance;
import dtool.dom.references.Entity;
import dtool.dom.references.EntitySingle;

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

	public boolean visit(Symbol elem) {
		Assert.isTrue(DefUnit.class.getSuperclass().equals(ASTNeoNode.class));
		return visit((ASTNeoNode) elem);
	}
	

	public boolean visit(Entity elem) {
		return visitAsSuperType(elem, Entity.class);
	}

	public boolean visit(EntQualified elem) {
		return visitAsSuperType(elem, EntQualified.class);
	}

	public boolean visit(EntitySingle elem) {
		return visitAsSuperType(elem, EntitySingle.class);
	}

	public boolean visit(EntIdentifier elem) {
		return visitAsSuperType(elem, EntIdentifier.class);
	}

	public boolean visit(EntTemplateInstance elem) {
		return visitAsSuperType(elem, EntTemplateInstance.class);
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
