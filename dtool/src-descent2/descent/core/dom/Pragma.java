package descent.core.dom;

import java.util.ArrayList;
import java.util.List;

/**
 * Pragma AST node. Pragmas are parts of the source code that begin with 
 * the <b>#</b> character and last until the end of the line.
 * 
 * <pre>
 * Pragma:
 *    <b>#</b> <i>text</i>
 * </pre>
 */
public class Pragma extends ASTNode {
	




	/**
	 * Creates a new unparented pragma node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	Pragma(AST ast) {
		super(ast);
	}


	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final int getNodeType0() {
		return PRAGMA;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		Pragma result = new Pragma(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		return result;
	}


	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		return BASE_NODE_SIZE + 0 * 4;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return
			memSize()
	;
	}

}
