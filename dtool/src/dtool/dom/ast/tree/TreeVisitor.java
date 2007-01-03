package dtool.dom.ast.tree;

import dtool.dom.ast.ASTNeoVisitor;
import dtool.dom.base.ASTNode;

/** 
 * A visitor adaptor that traverses the tree and works in a homogenous way, 
 * i.e., without any type-specific methods.
 */
public abstract class TreeVisitor extends ASTNeoVisitor {

	@SuppressWarnings("unchecked")
	public void traverse(TreeNode elem) {
		elem.accept(this);
	}
	
	abstract boolean enterNode(TreeNode elem);
	abstract void leaveNode(TreeNode elem);
	
	public final void preVisit(ASTNode elem) {
		//enterNode((TreeNode)elem);
	}

	public final void postVisit(ASTNode elem) {
		//leaveNode((TreeNode)elem);
	}
	
	public boolean visit(ASTNode elem) {
		return enterNode((TreeNode)elem);
	}
	
	public void endVisit(ASTNode elem) {
		leaveNode((TreeNode)elem);
	}
	

	
		
}