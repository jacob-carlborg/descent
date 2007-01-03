package dtool.dom.ast.tree;

import dtool.dom.base.ASTNode;


/**
 * Sets AST parent entries. 
 */
public class TreeParentizer extends TreeVisitor {
	
	private static TreeParentizer singleton = new TreeParentizer();
	
	private TreeNode parent = null;
	private boolean firstvisit = true;
	
	private void initialize() {
		parent = null;
		firstvisit = true;
	}
	
	public static void parentize(ASTNode elem){
		singleton.initialize();
		elem.accept(singleton);
	}

	public void leaveNode(TreeNode elem) {
		parent = elem.getParent(); // Restore parent
	}

	@SuppressWarnings("unchecked")
	boolean enterNode(TreeNode elem) {
		if (firstvisit) {
			firstvisit = false;
		} else {
			elem.parent = parent; // Set parent to current parent
		}
		parent = elem; // Set as new parent
		return true; 
	}

}

