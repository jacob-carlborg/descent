package dtool.dom.ast.tree;

import java.util.ArrayList;
import java.util.List;


/**
 * Uses a Visitor to collect a node's children.
 */
public class TreeChildrenCollector extends TreeVisitor {
	
	private boolean visitingParent = true;
	private List<TreeNode> childrenLst;
	
	public static List<TreeNode> getChildrenList(TreeNode elem){
		TreeChildrenCollector collector = new TreeChildrenCollector();
		collector.childrenLst = new ArrayList<TreeNode>();
		collector.traverse(elem);
		return collector.childrenLst;
	}
	
	public static TreeNode[] getChildrenArray(TreeNode elem, TreeNode[] artype){
		return getChildrenList(elem).toArray(artype);
	}	
	
	public boolean enterNode(TreeNode elem) {
		if(visitingParent == true) {
			visitingParent = false;
			return true; // visit children
		}

		// visiting children
		childrenLst.add(elem);
		return false;
	}

	void leaveNode(TreeNode elem) {
		// Do nothing
	}
}
