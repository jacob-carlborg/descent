package descent.core.dom;

import java.util.ArrayList;
import java.util.List;

/**
 * D doc comment AST node.
 */
public class DDocComment extends Comment {
	


	static {
		List properyList = new ArrayList(2);
		createPropertyList(DDocComment.class, properyList);
	}



	/**
	 * The text.
	 */
	private String text;


	/**
	 * Creates a new unparented d doc comment node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	DDocComment(AST ast) {
		super(ast);
	}



	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final int getNodeType0() {
		return D_DOC_COMMENT;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		DDocComment result = new DDocComment(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setKind(getKind());
		result.setText(getText());
		return result;
	}



	/**
	 * Returns the text of this d doc comment.
	 * 
	 * @return the text
	 */ 
	public String getText() {
		return this.text;
	}

	/**
	 * Sets the text of this d doc comment.
	 * 
	 * @param text the text
	 * @exception IllegalArgumentException if the argument is incorrect
	 */ 
	public void setText(String text) {
		if (text == null) {
			throw new IllegalArgumentException();
		}
		this.text = text;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		return BASE_NODE_SIZE + 2 * 4;
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
