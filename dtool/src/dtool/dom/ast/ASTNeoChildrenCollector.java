package dtool.dom.ast;

import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.tree.IVisitable;



/**
 * Uses a Visitor to collect a node's children.
 */
public class ASTNeoChildrenCollector extends ASTNeoHomoVisitor {
	
	private boolean visitingParent = true;
	private List<ASTNeoNode> childrenLst;
	
	public static ASTNeoNode[] getChildrenArray(ASTNeoNode elem){
		return getChildrenList(elem).toArray(ASTNeoNode.NO_ELEMENTS);
	}	
	
	public static List<ASTNeoNode> getChildrenList(IVisitable<? super IASTNeoVisitor> elem){
		ASTNeoChildrenCollector collector = new ASTNeoChildrenCollector();
		collector.childrenLst = new ArrayList<ASTNeoNode>();
		collector.traverse(elem);
		return collector.childrenLst;
	}
	

	
	public boolean enterNode(ASTNeoNode elem) {
		if(visitingParent == true) {
			visitingParent = false;
			return true; // visit children
		}

		// visiting children
		childrenLst.add(elem);
		return false;
	}

	protected void leaveNode(ASTNeoNode elem) {
		// Do nothing
	}
}
