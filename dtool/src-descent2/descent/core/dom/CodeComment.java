package descent.core.dom;

import java.util.ArrayList;
import java.util.List;

/**
 * Code comment AST node.
 */
public class CodeComment extends Comment {
	



	static {
		List properyList = new ArrayList(1);
		createPropertyList(CodeComment.class, properyList);

	}




	/**
	 * Creates a new unparented code comment node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	CodeComment(AST ast) {
		super(ast);
	}


		
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final int getNodeType0() {
		return CODE_COMMENT;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		CodeComment result = new CodeComment(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setKind(getKind());
		return result;
	}



	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		return BASE_NODE_SIZE + 1 * 4;
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
