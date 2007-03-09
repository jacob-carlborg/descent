package dtool.dom.ast;

import util.tree.TreeNode;
import dtool.dom.base.ASTNode;

/** 
 * An abstract visitor that visits nodes in a homogenous way, 
 * i.e., without any type-specific methods. Uses the accept0 mechanism and
 * not getChildren().
 */
public abstract class ASTHomoVisitor extends ASTNeoVisitor {

	@SuppressWarnings("unchecked")
	public void traverse(TreeNode elem) {
		elem.accept(this);
	}
	
	abstract boolean enterNode(TreeNode elem);
	abstract void leaveNode(TreeNode elem);
	
	public final void preVisit(ASTNode elem) {
		enterNode((TreeNode)elem);
	}

	public final void postVisit(ASTNode elem) {
		leaveNode((TreeNode)elem);
	}

}