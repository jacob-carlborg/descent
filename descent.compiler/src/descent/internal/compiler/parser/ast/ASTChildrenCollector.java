package descent.internal.compiler.parser.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Uses a Visitor to collect a node's children.
 */
public class ASTChildrenCollector extends ASTHomoVisitor {
	
	private boolean visitingParent = true;
	private List<ASTNode> childrenLst;
	
	public static List<ASTNode> getChildrenList(ASTNode elem){
		ASTChildrenCollector collector = new ASTChildrenCollector();
		collector.childrenLst = new ArrayList<ASTNode>();
		collector.traverse(elem);
		return collector.childrenLst;
	}
	
	public static ASTNode[] getChildrenArray(ASTNode elem){
		return getChildrenList(elem).toArray(ASTNode.NO_ELEMENTS);
	}	
	
	public boolean enterNode(ASTNode elem) {
		if(visitingParent == true) {
			visitingParent = false;
			return true; // visit children
		}

		// visiting children
		childrenLst.add(elem);
		return false;
	}

	protected void leaveNode(ASTNode elem) {
		// Do nothing
	}
}
