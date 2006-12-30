package dtool.dom.ast;

import java.util.ArrayList;
import java.util.List;

import dtool.dom.base.ASTElement;
import dtool.dom.base.ASTNode;
import dtool.dom.base.DefUnit;
import dtool.dom.base.Def_Modifiers;
import dtool.dom.base.Definition;
import dtool.dom.base.Entity;
import dtool.dom.base.EntitySingle;

/**
 * Uses a Visitor to determine a node's children
 */
public class TreeChildrenCollector extends ASTNeoVisitor {
	
	private boolean visitingParent = true;
	private List<ASTNode> childrenLst;
	
	public static List<ASTNode> getChildrenList(ASTNode elem){
		TreeChildrenCollector collector = new TreeChildrenCollector();
		collector.childrenLst = new ArrayList<ASTNode>();
		elem.accept(collector);
		return collector.childrenLst;
	}
	
	public static ASTNode[] getChildrenArray(ASTNode elem){
		return getChildrenList(elem).toArray(new ASTNode[0]);
	}	
	

	public boolean visit(ASTNode elem) {
		if(visitingParent == true) {
			visitingParent = false;
			return true; // visit children
		}

		// visiting children
		childrenLst.add(elem);
		return false;
	}

}

