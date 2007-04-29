package dtool.dom.ast;

import java.util.ArrayList;
import java.util.List;

import util.tree.TreeNode;


/**
 * Uses a Visitor to collect a node's children.
 */
public class ASTChildrenCollector extends ASTHomoVisitor {
	
	private boolean visitingParent = true;
	private List<ASTNode> childrenLst;
	
	public static List<ASTNode> getChildrenList(TreeNode elem){
		ASTChildrenCollector collector = new ASTChildrenCollector();
		collector.childrenLst = new ArrayList<ASTNode>();
		collector.traverse(elem);
		return collector.childrenLst;
	}
	
	public static ASTNode[] getChildrenArray(TreeNode elem){
		return getChildrenList(elem).toArray(ASTNode.NO_ELEMENTS);
	}	
	
	public boolean enterNode(TreeNode elem) {
		if(visitingParent == true) {
			visitingParent = false;
			return true; // visit children
		}

		// visiting children
		childrenLst.add((ASTNode)elem);
		return false;
	}

	protected void leaveNode(TreeNode elem) {
		// Do nothing
	}
}
