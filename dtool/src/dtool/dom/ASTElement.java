package dtool.dom;

import descent.core.domX.ASTNode;
import descent.core.domX.ASTVisitor;
import dtool.dombase.ASTNeoVisitor;

public abstract class ASTElement extends ASTNode {

	public int getElementType() {
		return 0; // TODO Not DMD element
	}
	

	/**
	 * XXX: There is some temporary wizardry going on here...
	 * Same as ASTNode.accept but makes sub-elements accept0 use ASTNeoVisitor
	 */
	public final void accept(ASTVisitor visitor) {
		if (visitor == null) {
			throw new IllegalArgumentException();
		}
		// begin with the generic pre-visit
		visitor.preVisit(this);
		// dynamic dispatch to internal method for type-specific visit/endVisit
		this.accept0((ASTNeoVisitor) visitor);
		// end with the generic post-visit
		visitor.postVisit(this);
	}

	public final void accept0(ASTVisitor visitor) {
		// Neo AST elements cannot use ASTVisitor
		assert false;
	}

	// Neo AST elements use ASTNeoVisitor
	public abstract void accept0(ASTNeoVisitor visitor);
	
}