package dtool.dom.ast;

import melnorme.miscutil.tree.IVisitable;

/** 
 * An abstract visitor that visits nodes in a homogeneous way, 
 * i.e., without any type-specific methods. Uses the accept0 mechanism and
 * not getChildren().
 */
public abstract class ASTNeoHomoVisitor extends ASTNeoUpTreeVisitor {

	public <T extends IASTNeoVisitor> void traverse(IVisitable<? super IASTNeoVisitor> elem) {
		elem.accept(this);
	}
	
	public void preVisit(ASTNeoNode elem) {
	}

	public void postVisit(ASTNeoNode elem) {
	}
	
	abstract boolean enterNode(ASTNeoNode elem);
	abstract void leaveNode(ASTNeoNode elem);


	public final boolean visit(ASTNeoNode elem) {
		return enterNode(elem);
	}

	public final void endVisit(ASTNeoNode elem) {
		leaveNode(elem);
	}

}