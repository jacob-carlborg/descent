package dtool.dom.ast;

import util.tree.ITreeNode;


/**
 * Sets parent entries in the tree nodes, using homogenous Visitor. 
 */
public class ASTNodeParentizer extends ASTHomoVisitor {
	
	private static ASTNodeParentizer singleton = new ASTNodeParentizer();
	
	private ASTNode parent = null;
	private boolean firstvisit = true;
	
	private void initialize() {
		parent = null;
		firstvisit = true;
	}
	
	public static void parentize(ASTNode elem){
		singleton.initialize();
		singleton.traverse(elem);
	}

	protected void leaveNode(ITreeNode elem) {
		parent = (ASTNode)elem.getParent(); // Restore parent
	}

	@SuppressWarnings("unchecked")
	protected boolean enterNode(ITreeNode elem) {
		if (firstvisit) {
			firstvisit = false;
		} else {
			((ASTNode)elem).setParent(parent); // Set parent to current parent
		}
		parent = (ASTNode)elem; // Set as new parent
		return true; 
	}

}

