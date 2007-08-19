package dtool.dom.ast;

import descent.internal.compiler.parser.ast.ASTNode;


/**
 * Sets parent entries in the tree nodes, using homogenous Visitor.
 * Assumes a neo AST. 
 */
public class ASTNodeParentizer extends ASTNeoHomoVisitor {

	public static void parentize(ASTNeoNode elem){
		elem.accept(new ASTNodeParentizer());
	}	
	
	private ASTNode parent = null;
	private boolean firstvisit = true;
		
	@Override
	public void preVisit(ASTNode elem) {
		if (firstvisit) {
			firstvisit = false;
		} else {
			elem.setParent(parent); // Set parent to current parent
		}
		parent = elem; // Set as new parent
	}
	
	@Override
	public void postVisit(ASTNode elem) {
		parent = elem.getParent(); // Restore previous parent
	}

	protected boolean enterNode(ASTNode elem) {
		return true;
	}

	protected void leaveNode(ASTNode elem) {
	}

}

