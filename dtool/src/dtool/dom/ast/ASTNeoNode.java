package dtool.dom.ast;

import melnorme.miscutil.Assert;
import descent.core.domX.IASTVisitor;
import dtool.refmodel.IScope;
import dtool.refmodel.NodeUtil;

public abstract class ASTNeoNode extends ASTNode  {


	public void convertNode(ASTNode elem) {
		setSourceRange(elem);
	}

	public int getElementType() {
		return 0; // TODO Not DMD element
	}
	

	/**
	 * Same as ASTNode.accept but makes sub-elements accept0 use ASTNeoVisitor.
	 * This is a temporary adapting solution.
	 */
	public final void accept(IASTVisitor visitor) {
		if (visitor == null) {
			throw new IllegalArgumentException();
		}
		// begin with the generic pre-visit
		visitor.preVisit(this);
		// dynamic dispatch to internal method for type-specific visit/endVisit
		this.accept0((IASTNeoVisitor) visitor);
		// end with the generic post-visit
		visitor.postVisit(this);
	}

	public final void accept0(IASTVisitor visitor) {
		Assert.fail("NEO AST elements should not use IASTVisitor");
		
	}

	// Neo AST elements use ASTNeoVisitor
	/* Template:
	boolean children = visitor.visit(this);
	if (children) {
		TreeVisitor.acceptChild(visitor, md);
		TreeVisitor.acceptChildren(visitor, members);
	}
	visitor.endVisit(this);
	*/
	public abstract void accept0(IASTNeoVisitor visitor);

	 


	/* ===================  Convertion utils  ====================== */
	
	/** Sets the source range the same as the given elem. */
	public void setSourceRange(ASTNode elem) {
		startPos = elem.getStartPos();
		length = elem.getLength();
	}

	public IScope getModuleScope() {
		return NodeUtil.getParentModule(this);
	}
	
}