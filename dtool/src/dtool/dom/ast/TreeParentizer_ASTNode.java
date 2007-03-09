package dtool.dom.ast;

import util.tree.TreeNode;


/**
 * Sets parent entries in the tree nodes, using homogenous Visitor. 
 */
public class TreeParentizer_ASTNode extends ASTHomoVisitor {
	
	private static TreeParentizer_ASTNode singleton = new TreeParentizer_ASTNode();
	
	private TreeNode parent = null;
	private boolean firstvisit = true;
	
	private void initialize() {
		parent = null;
		firstvisit = true;
	}
	
	public static void parentize(TreeNode elem){
		singleton.initialize();
		singleton.traverse(elem);
	}

	protected void leaveNode(TreeNode elem) {
		parent = elem.getParent(); // Restore parent
	}

	@SuppressWarnings("unchecked")
	protected boolean enterNode(TreeNode elem) {
		if (firstvisit) {
			firstvisit = false;
		} else {
			elem.setParent(parent); // Set parent to current parent
		}
		parent = elem; // Set as new parent
		return true; 
	}

}

