package dtool.dom.ast;

import dtool.dom.base.ASTNode;


/**
 * Sets AST parent entries. 
 */
public class ASTParentizer extends ASTNeoVisitor {
	
	private static ASTParentizer singleton = new ASTParentizer();
	
	ASTNode parent = null;
	boolean firstvisit = true;
	
	private void initialize() {
		parent = null;
		firstvisit = true;
	}
	
	public static void parentize(ASTNode elem){
		singleton.initialize();
		elem.accept(singleton);
	}

	public boolean visit(ASTNode elem) {
		if (firstvisit) {
			firstvisit = false;
		} else {
			elem.parent = parent; // Set parent to current parent
		}
		parent = elem; // Set as new parent
		return true;
	}

	public void endVisit(ASTNode elem) {
		parent = elem.parent; // Restore parent
	}

}

