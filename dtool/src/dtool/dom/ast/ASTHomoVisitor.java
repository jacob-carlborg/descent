package dtool.dom.ast;

import util.tree.TreeNode;

/** 
 * An abstract visitor that visits nodes in a homogenous way, 
 * i.e., without any type-specific methods. Uses the accept0 mechanism and
 * not getChildren().
 */
public abstract class ASTHomoVisitor extends ASTNeoUpTreeVisitor {

	@SuppressWarnings("unchecked")
	public void traverse(TreeNode elem) {
		elem.accept(this);
	}
	
	abstract boolean enterNode(TreeNode elem);
	abstract void leaveNode(TreeNode elem);
	
	public final void preVisit(ASTNode elem) {
	}

	public final void postVisit(ASTNode elem) {
	}
	
	public final boolean visit(ASTNode elem) {
		return enterNode((TreeNode)elem);
	}

	public final void endVisit(ASTNode elem) {
		leaveNode(elem);
	}
}