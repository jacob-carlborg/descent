package util.tree;


/**
 * Sets parent entries in the tree nodes, using TreeWalker. 
 */
public class TreeParentizer extends TreeWalker {
	
	private static TreeParentizer singleton = new TreeParentizer();
	
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

