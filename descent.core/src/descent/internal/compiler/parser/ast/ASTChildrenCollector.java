package descent.internal.compiler.parser.ast;

import java.util.ArrayList;
import java.util.List;

import descent.internal.compiler.parser.INode;



/**
 * Uses a Visitor to collect a node's children.
 */
public class ASTChildrenCollector extends ASTHomoVisitor {
	
	private boolean visitingParent = true;
	private List<INode> childrenLst;
	
	public static List<INode> getChildrenList(ASTNode elem){
		ASTChildrenCollector collector = new ASTChildrenCollector();
		collector.childrenLst = new ArrayList<INode>();
		collector.traverse(elem);
		return collector.childrenLst;
	}
	
	public static INode[] getChildrenArray(ASTNode elem){
		return getChildrenList(elem).toArray(ASTNode.NO_ELEMENTS);
	}	
	
	public boolean enterNode(INode elem) {
		if(visitingParent == true) {
			visitingParent = false;
			return true; // visit children
		}

		// visiting children
		childrenLst.add(elem);
		return false;
	}

	protected void leaveNode(INode elem) {
		// Do nothing
	}
}
